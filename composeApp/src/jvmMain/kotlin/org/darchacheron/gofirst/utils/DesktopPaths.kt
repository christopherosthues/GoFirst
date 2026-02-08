package org.darchacheron.gofirst.utils

import java.io.File
import kotlin.apply
import kotlin.io.path.Path
import kotlin.text.contains
import kotlin.text.lowercase

/**
 * Utility object to handle desktop-specific application paths
 */
object DesktopPaths {
    private const val APP_NAME = "FitBe"

    /**
     * Gets the OS-specific application data directory
     * Windows: %APPDATA%\FitBe
     * macOS: ~/Library/Application Support/FitBe
     * Linux: ~/.config/FitBe
     */
    fun getAppDataDir(): File {
        val os = System.getProperty("os.name").lowercase()
        val userHome = System.getProperty("user.home")

        val appDir =
            when {
                os.contains("win") -> Path(System.getenv("APPDATA"), APP_NAME)
                os.contains("mac") -> Path(userHome, "Library/Application Support", APP_NAME)
                os.contains("nix") || os.contains("nux") || os.contains("aix") ->
                    Path(
                        userHome,
                        ".local/share/",
                        APP_NAME
                    )

                else -> Path(userHome, ".config", APP_NAME) // Linux and others
            }

        return File(appDir.toString()).apply { mkdirs() }
    }
}