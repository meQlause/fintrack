package com.meqlause.fintrack.feature.settings

import android.content.Context

// FR-123: the installed version name, read from the package manager so no generated constant is needed
internal fun readVersionName(context: Context): String =
    context.packageManager
        .getPackageInfo(context.packageName, 0)
        .versionName
        .orEmpty()
