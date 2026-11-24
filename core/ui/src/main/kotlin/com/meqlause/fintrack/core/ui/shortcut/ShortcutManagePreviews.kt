package com.meqlause.fintrack.core.ui.shortcut

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.component.PickerDrawer
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.QuickEntryId
import com.meqlause.fintrack.core.ui.R
import com.meqlause.fintrack.core.ui.UiPreviewCanvas

private const val PARKIR_RUPIAH = 4_500L
private const val KOPI_RUPIAH = 25_000L
private const val MAKAN_RUPIAH = 35_000L
private const val MAKAN_ID = 3L

@Composable
internal fun ShortcutManageSample(items: List<ShortcutTileParams> = sampleShortcuts()) {
    PickerDrawer(title = stringResource(R.string.shortcut_manage_title), onClose = {}) {
        ShortcutManageList(
            items = items,
            actions = ShortcutManageActions(onDelete = {}, onNew = {}, onEdit = {}),
        )
    }
}

private fun sampleShortcuts(): List<ShortcutTileParams> =
    listOf(
        ShortcutTileParams(
            quickEntryId = QuickEntryId(1L),
            title = "Parkir",
            amount = Money(PARKIR_RUPIAH),
            walletName = "Tunai",
            appearance = Appearance("transport", "blue"),
        ),
        ShortcutTileParams(
            quickEntryId = QuickEntryId(2L),
            title = "Kopi pagi",
            amount = Money(KOPI_RUPIAH),
            walletName = "GoPay",
            appearance = Appearance("food", "orange"),
        ),
        ShortcutTileParams(
            quickEntryId = QuickEntryId(MAKAN_ID),
            title = "Makan siang",
            amount = Money(MAKAN_RUPIAH),
            walletName = "BCA",
            appearance = Appearance("food", "orange"),
        ),
    )

@Preview(name = "ShortcutManage light", showBackground = true)
@Composable
internal fun ShortcutManageLightPreview() {
    UiPreviewCanvas(darkTheme = false) { ShortcutManageSample() }
}

@Preview(name = "ShortcutManage dark", showBackground = true)
@Composable
internal fun ShortcutManageDarkPreview() {
    UiPreviewCanvas(darkTheme = true) { ShortcutManageSample() }
}

@Preview(name = "ShortcutManage empty", showBackground = true)
@Composable
internal fun ShortcutManageEmptyPreview() {
    UiPreviewCanvas(darkTheme = false) { ShortcutManageSample(items = emptyList()) }
}
