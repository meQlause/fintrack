package com.meqlause.fintrack.lock

import android.view.Window
import android.view.WindowManager

// FR-104: FLAG_SECURE keeps the window out of recents thumbnails and screenshots while the option is on
internal fun Window.applyHideFromRecents(hide: Boolean) {
    if (hide) {
        addFlags(WindowManager.LayoutParams.FLAG_SECURE)
    } else {
        clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }
}
