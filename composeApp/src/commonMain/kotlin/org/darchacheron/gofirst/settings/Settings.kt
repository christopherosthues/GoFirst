package org.darchacheron.gofirst.settings

import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
data class Settings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
)