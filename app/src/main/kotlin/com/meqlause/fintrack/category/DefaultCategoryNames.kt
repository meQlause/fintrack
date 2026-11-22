package com.meqlause.fintrack.category

import android.content.Context
import com.meqlause.fintrack.R
import com.meqlause.fintrack.core.domain.category.DefaultCategory

/** FR-004, DOC-02: the Indonesian display names of the default categories, read from string resources. */
class DefaultCategoryNames(
    private val context: Context,
) {
    /** FR-004: the name shown for [category] when it is seeded on first launch. */
    fun nameOf(category: DefaultCategory): String = context.getString(resourceOf(category))

    private fun resourceOf(category: DefaultCategory): Int =
        when (category) {
            DefaultCategory.FOOD_AND_DRINK -> R.string.category_food_and_drink
            DefaultCategory.TRANSPORT -> R.string.category_transport
            DefaultCategory.SHOPPING -> R.string.category_shopping
            DefaultCategory.BILL -> R.string.category_bill
            DefaultCategory.ENTERTAINMENT -> R.string.category_entertainment
            DefaultCategory.HEALTH -> R.string.category_health
            DefaultCategory.EDUCATION -> R.string.category_education
            DefaultCategory.HOME -> R.string.category_home
            DefaultCategory.PHONE_AND_INTERNET -> R.string.category_phone_and_internet
            DefaultCategory.SOCIAL_AND_EVENTS -> R.string.category_social_and_events
            DefaultCategory.DONATION_AND_ZAKAT -> R.string.category_donation_and_zakat
            DefaultCategory.FEES_AND_ADMIN -> R.string.category_fees_and_admin
            DefaultCategory.OTHER_EXPENSE -> R.string.category_other_expense
            DefaultCategory.SALARY -> R.string.category_salary
            DefaultCategory.BONUS -> R.string.category_bonus
            DefaultCategory.BUSINESS -> R.string.category_business
            DefaultCategory.GIFT_INCOME -> R.string.category_gift_income
            DefaultCategory.OTHER_INCOME -> R.string.category_other_income
        }
}
