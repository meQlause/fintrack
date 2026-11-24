package com.meqlause.fintrack.feature.transaction.edit

import com.meqlause.fintrack.core.designsystem.icon.IconLook
import com.meqlause.fintrack.core.domain.category.CategoryError
import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.ui.picker.PickerOption
import com.meqlause.fintrack.feature.transaction.R

// S-04: one drawer row per category, keyed by its id and wearing its own icon and colour
internal fun Category.toOption(): PickerOption =
    PickerOption(
        optionKey = categoryId.value.toString(),
        label = profile.name,
        look = IconLook(iconKey = profile.appearance.iconKey, colorKey = profile.appearance.colorKey),
    )

// FR-021: the Indonesian message of each name rule a new category can break
internal fun CategoryError.messageRes(): Int =
    when (this) {
        CategoryError.NAME_REQUIRED -> R.string.transaction_category_error_required
        CategoryError.NAME_TOO_LONG -> R.string.transaction_category_error_long
        CategoryError.NAME_TAKEN -> R.string.transaction_category_error_taken
    }
