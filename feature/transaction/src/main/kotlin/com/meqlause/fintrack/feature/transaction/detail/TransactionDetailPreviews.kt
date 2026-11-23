package com.meqlause.fintrack.feature.transaction.detail

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.TransactionSource
import com.meqlause.fintrack.core.model.TransactionType

// S-06: a stored expense with a note and merchant, shown in both themes
private val sampleDetail =
    TransactionDetail(
        type = TransactionType.EXPENSE,
        amount = Money(25_000L),
        fee = Money(0L),
        title = "Warung Bu Tini",
        merchant = "Warung Bu Tini",
        note = "Makan siang",
        categoryName = "Makan & Minum",
        walletLabel = "BCA",
        dateLabel = "Hari ini",
        timeLabel = "12:30",
        source = TransactionSource.MANUAL,
        isLoanLinked = false,
    )

@Preview(name = "Transaction detail light", showBackground = true, heightDp = 720)
@Composable
internal fun TransactionDetailLightPreview() {
    FintrackTheme(darkTheme = false) {
        TransactionDetailScreen(
            state = TransactionDetailUiState(isLoaded = true, detail = sampleDetail),
            onBack = {},
            onEdit = {},
            onDuplicate = {},
            onDelete = {},
            onUndo = {},
        )
    }
}

@Preview(name = "Transaction detail dark", showBackground = true, heightDp = 720)
@Composable
internal fun TransactionDetailDarkPreview() {
    FintrackTheme(darkTheme = true) {
        TransactionDetailScreen(
            state = TransactionDetailUiState(isLoaded = true, detail = sampleDetail, isDeletePending = true),
            onBack = {},
            onEdit = {},
            onDuplicate = {},
            onDelete = {},
            onUndo = {},
        )
    }
}
