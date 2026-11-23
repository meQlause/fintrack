package com.meqlause.fintrack.feature.transaction.edit

import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.WalletId

// FR-030..FR-032: what the user has typed or picked; amounts stay digit strings until they are saved
internal data class TransactionForm(
    val type: TransactionType = TransactionType.EXPENSE,
    val rupiahDigits: String = "",
    val feeDigits: String = "",
    val route: FormRoute = FormRoute(),
    val note: String = "",
) {
    val walletId: WalletId? get() = route.walletId
    val targetWalletId: WalletId? get() = route.targetWalletId
    val categoryId: CategoryId? get() = route.categoryId

    // FR-031: one change of the wallet, the target wallet or the category
    fun withRoute(change: FormRoute.() -> FormRoute): TransactionForm = copy(route = route.change())

    val amount: Money
        get() = Money(rupiahDigits.toLongOrNull() ?: 0L)

    val feeAmount: Money
        get() = Money(feeDigits.toLongOrNull() ?: 0L)

    val isTransfer: Boolean
        get() = type == TransactionType.TRANSFER
}

// FR-031, FR-032: the wallet, the target wallet of a transfer and the category the user picked
internal data class FormRoute(
    val walletId: WalletId? = null,
    val targetWalletId: WalletId? = null,
    val categoryId: CategoryId? = null,
)
