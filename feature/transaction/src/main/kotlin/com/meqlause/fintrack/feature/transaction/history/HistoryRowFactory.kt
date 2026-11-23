package com.meqlause.fintrack.feature.transaction.history

import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.ui.format.DateFormatter
import com.meqlause.fintrack.core.ui.transaction.RowAppearance
import java.time.ZoneId

// FR-036: a transaction as one list row; the title falls back to the category, the subtitle names wallet and time
internal fun Transaction.toRowItem(
    names: HistoryNames,
    zone: ZoneId,
): HistoryRowItem {
    val time = DateFormatter.timeLabel(timing.occurredAt.atZone(zone).toLocalTime())
    return HistoryRowItem(
        transactionId = transactionId.value,
        type = movement.type,
        amount = movement.amount,
        title =
            memo.merchant.ifBlank { memo.note }.takeIf { it.isNotBlank() }
                ?: routing.categoryId?.let(names::categoryName),
        subtitle = "${walletLabel(names)} · $time",
        appearance = RowAppearance.of(movement.type, routing.categoryId?.let(names::categoryAppearance)),
    )
}

// FR-036: a transfer reads "Dari → Ke", any other movement names its one wallet
private fun Transaction.walletLabel(names: HistoryNames): String {
    val source = names.walletName(routing.walletId)
    val target = routing.targetWalletId ?: return source
    return "$source → ${names.walletName(target)}"
}
