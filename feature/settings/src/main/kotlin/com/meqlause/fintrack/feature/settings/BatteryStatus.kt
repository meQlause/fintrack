package com.meqlause.fintrack.feature.settings

import android.content.Context
import android.os.PowerManager

// DOC-07: true when the system does not optimise Fintrack's battery use, so notifications keep arriving
internal fun isOptimisationIgnored(context: Context): Boolean {
    val power = context.getSystemService(PowerManager::class.java)
    return power.isIgnoringBatteryOptimizations(context.packageName)
}
