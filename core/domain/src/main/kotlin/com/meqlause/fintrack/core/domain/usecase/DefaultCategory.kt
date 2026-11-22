package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.model.CategoryKind
import com.meqlause.fintrack.core.model.CategoryKind.EXPENSE
import com.meqlause.fintrack.core.model.CategoryKind.INCOME

/**
 * DOC-02, FR-004: the seeded categories with their kind and catalog keys.
 * Declaration order is the manual sort order; display names come from [DefaultCategoryNames].
 */
public enum class DefaultCategory(
    public val kind: CategoryKind,
    public val iconKey: String,
    public val colorKey: String,
) {
    FOOD_AND_DRINK(EXPENSE, "food", "orange"),
    TRANSPORT(EXPENSE, "transport", "blue"),
    SHOPPING(EXPENSE, "shopping", "pink"),
    BILL(EXPENSE, "bill", "purple"),
    ENTERTAINMENT(EXPENSE, "entertainment", "violet"),
    HEALTH(EXPENSE, "health", "red"),
    EDUCATION(EXPENSE, "education", "indigo"),
    HOME(EXPENSE, "home", "amber"),
    PHONE_AND_INTERNET(EXPENSE, "phone", "cyan"),
    SOCIAL_AND_EVENTS(EXPENSE, "gift", "rose"),
    DONATION_AND_ZAKAT(EXPENSE, "charity", "green"),
    FEES_AND_ADMIN(EXPENSE, "fee", "slate"),
    OTHER_EXPENSE(EXPENSE, "other", "gray"),
    SALARY(INCOME, "salary", "green"),
    BONUS(INCOME, "bonus", "teal"),
    BUSINESS(INCOME, "business", "blue"),
    GIFT_INCOME(INCOME, "gift", "rose"),
    OTHER_INCOME(INCOME, "other", "gray"),
}
