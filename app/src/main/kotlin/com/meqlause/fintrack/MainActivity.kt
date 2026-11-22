package com.meqlause.fintrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// NFR-001: portrait-only launcher activity; hosts the navigation shell (DOC-09) inside the theme
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FintrackTheme {
                FintrackApp()
            }
        }
    }
}
