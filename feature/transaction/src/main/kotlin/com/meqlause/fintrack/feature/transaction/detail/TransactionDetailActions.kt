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

// FR-034, FR-035, FR-038, S-06: Ubah, Ulangi (not for loan-linked rows) and Hapus, each a card
@Composable
internal fun ActionRow(
    isLoanLinked: Boolean,
    actions: TransactionDetailCallbacks,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small)) {
        val edit = ActionLabel(stringResource(R.string.transaction_detail_edit), DesignR.drawable.ic_edit)
        ActionCard(label = edit, onClick = actions.onEdit, modifier = Modifier.weight(1f))
        if (!isLoanLinked) {
            val copy = ActionLabel(stringResource(R.string.transaction_detail_duplicate), DesignR.drawable.ic_copy)
            ActionCard(label = copy, onClick = actions.onDuplicate, modifier = Modifier.weight(1f))
        }
        val delete =
            ActionLabel(
                text = stringResource(R.string.transaction_detail_delete),
                iconRes = DesignR.drawable.ic_trash,
                color = FintrackTheme.colors.semantics.error,
            )
        ActionCard(label = delete, onClick = actions.onDelete, modifier = Modifier.weight(1f))
    }
}

// S-06: the words, the icon and the colour of one action card
private data class ActionLabel(
    val text: String,
    @param:DrawableRes val iconRes: Int,
    val color: Color? = null,
)

@Composable
private fun ActionCard(
    label: ActionLabel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = label.color ?: FintrackTheme.colors.texts.onSurface
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
                painter = painterResource(label.iconRes),
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(FintrackTheme.sizes.iconSmall),
            )
            Text(text = label.text, style = FintrackTheme.typography.caption, color = color)
        }
    }
}
