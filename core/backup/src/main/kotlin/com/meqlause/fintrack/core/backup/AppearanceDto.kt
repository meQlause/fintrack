package com.meqlause.fintrack.core.backup

import com.meqlause.fintrack.core.model.Appearance
import kotlinx.serialization.Serializable

// DOC-11: icon and colour keys of a wallet or category
@Serializable
internal data class AppearanceDto(
    val iconKey: String,
    val colorKey: String,
) {
    fun toModel(): Appearance = Appearance(iconKey = iconKey, colorKey = colorKey)

    companion object {
        fun from(appearance: Appearance): AppearanceDto = AppearanceDto(appearance.iconKey, appearance.colorKey)
    }
}
