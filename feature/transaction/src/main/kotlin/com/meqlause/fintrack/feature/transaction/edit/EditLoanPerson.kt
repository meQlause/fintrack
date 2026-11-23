package com.meqlause.fintrack.feature.transaction.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.meqlause.fintrack.core.designsystem.component.InputLook
import com.meqlause.fintrack.core.designsystem.component.ScreenInput
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.designsystem.R as DesignR

// FR-150, S-04: the borrower row of the loan card, a grey person avatar and a borderless name input
@Composable
internal fun EditLoanPerson(
    name: String,
    hint: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FintrackTheme.colors
    val style = FintrackScreenText.style(size = 15f, weight = 600)
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = FintrackScreenSizes.formRowTall)
                .padding(horizontal = FintrackScreenSizes.fieldPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FintrackScreenSizes.pickRowVertical),
    ) {
        Box(
            modifier =
                Modifier
                    .size(FintrackScreenSizes.loanAvatar)
                    .clip(CircleShape)
                    .background(colors.surfaces.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(DesignR.drawable.ic_person),
                contentDescription = null,
                tint = colors.texts.onSurfaceVariant,
                modifier = Modifier.size(FintrackScreenSizes.circleGlyph),
            )
        }
        ScreenInput(
            value = name,
            onValueChange = onChange,
            look = InputLook(placeholder = hint, style = style),
            modifier = Modifier.weight(1f),
        )
    }
}
