package com.meqlause.fintrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

// NFR-001: portrait-only launcher activity; shows the brand name until the navigation shell (T2.6)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BrandText()
        }
    }
}

@Composable
private fun BrandText() {
    Text(text = stringResource(R.string.app_name))
}
