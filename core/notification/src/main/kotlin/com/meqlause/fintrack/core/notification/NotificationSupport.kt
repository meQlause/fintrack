package com.meqlause.fintrack.core.notification

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

private const val OPEN_APP_REQUEST = 0
private const val RUPIAH_GROUP = 3

// FR-135: the runtime grant is needed on Android 13 and later; without it nothing is posted
internal fun canPostNotifications(context: Context): Boolean =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

// FR-135: tapping a reminder opens the app's launcher activity
internal fun openAppIntent(context: Context): PendingIntent? {
    val launch = context.packageManager.getLaunchIntentForPackage(context.packageName) ?: return null
    return PendingIntent.getActivity(context, OPEN_APP_REQUEST, launch, PendingIntent.FLAG_IMMUTABLE)
}

// BR-009 display only: whole rupiah with dot grouping, as in Indonesian copy
internal fun rupiahText(amount: Long): String =
    "Rp" +
        amount
            .toString()
            .reversed()
            .chunked(RUPIAH_GROUP)
            .joinToString(".")
            .reversed()
