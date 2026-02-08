package org.darchacheron.gofirst.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
                .catch { _settingsFlow.value = UiState.Error(it.message ?: "Unknown error") }
                .collect { _settingsFlow.value = UiState.Success(it) }
        }
    }

    fun onThemeModeChanged(mode: ThemeMode) {
        val currentValue = _settingsFlow.value
        val settings: Settings = if (currentValue is UiState.Success<*>) currentValue.data as Settings else Settings()
        _settingsFlow.update { UiState.Success(settings.copy(themeMode = mode)) }
    }

    fun saveSettings(onSuccess: () -> Unit = {}) {
        _settingsFlow.update { UiState.Loading() }
        viewModelScope.launch {
            try {
                val currentValue = _settingsFlow.value
                val settings: Settings = if (currentValue is UiState.Success<*>) currentValue.data as Settings else Settings()
                val settingsToSave =
                    Settings(
                        themeMode = settings.themeMode,
                    )
                settingsRepository.saveSettings(settingsToSave)
                _settingsFlow.update {
                    UiState.Success<Settings>(settingsToSave)
                }
                onSuccess()
            } catch (e: Exception) {
                _settingsFlow.update {
                    UiState.Error(e.message ?: "Unknown error")
                }
            }
        }
    }
}