package org.darchacheron.gofirst

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.darchacheron.gofirst.di.initKoin

fun main() {
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "GoFirst",
        ) {
            App()
        }
    }
}