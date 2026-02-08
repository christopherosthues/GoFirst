package org.darchacheron.gofirst.play

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlin.random.Random

data class TouchPoint(
    val id: Long,
    val position: Offset,
    val color: Color
)

class PlayViewModel : ViewModel() {
    private val _touches = mutableStateMapOf<Long, TouchPoint>()
    val touches: Map<Long, TouchPoint> = _touches

    private val _countdown = mutableStateOf<Int?>(null)
    val countdown: State<Int?> = _countdown

    private val _isSelectingPlayer = mutableStateOf(false)

    private val _highlightedPlayerId = mutableStateOf<Long?>(null)
    val highlightedPlayerId: State<Long?> = _highlightedPlayerId

    private val _selectedPlayerId = mutableStateOf<Long?>(null)
    val selectedPlayerId: State<Long?> = _selectedPlayerId

    private var selectionJob: Job? = null

    private val colors = listOf(
        Color(0xFFE57373), // Red
        Color(0xFF64B5F6), // Blue
        Color(0xFF81C784), // Green
        Color(0xFFFFF176), // Yellow
        Color(0xFFFFB74D), // Orange
        Color(0xFFBA68C8), // Purple
        Color(0xFF4DB6AC), // Teal
        Color(0xFFF06292), // Pink
        Color(0xFF3D1E06), // Brown
    )

    fun onTouchDown(id: Long, position: Offset) {
        if (_selectedPlayerId.value != null) {
            reset()
        }

        // Do not add players if selection is in progress
        if (_isSelectingPlayer.value) return

        if (!_touches.containsKey(id)) {
            val color = colors[(_touches.size) % colors.size]
            _touches[id] = TouchPoint(id, position, color)
        }

        startSelectionProcess()
    }

    fun onTouchMove(id: Long, position: Offset) {
        _touches[id]?.let {
            _touches[id] = it.copy(position = position)
        }
    }

    fun onTouchUp(id: Long) {
        // Do not remove players if selection is in progress or completed
        if (_isSelectingPlayer.value || _selectedPlayerId.value != null) return

        _touches.remove(id)
        if (_touches.size < 2 && _selectedPlayerId.value == null && _countdown.value != null) {
            cancelSelection()
        }
    }

    private fun startSelectionProcess() {
        if (_touches.size >= 2 && selectionJob == null && _selectedPlayerId.value == null) {
            selectionJob = viewModelScope.launch {
                for (i in 5 downTo 1) {
                    _countdown.value = i
                    delay(1000)
                    if (_touches.size < 2) {
                        cancelSelection()
                        return@launch
                    }
                }
                _countdown.value = 0
                delay(200) // Small pause at 0
                _countdown.value = null
                _isSelectingPlayer.value = true
                animateSelection()
            }
        }
    }

    private suspend fun animateSelection() {
        val keys = _touches.keys.toList()
        if (keys.isEmpty()) return

        // Shuffle animation: highlight players one after another
        var delayMillis = 100L
        repeat(15) {
            _highlightedPlayerId.value = keys[Random.nextInt(keys.size)]
            delay(delayMillis)
            // Gradually slow down the animation
            delayMillis += 20
            if (_touches.size < 2) {
                reset()
                return
            }
        }

        // Final selection
        _selectedPlayerId.value = keys[Random.nextInt(keys.size)]
        _highlightedPlayerId.value = null
        selectionJob = null
        _isSelectingPlayer.value = false
    }

    private fun cancelSelection() {
        selectionJob?.cancel()
        selectionJob = null
        _countdown.value = null
        _highlightedPlayerId.value = null
    }

    fun reset() {
        cancelSelection()
        _touches.clear()
        _selectedPlayerId.value = null
    }

    override fun onCleared() {
        super.onCleared()
        cancelSelection()
    }
}
