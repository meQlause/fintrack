package com.meqlause.fintrack.feature.inbox

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.ui.picker.PickerOption

private const val BELANJA_AMOUNT = 25_000L
private const val KOPI_AMOUNT = 32_000L
private const val TRANSFER_IN_AMOUNT = 150_000L
private const val PAIR_AMOUNT = 100_000L

// S-07: sample rows for the screenshot previews; the amounts and labels follow the spec copy
// design/screens/inbox.png: the prototype inbox seed
private val sampleCards =
    listOf(
        InboxCardUi(
            itemId = 1L,
            text = InboxCardText(sourceLabel = "BCA", timeLabel = "09:42", merchant = "INDOMARET KEMANG"),
            amount = Money(BELANJA_AMOUNT),
            type = TransactionType.EXPENSE,
            wallet = InboxSlot(slotId = 1L, name = "BCA"),
            category = InboxSlot(slotId = 2L, name = "Belanja"),
        ),
        InboxCardUi(
            itemId = 2L,
            text = InboxCardText(sourceLabel = "GoPay", timeLabel = "09:15", merchant = "KOPI KENANGAN"),
            amount = Money(KOPI_AMOUNT),
            type = TransactionType.EXPENSE,
            wallet = InboxSlot(slotId = 2L, name = "GoPay"),
            category = InboxSlot(slotId = 1L, name = "Makanan"),
        ),
        InboxCardUi(
            itemId = 3L,
            text = InboxCardText(sourceLabel = "DANA", timeLabel = "08:03", merchant = "Transfer masuk dari XXXX"),
            amount = Money(TRANSFER_IN_AMOUNT),
            type = TransactionType.INCOME,
            wallet = InboxSlot(slotId = 3L, name = "DANA"),
            category = InboxSlot(slotId = 3L, name = "Lainnya"),
        ),
    )

private val samplePair =
    InboxPairUi(
        fromItemId = 3L,
        toItemId = 4L,
        from = InboxSlot(slotId = 1L, name = "BCA"),
        target = InboxSlot(slotId = 5L, name = "GoPay"),
        amount = Money(PAIR_AMOUNT),
    )

private val sampleOptions = listOf(PickerOption("1", "BCA"), PickerOption("5", "GoPay"))

private val sampleState =
    InboxUiState(
        isLoaded = true,
        hasAccess = true,
        pending = InboxPending(cards = sampleCards, pairs = listOf(samplePair)),
        resolved = emptyList(),
        options = InboxOptions(wallets = sampleOptions),
    )

@Preview(name = "Kotak masuk light", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
internal fun InboxLightPreview() {
    InboxPreviewFrame(darkTheme = false, state = sampleState)
}

@Preview(name = "Kotak masuk dark", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
internal fun InboxDarkPreview() {
    InboxPreviewFrame(darkTheme = true, state = sampleState)
}

@Preview(name = "Kotak masuk kosong", showBackground = true, heightDp = 480)
@Composable
internal fun InboxEmptyPreview() {
    InboxPreviewFrame(darkTheme = false, state = InboxUiState(isLoaded = true))
}

@Preview(name = "Kotak masuk akses mati", showBackground = true, heightDp = 480)
@Composable
internal fun InboxAccessOffPreview() {
    InboxPreviewFrame(darkTheme = false, state = InboxUiState(isLoaded = true, hasAccess = false))
}

@Composable
private fun InboxPreviewFrame(
    darkTheme: Boolean,
    state: InboxUiState,
) {
    FintrackTheme(darkTheme = darkTheme) {
        Surface(color = FintrackTheme.colors.surfaces.background) {
            InboxScreen(state = state, actions = InboxActions(), showInfo = false)
        }
    }
}

// NFR-010: font scale 2.0 variant for the screenshot tests
@Preview(name = "Kotak masuk font 200", showBackground = true, heightDp = 1200, fontScale = 2f)
@Composable
internal fun InboxFontScalePreview() {
    InboxPreviewFrame(darkTheme = false, state = sampleState)
}
