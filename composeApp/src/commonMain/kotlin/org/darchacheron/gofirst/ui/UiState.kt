package org.darchacheron.gofirst.ui

import org.jetbrains.compose.resources.StringResource

abstract class UiState {
    class Loading : UiState()

    class Error(val message: StringResource?) : UiState()

    class Success<T>(val data: T) : UiState()
}