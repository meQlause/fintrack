package com.meqlause.fintrack.feature.transaction.detail

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.TransactionSource
import com.meqlause.fintrack.core.model.TransactionType

// S-06: a stored expense with a note and merchant, shown in both themes
private val sampleDetail =
    TransactionDetail(
        type = TransactionType.EXPENSE,
        amount = Money(47_000L),
        feeAmount = Money(0L),
        title = "Makan siang",
        merchant = "",
        note = "Makan siang",
        categoryName = "Makanan & Minuman",
        walletLabel = "DANA",
        dateLabel = "Sel, 3 Nov",
        timeLabel = "12:10",
        source = TransactionSource.NOTIFICATION,
        isLoanLinked = false,
        appearance = Appearance("food", "orange"),
    )

@Preview(name = "Transaction detail light", showBackground = true, heightDp = 720)
@Composable
internal fun TransactionDetailLightPreview() {
    FintrackTheme(darkTheme = false) {
        TransactionDetailScreen(
            state = TransactionDetailUiState(isLoaded = true, detail = sampleDetail),
            actions = TransactionDetailCallbacks({}, {}, {}, {}, {}, {}),
        )
    }
}

@Preview(name = "Transaction detail dark", showBackground = true, heightDp = 720)
@Composable
internal fun TransactionDetailDarkPreview() {
    FintrackTheme(darkTheme = true) {
        TransactionDetailScreen(
            state = TransactionDetailUiState(isLoaded = true, detail = sampleDetail, isDeletePending = true),
            actions = TransactionDetailCallbacks({}, {}, {}, {}, {}, {}),
        )
    }
}
