package com.meqlause.fintrack.feature.transaction.shortcut

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.QuickEntryId
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.feature.transaction.edit.sampleCatalog

// FR-039, S-03: a half-filled expense shortcut on BCA, as the owner would see "Pintasan baru"
internal fun shortcutFormSample(): ShortcutFormUiState =
    ShortcutFormUiState(
        draft =
            ShortcutDraft(
                name = "Kopi pagi",
                rupiahDigits = "25000",
                categoryId = CategoryId(1L),
                walletId = WalletId(1L),
                note = "tanpa gula",
            ),
        catalog = sampleCatalog(),
    )

// FR-039: the same shortcut opened for editing
internal fun shortcutFormEditSample(): ShortcutFormUiState =
    shortcutFormSample().withStatus { copy(editedId = QuickEntryId(1L)) }

@Preview(name = "Pintasan baru terang", showBackground = true)
@Composable
internal fun ShortcutFormLightPreview(state: ShortcutFormUiState = shortcutFormSample()) {
    FintrackTheme(darkTheme = false) { ShortcutFormScreen(state = state, onEvent = {}, onDismiss = {}) }
}

@Preview(name = "Pintasan baru gelap", showBackground = true)
@Composable
internal fun ShortcutFormDarkPreview(state: ShortcutFormUiState = shortcutFormSample()) {
    FintrackTheme(darkTheme = true) { ShortcutFormScreen(state = state, onEvent = {}, onDismiss = {}) }
}
