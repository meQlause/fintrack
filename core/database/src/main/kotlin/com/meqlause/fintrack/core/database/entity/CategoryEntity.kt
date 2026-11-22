package com.meqlause.fintrack.core.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.meqlause.fintrack.core.model.CategoryKind

/** DOC-02: an expense or income category; referenced categories cannot be deleted (BR-008). */
@Entity(tableName = "category")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val categoryId: Long = 0,
    @Embedded val identity: Identity,
    @Embedded val appearance: AppearanceColumns,
    @Embedded val state: State = State(),
    @Embedded val audit: AuditColumns,
) {
    /** DOC-02: name and kind; the kind is immutable after creation (FR-004). */
    data class Identity(
        val name: String,
        val kind: CategoryKind,
    )

    /** DOC-02: seeded flag, archive state and manual order. */
    data class State(
        val isDefault: Boolean = false,
        val isArchived: Boolean = false,
        val sortOrder: Int = 0,
    )
}
