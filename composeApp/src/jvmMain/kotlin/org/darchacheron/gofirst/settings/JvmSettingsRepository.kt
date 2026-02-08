package org.darchacheron.gofirst.settings

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import org.darchacheron.gofirst.ui.PlayerColors
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

                val settings =
                    Settings(
                        themeMode =
                            getProperty(SettingsKeys.THEME_MODE)?.let {
                                ThemeMode.valueOf(it)
                            } ?: ThemeMode.SYSTEM,
                        player1Color = getProperty(SettingsKeys.PLAYER_1_COLOR)?.toLong() ?: PlayerColors[0].value.toLong(),
                        player2Color = getProperty(SettingsKeys.PLAYER_2_COLOR)?.toLong() ?: PlayerColors[1].value.toLong(),
                        player3Color = getProperty(SettingsKeys.PLAYER_3_COLOR)?.toLong() ?: PlayerColors[2].value.toLong(),
                        player4Color = getProperty(SettingsKeys.PLAYER_4_COLOR)?.toLong() ?: PlayerColors[3].value.toLong(),
                        player5Color = getProperty(SettingsKeys.PLAYER_5_COLOR)?.toLong() ?: PlayerColors[4].value.toLong(),
                        player6Color = getProperty(SettingsKeys.PLAYER_6_COLOR)?.toLong() ?: PlayerColors[5].value.toLong(),
                        player7Color = getProperty(SettingsKeys.PLAYER_7_COLOR)?.toLong() ?: PlayerColors[6].value.toLong(),
                        player8Color = getProperty(SettingsKeys.PLAYER_8_COLOR)?.toLong() ?: PlayerColors[7].value.toLong(),
                        player9Color = getProperty(SettingsKeys.PLAYER_9_COLOR)?.toLong() ?: PlayerColors[8].value.toLong(),
                        player10Color = getProperty(SettingsKeys.PLAYER_10_COLOR)?.toLong() ?: PlayerColors[9].value.toLong(),
                    )
                settingsFlow.value = settings
            }
        }.onFailure { it.printStackTrace() }
    }

    override suspend fun saveSettings(settings: Settings) {
        withContext(Dispatchers.IO) {
            runCatching {
                Properties().apply {
                    setProperty(SettingsKeys.THEME_MODE, settings.themeMode.name)
                    setProperty(SettingsKeys.PLAYER_1_COLOR, settings.player1Color.toString())
                    setProperty(SettingsKeys.PLAYER_2_COLOR, settings.player2Color.toString())
                    setProperty(SettingsKeys.PLAYER_3_COLOR, settings.player3Color.toString())
                    setProperty(SettingsKeys.PLAYER_4_COLOR, settings.player4Color.toString())
                    setProperty(SettingsKeys.PLAYER_5_COLOR, settings.player5Color.toString())
                    setProperty(SettingsKeys.PLAYER_6_COLOR, settings.player6Color.toString())
                    setProperty(SettingsKeys.PLAYER_7_COLOR, settings.player7Color.toString())
                    setProperty(SettingsKeys.PLAYER_8_COLOR, settings.player8Color.toString())
                    setProperty(SettingsKeys.PLAYER_9_COLOR, settings.player9Color.toString())
                    setProperty(SettingsKeys.PLAYER_10_COLOR, settings.player10Color.toString())
                    settingsFile.outputStream().use { store(it, null) }
                }
                settingsFlow.value = settings
            }.onFailure { it.printStackTrace() }
        }
    }

    override fun getSettingsFlow(): Flow<Settings> = settingsFlow

    override suspend fun getSettings(): Settings = settingsFlow.value
}