package org.darchacheron.gofirst.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlin.let
import kotlin.takeIf
import kotlin.text.isNotBlank
import kotlin.uuid.Uuid

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = SettingsKeys.FILE_NAME)

class AndroidSettingsRepository(
    private val context: Context
) : SettingsRepository {
    private object PreferencesKeys {
        val WEIGHT_UNIT = stringPreferencesKey(SettingsKeys.WEIGHT_UNIT)
        val DISTANCE_UNIT = stringPreferencesKey(SettingsKeys.DISTANCE_UNIT)
        val BODY_MEASUREMENT_UNIT = stringPreferencesKey(SettingsKeys.BODY_MEASUREMENT_UNIT)
        val THEME_MODE = stringPreferencesKey(SettingsKeys.THEME_MODE)
        val SELECTED_PROFILE_ID = stringPreferencesKey(SettingsKeys.SELECTED_PROFILE_ID)
    }

    override suspend fun saveSettings(settings: Settings) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.WEIGHT_UNIT] = settings.weightUnit.name
            preferences[PreferencesKeys.DISTANCE_UNIT] = settings.distanceUnit.name
            preferences[PreferencesKeys.BODY_MEASUREMENT_UNIT] = settings.bodyMeasurementUnit.name
            preferences[PreferencesKeys.THEME_MODE] = settings.themeMode.name
            preferences[PreferencesKeys.SELECTED_PROFILE_ID] = settings.selectedProfileId?.toString() ?: ""
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
                    weightUnit =
                        preferences[PreferencesKeys.WEIGHT_UNIT]?.let {
                            WeightUnit.valueOf(it)
                        } ?: WeightUnit.KG,
                    distanceUnit =
                        preferences[PreferencesKeys.DISTANCE_UNIT]?.let {
                            DistanceUnit.valueOf(it)
                        } ?: DistanceUnit.KM,
                    bodyMeasurementUnit =
                        preferences[PreferencesKeys.BODY_MEASUREMENT_UNIT]?.let {
                            BodyMeasurementUnit.valueOf(it)
                        } ?: BodyMeasurementUnit.CM,
                    themeMode =
                        preferences[PreferencesKeys.THEME_MODE]?.let {
                            ThemeMode.valueOf(it)
                        } ?: ThemeMode.SYSTEM,
                    selectedProfileId =
                        preferences[PreferencesKeys.SELECTED_PROFILE_ID]
                            ?.takeIf { it.isNotBlank() }
                            ?.let { Uuid.parse(it) }
                )
            }
}