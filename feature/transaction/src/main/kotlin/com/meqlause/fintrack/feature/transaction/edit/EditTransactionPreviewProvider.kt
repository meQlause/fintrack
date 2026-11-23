package com.meqlause.fintrack.feature.transaction.edit

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.CategoryKind
import com.meqlause.fintrack.core.model.CategoryProfile
import com.meqlause.fintrack.core.model.CategoryState
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletKind
import com.meqlause.fintrack.core.model.WalletProfile
import com.meqlause.fintrack.core.model.WalletStatus
import com.meqlause.fintrack.core.model.WalletTerms
import com.meqlause.fintrack.core.model.WalletType
import java.time.Instant

// FR-030..FR-033, S-04: previews of the sheet in light and dark themes, for an expense and a transfer
internal class EditTransactionPreviewProvider : PreviewParameterProvider<EditTransactionUiState> {
    override val values: Sequence<EditTransactionUiState> = sequenceOf(expenseSample(), transferSample())
}

@Preview(name = "Catat transaksi terang", showBackground = true)
@Composable
internal fun EditTransactionLightPreview(
    @PreviewParameter(EditTransactionPreviewProvider::class) state: EditTransactionUiState,
) {
    FintrackTheme(darkTheme = false) {
        EditTransactionScreen(state = state, onEvent = {}, onDismiss = {})
    }
}

@Preview(name = "Catat transaksi gelap", showBackground = true)
@Composable
internal fun EditTransactionDarkPreview(
    @PreviewParameter(EditTransactionPreviewProvider::class) state: EditTransactionUiState,
) {
    FintrackTheme(darkTheme = true) {
        EditTransactionScreen(state = state, onEvent = {}, onDismiss = {})
    }
}

// S-04: the mockup's empty expense: Rp0 with Makan picked and BCA as the paying wallet
internal fun expenseSample(): EditTransactionUiState =
    EditTransactionUiState(
        form =
            TransactionForm(
                type = TransactionType.EXPENSE,
                rupiahDigits = "",
                route = FormRoute(walletId = WalletId(1L), categoryId = CategoryId(1L)),
            ),
        catalog = sampleCatalog(),
        record = EditRecord(existing = null, occurredAt = Instant.EPOCH, dateLabel = "Hari ini, 14:05"),
    )

// S-04: a transfer from BCA to GoPay
internal fun transferSample(): EditTransactionUiState =
    EditTransactionUiState(
        form =
            TransactionForm(
                type = TransactionType.TRANSFER,
                rupiahDigits = "",
                route = FormRoute(walletId = WalletId(1L), targetWalletId = WalletId(2L)),
            ),
        catalog = sampleCatalog(),
        record = EditRecord(existing = null, occurredAt = Instant.EPOCH, dateLabel = "Hari ini, 14:05"),
    )

// S-04: the Pinjaman tab with nobody named yet, lent from BCA
internal fun loanSample(): EditTransactionUiState = expenseSample().withExtras { copy(loan = LoanForm()) }

internal fun sampleCatalog(): EditCatalog =
    EditCatalog(
        wallets =
            listOf(
                sampleWallet(1L, "BCA", WalletType.BANK, Appearance("bank", "blue")),
                sampleWallet(2L, "GoPay", WalletType.EWALLET, Appearance("ewallet", "teal")),
            ),
        moneyByWallet =
            mapOf(
                WalletId(value = 1L) to Money(rupiah = 10_000_000L),
                WalletId(value = 2L) to Money(rupiah = 222_000L),
            ),
        categories = sampleCategories(),
    )

// S-04: the sample categories, six of expense and one of income
internal fun sampleCategories(): List<Category> =
    listOf(
        sampleCategory(categoryKey = 1L, name = "Makan", CategoryKind.EXPENSE, Appearance("food", "orange")),
        sampleCategory(
            categoryKey = 3L,
            name = "Transport",
            CategoryKind.EXPENSE,
            Appearance("transport", "blue"),
        ),
        sampleCategory(
            categoryKey = 4L,
            name = "Belanja",
            CategoryKind.EXPENSE,
            Appearance("shopping", "pink"),
        ),
        sampleCategory(categoryKey = 5L, name = "Tagihan", CategoryKind.EXPENSE, Appearance("bill", "purple")),
        sampleCategory(
            categoryKey = 6L,
            name = "Hiburan",
            CategoryKind.EXPENSE,
            Appearance("entertainment", "indigo"),
        ),
        sampleCategory(categoryKey = 7L, name = "Kesehatan", CategoryKind.EXPENSE, Appearance("health", "red")),
        sampleCategory(categoryKey = 2L, name = "Gaji", CategoryKind.INCOME, Appearance("salary", "green")),
    )

private fun sampleWallet(
    walletKey: Long,
    name: String,
    type: WalletType,
    appearance: Appearance,
): Wallet =
    Wallet(
        walletId = WalletId(walletKey),
        kind = WalletKind(type, null),
        terms = WalletTerms(Money(0L), null),
        profile = WalletProfile(name, appearance, walletKey.toInt()),
        status = WalletStatus(),
        audit = Audit(Instant.EPOCH, Instant.EPOCH),
    )

private fun sampleCategory(
    categoryKey: Long,
    name: String,
    kind: CategoryKind,
    appearance: Appearance,
): Category =
    Category(
        categoryId = CategoryId(categoryKey),
        profile = CategoryProfile(name, kind, appearance, categoryKey.toInt()),
        state = CategoryState(isDefault = true, isArchived = false),
        audit = Audit(Instant.EPOCH, Instant.EPOCH),
    )
