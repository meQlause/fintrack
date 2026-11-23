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

// S-04: an expense of Rp25.000 paid from BCA into the Makan category
internal fun expenseSample(): EditTransactionUiState =
    EditTransactionUiState(
        form =
            TransactionForm(
                type = TransactionType.EXPENSE,
                rupiahDigits = "25000",
                walletId = WalletId(1L),
                categoryId = CategoryId(1L),
            ),
        catalog = sampleCatalog(),
        record = EditRecord(existing = null, occurredAt = Instant.EPOCH, dateLabel = "Hari ini, 14:05"),
    )

// S-04: a transfer from BCA to the credit card, which is allowed as the target
internal fun transferSample(): EditTransactionUiState =
    EditTransactionUiState(
        form =
            TransactionForm(
                type = TransactionType.TRANSFER,
                rupiahDigits = "500000",
                walletId = WalletId(1L),
                targetWalletId = WalletId(2L),
            ),
        catalog = sampleCatalog(),
        record = EditRecord(existing = null, occurredAt = Instant.EPOCH, dateLabel = "Hari ini, 14:05"),
    )

internal fun sampleCatalog(): EditCatalog =
    EditCatalog(
        wallets =
            listOf(
                sampleWallet(1L, "BCA", WalletType.BANK),
                sampleWallet(2L, "Kartu BCA", WalletType.CREDIT_CARD),
            ),
        moneyByWallet = mapOf(WalletId(1L) to Money(1_250_000L)),
        categories =
            listOf(
                sampleCategory(1L, "Makan", CategoryKind.EXPENSE),
                sampleCategory(2L, "Gaji", CategoryKind.INCOME),
            ),
    )

private fun sampleWallet(
    walletKey: Long,
    name: String,
    type: WalletType,
): Wallet =
    Wallet(
        walletId = WalletId(walletKey),
        kind = WalletKind(type, null),
        terms = WalletTerms(Money(0L), null),
        profile = WalletProfile(name, Appearance("wallet", "teal"), walletKey.toInt()),
        status = WalletStatus(),
        audit = Audit(Instant.EPOCH, Instant.EPOCH),
    )

private fun sampleCategory(
    categoryKey: Long,
    name: String,
    kind: CategoryKind,
): Category =
    Category(
        categoryId = CategoryId(categoryKey),
        profile = CategoryProfile(name, kind, Appearance("tag", "teal"), categoryKey.toInt()),
        state = CategoryState(isDefault = true, isArchived = false),
        audit = Audit(Instant.EPOCH, Instant.EPOCH),
    )
