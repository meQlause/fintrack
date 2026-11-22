package com.meqlause.fintrack

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// NFR-001: application entry point; Hilt root for the whole app graph
@HiltAndroidApp
class FintrackApplication : Application()
