package org.darchacheron.gofirst.settings

import gofirst.composeapp.generated.resources.Res
import gofirst.composeapp.generated.resources.theme_dark
import gofirst.composeapp.generated.resources.theme_light
import gofirst.composeapp.generated.resources.theme_system
import org.jetbrains.compose.resources.StringResource

/**
 * Enum for theme selection.
 */
enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM;

    fun toStringResource(): StringResource =
        when (this) {
            LIGHT -> Res.string.theme_light
            DARK -> Res.string.theme_dark
            SYSTEM -> Res.string.theme_system
        }
}