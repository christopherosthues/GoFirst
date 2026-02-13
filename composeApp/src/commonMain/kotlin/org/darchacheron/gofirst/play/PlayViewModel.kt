package org.darchacheron.gofirst.play

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerType
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.darchacheron.gofirst.settings.Settings
import org.darchacheron.gofirst.settings.SettingsRepository
import org.darchacheron.gofirst.ui.PlayerColors
import kotlin.random.Random

data class TouchPoint(
    val id: Long,
    val position: Offset,
    val color: Color
)

class PlayViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {
    companion object {
        const val TOUCH_RADIUS = 120f
    }

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
    private var lastColorIndex = -1

    // Mapping from hardware pointer ID to our internal Player ID
    private val pointerToPlayerMap = mutableMapOf<Long, Long>()
    private val pointerInitialPosition = mutableMapOf<Long, Offset>()
    private val pointerCreatedNewPlayer = mutableSetOf<Long>()

    private val playerColors = settingsRepository.getSettingsFlow()
        .map { getColors(it) }
        .catch { emit(PlayerColors) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, PlayerColors)

    private fun getColors(settings: Settings): List<Color> {
        return listOf(
            Color(settings.player1Color.toULong()),
            Color(settings.player2Color.toULong()),
            Color(settings.player3Color.toULong()),
            Color(settings.player4Color.toULong()),
            Color(settings.player5Color.toULong()),
            Color(settings.player6Color.toULong()),
            Color(settings.player7Color.toULong()),
            Color(settings.player8Color.toULong()),
            Color(settings.player9Color.toULong()),
            Color(settings.player10Color.toULong()),
        )
    }

    fun onPointerDown(ptrId: Long, position: Offset, type: PointerType) {
        if (_selectedPlayerId.value != null) {
            reset()
        }

        if (_isSelectingPlayer.value) return

        if (type == PointerType.Mouse) {
            val existing = _touches.entries.find { 
                it.value.position.minus(position).getDistance() < TOUCH_RADIUS 
            }
            
            if (existing != null) {
                // Interacting with existing player
                pointerToPlayerMap[ptrId] = existing.key
            } else {
                // Click on empty space adds a player
                val newId = Random.nextLong()
                val color = getNextColor()
                _touches[newId] = TouchPoint(newId, position, color)
                pointerToPlayerMap[ptrId] = newId
                pointerCreatedNewPlayer.add(ptrId)
            }
        } else {
            // Touch behavior: Player exists only while held
            val color = getNextColor()
            val newId = ptrId // Use pointer ID as player ID for touch
            _touches[newId] = TouchPoint(newId, position, color)
            pointerToPlayerMap[ptrId] = newId
            pointerCreatedNewPlayer.add(ptrId) 
        }
        
        pointerInitialPosition[ptrId] = position
        startSelectionProcess()
    }

    private fun getNextColor(): Color {
        val colors = playerColors.value
        if (colors.isEmpty()) return Color.Gray
        
        val usedColors = _touches.values.map { it.color }.toSet()
        var chosenIndex = colors.indices.firstOrNull { colors[it] !in usedColors }
        if (chosenIndex == null) {
            chosenIndex = (lastColorIndex + 1) % colors.size
        }

        lastColorIndex = chosenIndex
        return colors[chosenIndex]
    }

    fun onPointerMove(ptrId: Long, position: Offset) {
        val playerId = pointerToPlayerMap[ptrId] ?: return
        _touches[playerId]?.let {
            _touches[playerId] = it.copy(position = position)
        }
    }

    fun onPointerUp(ptrId: Long, type: PointerType) {
        val playerId = pointerToPlayerMap.remove(ptrId) ?: return
        val initialPos = pointerInitialPosition.remove(ptrId)
        val wasNew = pointerCreatedNewPlayer.remove(ptrId)

        if (_isSelectingPlayer.value || _selectedPlayerId.value != null) return

        if (type == PointerType.Mouse) {
            if (!wasNew && initialPos != null) {
                // If it was an existing player and we just clicked (didn't drag), remove it
                val currentPos = _touches[playerId]?.position
                if (currentPos != null && currentPos.minus(initialPos).getDistance() < 120f) {
                    _touches.remove(playerId)
                }
            }
            // If it was NEW, it stays. If it was existing and DRAGGED, it stays.
        } else {
            // Touch: always remove when finger is lifted
            _touches.remove(playerId)
        }

        if (_touches.isEmpty()) {
            lastColorIndex = -1
        }

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
                delay(200)
                _countdown.value = null
                _isSelectingPlayer.value = true
                animateSelection()
            }
        }
    }

    private suspend fun animateSelection() {
        var delayMillis = 100L
        repeat(15) {
            val currentKeys = _touches.keys.toList()
            if (currentKeys.isEmpty()) {
                reset()
                return
            }
            _highlightedPlayerId.value = currentKeys[Random.nextInt(currentKeys.size)]
            delay(delayMillis)
            delayMillis += 20
            if (_touches.size < 2) {
                reset()
                return
            }
        }

        val finalKeys = _touches.keys.toList()
        if (finalKeys.isNotEmpty()) {
            _selectedPlayerId.value = finalKeys[Random.nextInt(finalKeys.size)]
        }
        _highlightedPlayerId.value = null
        selectionJob = null
        _isSelectingPlayer.value = false
    }

    private fun cancelSelection() {
        selectionJob?.cancel()
        selectionJob = null
        _countdown.value = null
        _highlightedPlayerId.value = null
        _isSelectingPlayer.value = false
    }

    fun reset() {
        cancelSelection()
        _touches.clear()
        _selectedPlayerId.value = null
        lastColorIndex = -1
        pointerToPlayerMap.clear()
        pointerInitialPosition.clear()
        pointerCreatedNewPlayer.clear()
    }

    override fun onCleared() {
        super.onCleared()
        cancelSelection()
    }
}
