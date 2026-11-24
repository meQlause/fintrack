package com.meqlause.fintrack.core.ui.notification

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.ui.R

// DOC-10: prominent disclosure shown before the notification-access settings open (Play User Data policy)
@Composable
fun NotificationDisclosureDialog(
    onAgree: () -> Unit,
    onDecline: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDecline,
        title = { Text(text = stringResource(R.string.disclosure_title)) },
        text = { DisclosureBody() },
        confirmButton = {
            TextButton(onClick = onAgree) { Text(text = stringResource(R.string.disclosure_agree)) }
        },
        dismissButton = {
            TextButton(onClick = onDecline) { Text(text = stringResource(R.string.disclosure_decline)) }
        },
    )
}

// DOC-10: what is read, what is ignored, where it stays, how long and how to turn it off
@Composable
private fun DisclosureBody() {
    val paragraphs =
        listOf(
            R.string.disclosure_what_is_read,
            R.string.disclosure_what_is_ignored,
            R.string.disclosure_stays_on_device,
            R.string.disclosure_retention,
            R.string.disclosure_turn_off,
        )
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
    ) {
        paragraphs.forEach { paragraph -> Text(text = stringResource(paragraph)) }
    }
}
