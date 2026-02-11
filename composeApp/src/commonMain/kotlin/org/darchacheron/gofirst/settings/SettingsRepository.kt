package org.darchacheron.gofirst.settings

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * Platform-independent interface for settings persistence
 */
interface SettingsRepository {
    suspend fun saveSettings(settings: Settings)

    fun getSettingsFlow(): Flow<Settings>

    suspend fun getSettings(): Settings = getSettingsFlow().first()
}