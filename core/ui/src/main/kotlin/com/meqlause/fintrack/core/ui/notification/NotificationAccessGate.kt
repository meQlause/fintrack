package com.meqlause.fintrack.core.ui.notification

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

/**
 * DOC-10: hands [content] a request function; calling it shows the disclosure first, and only "Setuju" opens the
 * system notification-access settings, "Tidak sekarang" opens nothing.
 */
@Composable
fun NotificationAccessGate(
    modifier: Modifier = Modifier,
    content: @Composable (requestAccess: () -> Unit) -> Unit,
) {
    val context = LocalContext.current
    var isDisclosing by rememberSaveable { mutableStateOf(false) }
    Box(modifier = modifier) { content { isDisclosing = true } }
    if (isDisclosing) {
        NotificationDisclosureDialog(
            onAgree = {
                isDisclosing = false
                // DOC-10: the caller may hold an application context, so the settings page opens in a new task
                val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            },
            onDecline = { isDisclosing = false },
        )
    }
}
