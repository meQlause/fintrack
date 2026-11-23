package com.meqlause.fintrack.feature.category

import com.meqlause.fintrack.core.designsystem.icon.FintrackIcons
import com.meqlause.fintrack.core.domain.category.DefaultCategory

// DOC-08, FR-021: the icon and colour keys the editor offers, taken from the default category catalogue
internal object CategoryAppearance {
    const val DEFAULT_ICON = "other"
    const val DEFAULT_COLOR = "gray"

    val iconKeys: List<String> = FintrackIcons.categoryKeys
    val colorKeys: List<String> = DefaultCategory.entries.map { it.colorKey }.distinct()
}
