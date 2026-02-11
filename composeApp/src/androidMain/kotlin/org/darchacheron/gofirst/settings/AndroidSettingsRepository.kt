package org.darchacheron.gofirst.settings

import android.content.Context
import androidx.compose.ui.graphics.toColorLong
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.darchacheron.gofirst.ui.PlayerColors
import kotlin.let
import kotlin.takeIf
import kotlin.text.isNotBlank
import kotlin.uuid.Uuid

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = SettingsKeys.FILE_NAME)

class AndroidSettingsRepository(
    private val context: Context
) : SettingsRepository {
    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey(SettingsKeys.THEME_MODE)
        val PLAYER_1_COLOR = longPreferencesKey(SettingsKeys.PLAYER_1_COLOR)
        val PLAYER_2_COLOR = longPreferencesKey(SettingsKeys.PLAYER_2_COLOR)
        val PLAYER_3_COLOR = longPreferencesKey(SettingsKeys.PLAYER_3_COLOR)
        val PLAYER_4_COLOR = longPreferencesKey(SettingsKeys.PLAYER_4_COLOR)
        val PLAYER_5_COLOR = longPreferencesKey(SettingsKeys.PLAYER_5_COLOR)
        val PLAYER_6_COLOR = longPreferencesKey(SettingsKeys.PLAYER_6_COLOR)
        val PLAYER_7_COLOR = longPreferencesKey(SettingsKeys.PLAYER_7_COLOR)
        val PLAYER_8_COLOR = longPreferencesKey(SettingsKeys.PLAYER_8_COLOR)
        val PLAYER_9_COLOR = longPreferencesKey(SettingsKeys.PLAYER_9_COLOR)
        val PLAYER_10_COLOR = longPreferencesKey(SettingsKeys.PLAYER_10_COLOR)
    }

    override suspend fun saveSettings(settings: Settings) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PLAYER_1_COLOR] = settings.player1Color
            preferences[PreferencesKeys.PLAYER_2_COLOR] = settings.player2Color
            preferences[PreferencesKeys.PLAYER_3_COLOR] = settings.player3Color
            preferences[PreferencesKeys.PLAYER_4_COLOR] = settings.player4Color
            preferences[PreferencesKeys.PLAYER_5_COLOR] = settings.player5Color
            preferences[PreferencesKeys.PLAYER_6_COLOR] = settings.player6Color
            preferences[PreferencesKeys.PLAYER_7_COLOR] = settings.player7Color
            preferences[PreferencesKeys.PLAYER_8_COLOR] = settings.player8Color
            preferences[PreferencesKeys.PLAYER_9_COLOR] = settings.player9Color
            preferences[PreferencesKeys.PLAYER_10_COLOR] = settings.player10Color
            preferences[PreferencesKeys.THEME_MODE] = settings.themeMode.name
        }
    }

    override fun getSettingsFlow(): Flow<Settings> =
        context.dataStore.data
            .catch { exception ->
                // Log the error and emit default settings
                exception.printStackTrace()
                emit(emptyPreferences())
            }.map { preferences ->
                Settings(
                    themeMode =
                        preferences[PreferencesKeys.THEME_MODE]?.let {
                            ThemeMode.valueOf(it)
                        } ?: ThemeMode.SYSTEM,
                    player1Color = preferences[PreferencesKeys.PLAYER_1_COLOR] ?: PlayerColors[0].toColorLong(),
                    player2Color = preferences[PreferencesKeys.PLAYER_2_COLOR] ?: PlayerColors[1].toColorLong(),
                    player3Color = preferences[PreferencesKeys.PLAYER_3_COLOR] ?: PlayerColors[2].toColorLong(),
                    player4Color = preferences[PreferencesKeys.PLAYER_4_COLOR] ?: PlayerColors[3].toColorLong(),
                    player5Color = preferences[PreferencesKeys.PLAYER_5_COLOR] ?: PlayerColors[4].toColorLong(),
                    player6Color = preferences[PreferencesKeys.PLAYER_6_COLOR] ?: PlayerColors[5].toColorLong(),
                    player7Color = preferences[PreferencesKeys.PLAYER_7_COLOR] ?: PlayerColors[6].toColorLong(),
                    player8Color = preferences[PreferencesKeys.PLAYER_8_COLOR] ?: PlayerColors[7].toColorLong(),
                    player9Color = preferences[PreferencesKeys.PLAYER_9_COLOR] ?: PlayerColors[8].toColorLong(),
                    player10Color = preferences[PreferencesKeys.PLAYER_10_COLOR] ?: PlayerColors[9].toColorLong(),
                )
            }
}