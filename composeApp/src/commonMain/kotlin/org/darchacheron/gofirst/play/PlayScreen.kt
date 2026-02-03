package org.darchacheron.gofirst.play

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PlayScreen(viewModel: PlayViewModel = koinViewModel()) {
    val touches = viewModel.touches
    val countdown by viewModel.countdown
    val selectedPlayerId by viewModel.selectedPlayerId

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        event.changes.forEach { change ->
                            if (change.changedToDown()) {
                                viewModel.onTouchDown(change.id.value, change.position)
                            } else if (change.changedToUp() || change.isConsumed) {
                                viewModel.onTouchUp(change.id.value)
                            } else if (change.positionChange() != Offset.Zero) {
                                viewModel.onTouchMove(change.id.value, change.position)
                            }
                        }
                    }
                }
            }
    ) {
        touches.forEach { (id, touchPoint) ->
            val isSelected = selectedPlayerId == id
            TouchIndicator(
                position = touchPoint.position,
                color = touchPoint.color,
                isSelected = isSelected
            )
        }

        if (countdown != null && countdown!! > 0) {
            Text(
                text = countdown.toString(),
                color = Color.White,
                fontSize = 120.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun TouchIndicator(position: Offset, color: Color, isSelected: Boolean) {
    val infiniteTransition = rememberInfiniteTransition()
    val radiusScale by if (isSelected) {
        infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.3f,
            animationSpec = infiniteRepeatable(
                animation = tween(400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    } else {
        remember { mutableStateOf(1f) }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val baseRadius = 120f
        val animatedRadius = baseRadius * radiusScale
        
        drawCircle(
            color = color,
            radius = animatedRadius,
            center = position,
            alpha = if (isSelected) 1f else 0.6f
        )
        
        if (isSelected) {
            drawCircle(
                color = Color.White,
                radius = animatedRadius + 15f,
                center = position,
                style = Stroke(width = 8f)
            )
        }
    }
}
