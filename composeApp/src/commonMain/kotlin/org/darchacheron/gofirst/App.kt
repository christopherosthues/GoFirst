package org.darchacheron.gofirst

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import org.darchacheron.gofirst.play.PlayScreen
import org.darchacheron.gofirst.settings.Settings
import org.darchacheron.gofirst.settings.SettingsViewModel
import org.darchacheron.gofirst.ui.AppTheme
import org.darchacheron.gofirst.ui.UiState
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject

@Composable
@Preview
fun App(
    settingsViewModel: SettingsViewModel = koinInject()
) {
    KoinApplication(application = {}) {
        val settingsUiState = settingsViewModel.settingsFlow.collectAsState()

        val currentSettings = settingsUiState.value
        val settings = if (currentSettings is UiState.Success<*>) {
            currentSettings.data as Settings
        } else {
            Settings()
        }

        AppTheme(themeMode = settings.themeMode) {
            MaterialTheme {
                PlayScreen()
            }
        }
    }
}