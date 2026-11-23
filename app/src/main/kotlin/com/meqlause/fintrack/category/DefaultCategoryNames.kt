package com.meqlause.fintrack.category

import android.content.Context
import com.meqlause.fintrack.R
import com.meqlause.fintrack.core.domain.category.DefaultCategory

/** FR-004, DOC-02: the Indonesian display names of the default categories, read from string resources. */
class DefaultCategoryNames(
    private val context: Context,
) {
    /** FR-004: the name shown for [category] when it is seeded on first launch. */
    fun nameOf(category: DefaultCategory): String = context.getString(RESOURCE_IDS.getValue(category))

    private companion object {
        val RESOURCE_IDS: Map<DefaultCategory, Int> =
            mapOf(
                DefaultCategory.FOOD_AND_DRINK to R.string.category_food_and_drink,
                DefaultCategory.TRANSPORT to R.string.category_transport,
                DefaultCategory.SHOPPING to R.string.category_shopping,
                DefaultCategory.BILL to R.string.category_bill,
                DefaultCategory.ENTERTAINMENT to R.string.category_entertainment,
                DefaultCategory.HEALTH to R.string.category_health,
                DefaultCategory.EDUCATION to R.string.category_education,
                DefaultCategory.HOME to R.string.category_home,
                DefaultCategory.PHONE_AND_INTERNET to R.string.category_phone_and_internet,
                DefaultCategory.SOCIAL_AND_EVENTS to R.string.category_social_and_events,
                DefaultCategory.DONATION_AND_ZAKAT to R.string.category_donation_and_zakat,
                DefaultCategory.FEES_AND_ADMIN to R.string.category_fees_and_admin,
                DefaultCategory.OTHER_EXPENSE to R.string.category_other_expense,
                DefaultCategory.SALARY to R.string.category_salary,
                DefaultCategory.BONUS to R.string.category_bonus,
                DefaultCategory.BUSINESS to R.string.category_business,
                DefaultCategory.GIFT_INCOME to R.string.category_gift_income,
                DefaultCategory.OTHER_INCOME to R.string.category_other_income,
            )
    }
}
