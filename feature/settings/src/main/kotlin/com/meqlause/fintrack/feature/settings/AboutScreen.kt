package com.meqlause.fintrack.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.ScreenBackHeader
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.feature.settings.R

// FR-123, S-12: stateless Tentang screen with the version, privacy promise, storage notes and licences
@Composable
internal fun AboutScreen(
    version: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().background(FintrackTheme.colors.surfaces.background)) {
        ScreenBackHeader(
            title = stringResource(R.string.about_title),
            backLabel = stringResource(R.string.settings_back),
            onBack = onBack,
        )
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = FintrackScreenSizes.screenGutter)
                    .padding(bottom = FintrackScreenSizes.screenBottom),
            verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
        ) {
            AboutHeader(version = version)
            SettingsSection(title = stringResource(R.string.about_section_privacy), padded = true) {
                AboutBody(text = stringResource(R.string.about_privacy_statement))
            }
            SettingsSection(title = stringResource(R.string.about_section_storage), padded = true) {
                AboutStorage()
            }
            SettingsSection(title = stringResource(R.string.about_section_licenses), padded = true) {
                AboutBody(text = stringResource(R.string.about_license_font))
            }
        }
    }
}

// FR-123: the app name and its installed version
@Composable
internal fun AboutHeader(version: String) {
    Column {
        Text(
            text = stringResource(R.string.about_app_name),
            style = FintrackTheme.typography.headlineSmall,
            color = FintrackTheme.colors.texts.onSurface,
        )
        Text(
            text = stringResource(R.string.about_version_value, version),
            style = FintrackTheme.typography.bodyMedium,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
    }
}

// FR-123: where the data lives and when it can leave the device
@Composable
internal fun AboutStorage() {
    val lines =
        listOf(
            R.string.about_storage_database,
            R.string.about_storage_key,
            R.string.about_storage_backup,
            R.string.about_storage_csv,
        )
    Column(verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small)) {
        lines.forEach { line -> AboutBody(text = stringResource(line)) }
    }
}

// FR-123: one paragraph of the Tentang screen
@Composable
internal fun AboutBody(text: String) {
    Text(
        text = text,
        style = FintrackTheme.typography.bodyLarge,
        color = FintrackTheme.colors.texts.onSurface,
    )
}
