package com.meqlause.fintrack.core.testing.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

// ADR-007: rows held in a MutableStateFlow; ids grow like SQLite AUTOINCREMENT and are never reused
internal class RowStore<T : Any>(
    private val idOf: (T) -> Long,
    private val withId: (T, Long) -> T,
    private val order: Comparator<T>,
) {
    private val rows = MutableStateFlow<List<T>>(emptyList())
    private var lastId = 0L

    fun observe(): Flow<List<T>> = rows.map { list -> list.sortedWith(order.thenBy(idOf)) }

    fun all(): List<T> = rows.value

    // DOC-02: a row that conflicts with an existing one is rejected, as the unique index rejects it
    fun append(
        record: T,
        conflictsWith: (T) -> Boolean = { false },
    ): Long {
        require(rows.value.none(conflictsWith)) { "unique constraint violated" }
        lastId += 1
        rows.update { it + withId(record, lastId) }
        return lastId
    }

    // FR-021: replaces the row with the same id; an unknown id is ignored
    fun replace(record: T) {
        rows.update { list -> list.map { if (idOf(it) == idOf(record)) record else it } }
    }

    fun remove(rowId: Long) {
        rows.update { list -> list.filterNot { idOf(it) == rowId } }
    }
}
