package com.meqlause.fintrack.feature.transaction.edit

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.QuickEntry
import com.meqlause.fintrack.core.model.QuickEntryId
import com.meqlause.fintrack.core.model.QuickTemplate
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.WalletId
import org.junit.Test
import java.time.Instant

// FR-039, BR-047: a shortcut fills the draft in editable form; only a new expense or income can become one
class EditShortcutMappingTest {
    private val fixedNow = Instant.parse("2026-11-23T10:00:00Z")

    @Test
    fun `a shortcut fills the form with its type, amount, wallet, category and title`() {
        val form = entry().toForm()

        assertThat(form.type).isEqualTo(TransactionType.EXPENSE)
        assertThat(form.rupiahDigits).isEqualTo("25000")
        assertThat(form.walletId).isEqualTo(WalletId(2L))
        assertThat(form.categoryId).isEqualTo(CategoryId(3L))
        assertThat(form.note).isEqualTo("Kopi")
    }

    @Test
    fun `a kept checkbox on a new expense builds the shortcut with the trimmed note as title`() {
        val state =
            EditTransactionUiState(
                form =
                    TransactionForm(
                        type = TransactionType.EXPENSE,
                        rupiahDigits = "25000",
                        walletId = WalletId(2L),
                        categoryId = CategoryId(3L),
                        note = "  Kopi  ",
                    ),
                keepAsShortcut = true,
            )

        val shortcut = state.toShortcut(fixedNow)

        assertThat(shortcut?.template?.title).isEqualTo("Kopi")
        assertThat(shortcut?.template?.amount).isEqualTo(Money(25_000L))
    }

    @Test
    fun `no shortcut is built when the checkbox is off or a shortcut was applied`() {
        val form = TransactionForm(walletId = WalletId(2L), categoryId = CategoryId(3L))
        val base = EditTransactionUiState(form = form)

        assertThat(base.toShortcut(fixedNow)).isNull()
        assertThat(base.copy(keepAsShortcut = true, appliedShortcut = true).toShortcut(fixedNow)).isNull()
    }

    private fun entry(): QuickEntry =
        QuickEntry(
            quickEntryId = QuickEntryId(1L),
            template = QuickTemplate(title = "Kopi", type = TransactionType.EXPENSE, amount = Money(25_000L)),
            categoryId = CategoryId(3L),
            walletId = WalletId(2L),
            sortOrder = 0,
            audit = Audit(createdAt = fixedNow, updatedAt = fixedNow),
        )
}
