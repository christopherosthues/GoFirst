package org.darchacheron.gofirst.settings

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import org.darchacheron.gofirst.utils.DesktopPaths
import java.io.File
import java.util.Properties
import kotlin.apply
import kotlin.io.inputStream
import kotlin.io.outputStream
import kotlin.io.use
import kotlin.let
import kotlin.onFailure
import kotlin.runCatching
import kotlin.takeIf
import kotlin.text.isNotBlank
import kotlin.uuid.Uuid

class JvmSettingsRepository : SettingsRepository {
    private val settingsFlow = MutableStateFlow(Settings())
    private val settingsFile =
        File(
            DesktopPaths.getAppDataDir(),
            "${SettingsKeys.FILE_NAME}.properties"
        )

    init {
        loadSettings()
    }

    private fun loadSettings() {
        runCatching {
            if (!settingsFile.exists()) {
                return
            }

            Properties().apply {
                settingsFile.inputStream().use { load(it) }

                val selectedProfileIdStr = getProperty(SettingsKeys.SELECTED_PROFILE_ID)
                val selectedProfileId = selectedProfileIdStr?.takeIf { it.isNotBlank() }?.let { Uuid.parse(it) }

                val settings =
                    Settings(
                        weightUnit =
                            getProperty(SettingsKeys.WEIGHT_UNIT)?.let {
                                WeightUnit.valueOf(it)
                            } ?: WeightUnit.KG,
                        distanceUnit =
                            getProperty(SettingsKeys.DISTANCE_UNIT)?.let {
                                DistanceUnit.valueOf(it)
                            } ?: DistanceUnit.KM,
                        bodyMeasurementUnit =
                            getProperty(SettingsKeys.BODY_MEASUREMENT_UNIT)?.let {
                                BodyMeasurementUnit.valueOf(it)
                            } ?: BodyMeasurementUnit.CM,
                        themeMode =
                            getProperty(SettingsKeys.THEME_MODE)?.let {
                                ThemeMode.valueOf(it)
                            } ?: ThemeMode.SYSTEM,
                        selectedProfileId = selectedProfileId
                    )
                settingsFlow.value = settings
            }
        }.onFailure { it.printStackTrace() }
    }

    override suspend fun saveSettings(settings: Settings) {
        withContext(Dispatchers.IO) {
            runCatching {
                Properties().apply {
                    setProperty(SettingsKeys.WEIGHT_UNIT, settings.weightUnit.name)
                    setProperty(SettingsKeys.DISTANCE_UNIT, settings.distanceUnit.name)
                    setProperty(SettingsKeys.BODY_MEASUREMENT_UNIT, settings.bodyMeasurementUnit.name)
                    setProperty(SettingsKeys.THEME_MODE, settings.themeMode.name)
                    setProperty(SettingsKeys.SELECTED_PROFILE_ID, settings.selectedProfileId?.toString() ?: "")
                    settingsFile.outputStream().use { store(it, null) }
                }
                settingsFlow.value = settings
            }.onFailure { it.printStackTrace() }
        }
    }

    override fun getSettingsFlow(): Flow<Settings> = settingsFlow

    override suspend fun getSettings(): Settings = settingsFlow.value
}