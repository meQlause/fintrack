package com.meqlause.fintrack.feature.transaction.detail

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackCard
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.feature.transaction.R
import com.meqlause.fintrack.core.designsystem.R as DesignR

// FR-034, FR-035, FR-038, S-06: Ubah, Ulangi (not for loan-linked rows) and Hapus, each a card with an icon over a label
@Composable
internal fun ActionRow(
    isLoanLinked: Boolean,
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small)) {
        ActionCard(
            text = stringResource(R.string.transaction_detail_edit),
            iconRes = DesignR.drawable.ic_edit,
            onClick = onEdit,
            modifier = Modifier.weight(1f),
        )
        if (!isLoanLinked) {
            ActionCard(
                text = stringResource(R.string.transaction_detail_duplicate),
                iconRes = DesignR.drawable.ic_copy,
                onClick = onDuplicate,
                modifier = Modifier.weight(1f),
            )
        }
        ActionCard(
            text = stringResource(R.string.transaction_detail_delete),
            iconRes = DesignR.drawable.ic_trash,
            onClick = onDelete,
            modifier = Modifier.weight(1f),
            color = FintrackTheme.colors.semantics.error,
        )
    }
}

@Composable
private fun ActionCard(
    text: String,
    @DrawableRes iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = FintrackTheme.colors.texts.onSurface,
) {
    FintrackCard(
        modifier = modifier.height(FintrackTheme.sizes.actionHeight),
        onClick = onClick,
        contentPadding = PaddingValues(),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().height(FintrackTheme.sizes.actionHeight),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.xSmall, Alignment.CenterVertically),
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(FintrackTheme.sizes.iconSmall),
            )
            Text(text = text, style = FintrackTheme.typography.caption, color = color)
        }
    }
}
