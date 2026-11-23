package com.meqlause.fintrack.core.designsystem.icon

import androidx.annotation.DrawableRes
import com.meqlause.fintrack.core.designsystem.R

// DOC-08: maps every iconKey stored on categories, wallets and features to a stroke drawable; unknown keys get the fallback
object FintrackIcons {
    private val drawables: Map<String, Int> =
        mapOf(
            "food" to R.drawable.ic_food,
            "transport" to R.drawable.ic_transport,
            "shopping" to R.drawable.ic_shopping,
            "bill" to R.drawable.ic_bill,
            "entertainment" to R.drawable.ic_entertainment,
            "health" to R.drawable.ic_health,
            "phone" to R.drawable.ic_phone,
            "gift" to R.drawable.ic_gift,
            "charity" to R.drawable.ic_gift,
            "fee" to R.drawable.ic_fee,
            "salary" to R.drawable.ic_salary,
            "bonus" to R.drawable.ic_bonus,
            "business" to R.drawable.ic_business,
            "cash" to R.drawable.ic_cash,
            "bank" to R.drawable.ic_bank,
            "ewallet" to R.drawable.ic_ewallet,
            "card" to R.drawable.ic_card,
            "clock" to R.drawable.ic_clock,
            "trend" to R.drawable.ic_trend,
            "investment" to R.drawable.ic_trend,
            "wallet" to R.drawable.ic_wallet,
            "home" to R.drawable.ic_home,
            "transfer" to R.drawable.ic_transfer,
            "person" to R.drawable.ic_person,
            "loan" to R.drawable.ic_person,
            "bolt" to R.drawable.ic_bolt,
            "bell" to R.drawable.ic_bell,
            "other" to R.drawable.ic_other,
        )

    // DOC-08: the icons a category can wear, in picker order
    val categoryKeys: List<String> =
        listOf("food", "transport", "shopping", "bill", "entertainment", "health", "phone", "gift", "fee", "home", "salary", "bonus", "business", "other")

    @DrawableRes
    fun drawableFor(iconKey: String): Int = drawables[iconKey] ?: R.drawable.ic_other
}
