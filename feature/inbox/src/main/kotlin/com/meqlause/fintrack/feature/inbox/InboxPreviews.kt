package com.meqlause.fintrack.feature.inbox

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.ui.picker.PickerOption

// S-07: sample rows for the screenshot previews; the amounts and labels follow the spec copy
// design/screens/inbox.png: the prototype inbox seed
private val sampleCards =
    listOf(
        InboxCardUi(1L, "BCA", "09:42", Money(25_000L), TransactionType.EXPENSE, "INDOMARET KEMANG", 1L, "BCA", 2L, "Belanja"),
        InboxCardUi(2L, "GoPay", "09:15", Money(32_000L), TransactionType.EXPENSE, "KOPI KENANGAN", 2L, "GoPay", 1L, "Makanan"),
        InboxCardUi(3L, "DANA", "08:03", Money(150_000L), TransactionType.INCOME, "Transfer masuk dari XXXX", 3L, "DANA", 3L, "Lainnya"),
    )

private val samplePair =
    InboxPairUi(
        fromItemId = 3L,
        toItemId = 4L,
        fromWalletId = 1L,
        fromWalletName = "BCA",
        toWalletId = 5L,
        toWalletName = "GoPay",
        amount = Money(100_000L),
    )

private val sampleOptions = listOf(PickerOption("1", "BCA"), PickerOption("5", "GoPay"))

private val sampleState =
    InboxUiState(
        isLoaded = true,
        hasAccess = true,
        cards = sampleCards,
        pairs = listOf(samplePair),
        resolved = emptyList(),
        walletOptions = sampleOptions,
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
