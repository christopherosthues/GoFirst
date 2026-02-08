package org.darchacheron.gofirst.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gofirst.composeapp.generated.resources.Res
import gofirst.composeapp.generated.resources.settings_error_loading_settings
import gofirst.composeapp.generated.resources.settings_error_resetting_settings
import gofirst.composeapp.generated.resources.settings_error_saving_settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.darchacheron.gofirst.ui.UiState

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _settingsFlow: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading())
    val settingsFlow = _settingsFlow.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.getSettingsFlow()
                .onStart { _settingsFlow.value = UiState.Loading() }
                .catch { _settingsFlow.value = UiState.Error(Res.string.settings_error_loading_settings) }
                .collect { _settingsFlow.value = UiState.Success(it) }
        }
    }

    fun onThemeModeChanged(mode: ThemeMode) {
        val currentValue = _settingsFlow.value
        val settings: Settings = if (currentValue is UiState.Success<*>) currentValue.data as Settings else Settings()
        _settingsFlow.update { UiState.Success(settings.copy(themeMode = mode)) }
    }

    fun onPlayerColorChanged(playerId: Int, color: Long) {
        val currentValue = _settingsFlow.value
        val settings: Settings = if (currentValue is UiState.Success<*>) currentValue.data as Settings else Settings()
        _settingsFlow.update {
            UiState.Success(
                settings.copy(
                    player1Color = if (playerId == 1) color else settings.player1Color,
                    player2Color = if (playerId == 2) color else settings.player2Color,
                    player3Color = if (playerId == 3) color else settings.player3Color,
                    player4Color = if (playerId == 4) color else settings.player4Color,
                    player5Color = if (playerId == 5) color else settings.player5Color,
                    player6Color = if (playerId == 6) color else settings.player6Color,
                    player7Color = if (playerId == 7) color else settings.player7Color,
                    player8Color = if (playerId == 8) color else settings.player8Color,
                    player9Color = if (playerId == 9) color else settings.player9Color,
                    player10Color = if (playerId == 10) color else settings.player10Color,
                )
            )
        }
    }

    fun saveSettings(onSuccess: () -> Unit = {}) {
        val currentValue = _settingsFlow.value
        _settingsFlow.update { UiState.Loading() }
        viewModelScope.launch {
            try {
                val settings: Settings = if (currentValue is UiState.Success<*>) currentValue.data as Settings else Settings()
                val settingsToSave =
                    Settings(
                        themeMode = settings.themeMode,
                    )
                settingsRepository.saveSettings(settingsToSave)
                _settingsFlow.update {
                    UiState.Success(settingsToSave)
                }
                onSuccess()
            } catch (e: Exception) {
                _settingsFlow.update {
                    UiState.Error(Res.string.settings_error_saving_settings)
                }
            }
        }
    }

    fun resetToDefaults() {
        val defaultSettings = Settings()
        _settingsFlow.update { UiState.Success(defaultSettings) }
    }

    fun revertChanges() {
        _settingsFlow.update { UiState.Loading() }
        viewModelScope.launch {
            try {
                val settings = settingsRepository.getSettings()
                _settingsFlow.update {
                    UiState.Success(settings)
                }
            } catch (e: Exception) {
                _settingsFlow.update {
                    UiState.Error(Res.string.settings_error_resetting_settings)
                }
            }
        }
    }

    fun clearError() {
        _settingsFlow.update { UiState.Error(null) }
    }
}