package com.meqlause.fintrack.feature.wallet.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.RowSlots
import com.meqlause.fintrack.core.designsystem.component.RowStyle
import com.meqlause.fintrack.core.designsystem.component.ScreenCard
import com.meqlause.fintrack.core.designsystem.component.ScreenRow
import com.meqlause.fintrack.core.designsystem.icon.IconCircle
import com.meqlause.fintrack.core.designsystem.icon.IconLook
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.networth.NetWorth
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.wallet.R
import com.meqlause.fintrack.core.designsystem.R as DesignR

// FR-150, S-09: the Pinjaman card opens the loan list (S-17); its summary is "Piutang Rp.. . Hutang Rp.."
@Composable
internal fun WalletLoanRow(
    netWorth: NetWorth,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ScreenCard(modifier = modifier) {
        ScreenRow(
            style = RowStyle(onClick = onOpen),
            slots = RowSlots(leading = { IconCircle(look = LOAN_LOOK) }, trailing = { LoanChevron() }),
        ) {
            Text(
                text = stringResource(R.string.wallet_loans_title),
                style = FintrackScreenText.rowTitle(),
                color = FintrackTheme.colors.texts.onSurface,
            )
            Text(
                text =
                    stringResource(
                        R.string.wallet_loans_summary,
                        MoneyFormatter.formatShort(netWorth.piutang),
                        MoneyFormatter.formatShort(netWorth.hutang),
                    ),
                style = FintrackScreenText.rowSubNumber(),
                color = FintrackTheme.colors.texts.onSurfaceVariant,
            )
        }
    }
}

private val LOAN_LOOK = IconLook(iconKey = "person", colorKey = "teal")

// FR-150: the chevron at the end of the Pinjaman card
@Composable
private fun LoanChevron() {
    Icon(
        painter = painterResource(DesignR.drawable.ic_chevron_right),
        contentDescription = null,
        tint = FintrackTheme.colors.texts.onSurfaceVariant,
        modifier = Modifier.size(FintrackScreenSizes.chevron),
    )
}
