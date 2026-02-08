package org.darchacheron.gofirst.play

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import gofirst.composeapp.generated.resources.Res
import gofirst.composeapp.generated.resources.ic_settings
import gofirst.composeapp.generated.resources.play_content_description_settings
import gofirst.composeapp.generated.resources.play_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayScreen(
    viewModel: PlayViewModel = koinViewModel(),
    onSettingsClick: () -> Unit = {}
) {
    val touches = viewModel.touches
    val countdown by viewModel.countdown
    val highlightedPlayerId by viewModel.highlightedPlayerId
    val selectedPlayerId by viewModel.selectedPlayerId

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.play_title)) },
                actions = {
                    IconButton(
                        onClick = onSettingsClick,
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_settings),
                            contentDescription = stringResource(Res.string.play_content_description_settings)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
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
                key(id) {
                    TouchIndicator(
                        position = touchPoint.position,
                        color = touchPoint.color,
                        isSelected = selectedPlayerId == id,
                        isHighlighted = highlightedPlayerId == id
                    )
                }
            }

            if (countdown != null && countdown!! > 0) {
                Text(
                    text = countdown.toString(),
                    fontSize = 120.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun TouchIndicator(position: Offset, color: Color, isSelected: Boolean, isHighlighted: Boolean) {
    val entryScale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        entryScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2000, easing = LinearOutSlowInEasing)
        )
    }

    val infiniteTransition = rememberInfiniteTransition()
    val selectionScale by if (isSelected) {
        infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.3f,
            animationSpec = infiniteRepeatable(
                animation = tween(400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    } else if (isHighlighted) {
        remember { mutableStateOf(1.1f) }
    } else {
        remember { mutableStateOf(1f) }
    }

    val selectionColor = MaterialTheme.colorScheme.onBackground

    Canvas(modifier = Modifier.fillMaxSize()) {
        val baseRadius = 120f
        val animatedRadius = baseRadius * entryScale.value * selectionScale

        drawCircle(
            color = color,
            radius = animatedRadius,
            center = position,
            alpha = if (isSelected || isHighlighted) 1f else 0.6f
        )

        if (isSelected || isHighlighted) {
            drawCircle(
                color = selectionColor,
                radius = animatedRadius + 15f,
                center = position,
                style = Stroke(width = if (isSelected) 8f else 4f)
            )
        }
    }
}
