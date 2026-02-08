package org.darchacheron.gofirst.ui

abstract class UiState {
    class Loading : UiState()

    class Error(val message: String) : UiState()

    class Success<T>(val data: T) : UiState()
}