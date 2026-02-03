package org.darchacheron.gofirst

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import org.darchacheron.gofirst.play.PlayScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        PlayScreen()
    }
}