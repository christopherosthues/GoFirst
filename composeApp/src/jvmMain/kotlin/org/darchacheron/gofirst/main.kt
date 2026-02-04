package org.darchacheron.gofirst

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import gofirst.composeapp.generated.resources.Res
import gofirst.composeapp.generated.resources.ic_launcher
import org.darchacheron.gofirst.di.initKoin
import org.jetbrains.compose.resources.painterResource

fun main() {
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "GoFirst",
            icon = painterResource(Res.drawable.ic_launcher)
        ) {
            App()
        }
    }
}