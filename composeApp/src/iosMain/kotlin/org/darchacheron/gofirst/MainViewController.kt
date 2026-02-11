package org.darchacheron.gofirst

import androidx.compose.ui.window.ComposeUIViewController
import org.darchacheron.gofirst.di.initKoin

fun MainViewController() =
    ComposeUIViewController(
        configure = {
            initKoin()
        }
    ) { App() }