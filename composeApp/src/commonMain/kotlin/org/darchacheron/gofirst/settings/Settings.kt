package org.darchacheron.gofirst.settings

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Data class representing user settings.
 */
@OptIn(ExperimentalUuidApi::class)
data class Settings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
)