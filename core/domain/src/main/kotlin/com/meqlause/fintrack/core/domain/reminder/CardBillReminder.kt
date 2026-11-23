package com.meqlause.fintrack.core.domain.reminder

import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.WalletId
import java.time.LocalDate

/** FR-135, BR-045: a credit statement due in a lead day that still has money to pay */
public data class CardBillReminder(
    public val walletId: WalletId,
    public val walletName: String,
    public val amount: Money,
    public val dueDate: LocalDate,
    public val daysBefore: Int,
) : Reminder {
    override val reminderKey: String
        get() = "card-${walletId.value}-$dueDate-$daysBefore"
}
