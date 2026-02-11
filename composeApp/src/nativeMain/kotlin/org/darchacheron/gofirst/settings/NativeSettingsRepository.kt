package org.darchacheron.gofirst.settings

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.darchacheron.gofirst.ui.PlayerColors
import platform.Foundation.NSDictionary
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask
import platform.Foundation.dictionaryWithContentsOfFile
import platform.Foundation.writeToFile
import kotlin.collections.first
import kotlin.collections.getValue
import kotlin.let
import kotlin.onFailure
import kotlin.runCatching
import kotlin.takeIf
import kotlin.text.isNotBlank
import kotlin.to
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class NativeSettingsRepository : SettingsRepository {
    private val settingsFlow = MutableStateFlow(Settings())
    private val fileManager = NSFileManager.defaultManager
    private val settingsFile: String
        get() {
            val docs =
                NSSearchPathForDirectoriesInDomains(
                    NSDocumentDirectory,
                    NSUserDomainMask,
                    true
                ).first() as String
            return "$docs/${SettingsKeys.FILE_NAME}.plist"
        }

    init {
        loadSettings()
    }

    private fun loadSettings() {
        runCatching {
            if (!fileManager.fileExistsAtPath(settingsFile)) {
                return
            }

            (NSDictionary.dictionaryWithContentsOfFile(settingsFile))?.let { dict ->
                val settings =
                    Settings(
                        themeMode =
                            (dict.getValue(SettingsKeys.THEME_MODE) as? String)?.let {
                                ThemeMode.valueOf(it)
                            } ?: ThemeMode.SYSTEM,
                        player1Color = (dict.getValue(SettingsKeys.PLAYER_1_COLOR) as? Long) ?: PlayerColors[0].value.toLong(),
                        player2Color = (dict.getValue(SettingsKeys.PLAYER_2_COLOR) as? Long) ?: PlayerColors[1].value.toLong(),
                        player3Color = (dict.getValue(SettingsKeys.PLAYER_3_COLOR) as? Long) ?: PlayerColors[2].value.toLong(),
                        player4Color = (dict.getValue(SettingsKeys.PLAYER_4_COLOR) as? Long) ?: PlayerColors[3].value.toLong(),
                        player5Color = (dict.getValue(SettingsKeys.PLAYER_5_COLOR) as? Long) ?: PlayerColors[4].value.toLong(),
                        player6Color = (dict.getValue(SettingsKeys.PLAYER_6_COLOR) as? Long) ?: PlayerColors[5].value.toLong(),
                        player7Color = (dict.getValue(SettingsKeys.PLAYER_7_COLOR) as? Long) ?: PlayerColors[6].value.toLong(),
                        player8Color = (dict.getValue(SettingsKeys.PLAYER_8_COLOR) as? Long) ?: PlayerColors[7].value.toLong(),
                        player9Color = (dict.getValue(SettingsKeys.PLAYER_9_COLOR) as? Long) ?: PlayerColors[8].value.toLong(),
                        player10Color = (dict.getValue(SettingsKeys.PLAYER_10_COLOR) as? Long) ?: PlayerColors[9].value.toLong(),
                    )
                settingsFlow.value = settings
            }
        }.onFailure { it.printStackTrace() }
    }

    override suspend fun saveSettings(settings: Settings) {
        runCatching {
            val dict =
                mutableMapOf(
                    SettingsKeys.THEME_MODE to settings.themeMode.name,
                    SettingsKeys.PLAYER_1_COLOR to settings.player1Color,
                    SettingsKeys.PLAYER_2_COLOR to settings.player2Color,
                    SettingsKeys.PLAYER_3_COLOR to settings.player3Color,
                    SettingsKeys.PLAYER_4_COLOR to settings.player4Color,
                    SettingsKeys.PLAYER_5_COLOR to settings.player5Color,
                    SettingsKeys.PLAYER_6_COLOR to settings.player6Color,
                    SettingsKeys.PLAYER_7_COLOR to settings.player7Color,
                    SettingsKeys.PLAYER_8_COLOR to settings.player8Color,
                    SettingsKeys.PLAYER_9_COLOR to settings.player9Color,
                    SettingsKeys.PLAYER_10_COLOR to settings.player10Color,
                )

            (dict as NSDictionary).writeToFile(settingsFile, true)
            settingsFlow.value = settings
        }.onFailure { it.printStackTrace() }
    }

    override fun getSettingsFlow(): Flow<Settings> = settingsFlow

    override suspend fun getSettings(): Settings = settingsFlow.value
}