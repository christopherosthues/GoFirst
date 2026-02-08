package org.darchacheron.gofirst.settings


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import gofirst.composeapp.generated.resources.Res
import gofirst.composeapp.generated.resources.ic_back
import gofirst.composeapp.generated.resources.ic_reset
import gofirst.composeapp.generated.resources.ic_save
import gofirst.composeapp.generated.resources.settings_color_cancel
import gofirst.composeapp.generated.resources.settings_color_select
import gofirst.composeapp.generated.resources.settings_color_title
import gofirst.composeapp.generated.resources.settings_content_description_back
import gofirst.composeapp.generated.resources.settings_content_description_reset
import gofirst.composeapp.generated.resources.settings_content_description_save
import gofirst.composeapp.generated.resources.settings_player
import gofirst.composeapp.generated.resources.settings_theme
import gofirst.composeapp.generated.resources.settings_title
import kotlinx.coroutines.launch
import org.darchacheron.gofirst.ui.UiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView(
    settingsViewModel: SettingsViewModel = koinInject(),
    onBack: () -> Unit = {}
) {
    val settings = settingsViewModel.settingsFlow.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = {
                        settingsViewModel.revertChanges()
                        onBack()
                    }) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_back),
                            contentDescription = stringResource(Res.string.settings_content_description_back)
                        )
                    }
                },
                actions = {}
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FloatingActionButton(
                    onClick = { settingsViewModel.resetToDefaults() },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_reset),
                        contentDescription = stringResource(Res.string.settings_content_description_reset)
                    )
                }
                FloatingActionButton(
                    onClick = {
                        settingsViewModel.saveSettings { onBack() }
                    }
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_save),
                        contentDescription = stringResource(Res.string.settings_content_description_save)
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            when (settings.value) {
                is UiState.Success<*> -> {
                    SettingsControl(settingsViewModel, (settings.value as UiState.Success<*>).data as? Settings ?: Settings())
                }
                is UiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                is UiState.Error -> {
                    val resource = (settings.value as UiState.Error).message
                    if (resource != null) {
                        val message = stringResource(resource)
                        LaunchedEffect(resource, message) {
                            scope.launch {
                                snackbarHostState.showSnackbar(message)
                                settingsViewModel.clearError()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsControl(settingsViewModel: SettingsViewModel, settings: Settings) {
    var isColorPickerVisible by remember { mutableStateOf(false) }
    var playerId by remember { mutableStateOf<Int?>(null) }
    var playerColor by remember { mutableStateOf<Color?>(null) }

    Text(
        text = stringResource(Res.string.settings_theme),
        style = MaterialTheme.typography.titleMedium
    )
    Row {
        ThemeMode.entries.forEach { mode ->
            Row(modifier = Modifier.padding(end = 8.dp)) {
                RadioButton(
                    selected = settings.themeMode == mode,
                    onClick = { settingsViewModel.onThemeModeChanged(mode) }
                )
                Text(
                    text = stringResource(mode.toStringResource()),
                    modifier = Modifier.align(Alignment.CenterVertically),
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))

    Row {
        Box(modifier = Modifier
            .background(Color(settings.player1Color.toULong()))
            .height(32.dp)
            .width(32.dp)
            .clickable(onClick = {
                isColorPickerVisible = true
                playerId = 0
                playerColor = Color(settings.player1Color.toULong())
            })
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = stringResource(Res.string.settings_player, 1),
            modifier = Modifier.align(Alignment.CenterVertically),
        )
    }
    Spacer(modifier = Modifier.height(16.dp))

    Row {
        Box(modifier = Modifier
            .background(Color(settings.player2Color.toULong()))
            .height(32.dp)
            .width(32.dp)
            .clickable(onClick = {
                isColorPickerVisible = true
                playerId = 1
                playerColor = Color(settings.player2Color.toULong())
            })
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = stringResource(Res.string.settings_player, 2),
            modifier = Modifier.align(Alignment.CenterVertically),
        )
    }
    Spacer(modifier = Modifier.height(16.dp))

    Row {
        Box(modifier = Modifier
            .background(Color(settings.player3Color.toULong()))
            .height(32.dp)
            .width(32.dp)
            .clickable(onClick = {
                isColorPickerVisible = true
                playerId = 2
                playerColor = Color(settings.player3Color.toULong())
            })
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = stringResource(Res.string.settings_player, 3),
            modifier = Modifier.align(Alignment.CenterVertically),
        )
    }
    Spacer(modifier = Modifier.height(16.dp))

    Row {
        Box(modifier = Modifier
            .background(Color(settings.player4Color.toULong()))
            .height(32.dp)
            .width(32.dp)
            .clickable(onClick = {
                isColorPickerVisible = true
                playerId = 3
                playerColor = Color(settings.player4Color.toULong())
            })
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = stringResource(Res.string.settings_player, 4),
            modifier = Modifier.align(Alignment.CenterVertically),
        )
    }
    Spacer(modifier = Modifier.height(16.dp))

    Row {
        Box(modifier = Modifier
            .background(Color(settings.player5Color.toULong()))
            .height(32.dp)
            .width(32.dp)
            .clickable(onClick = {
                isColorPickerVisible = true
                playerId = 4
                playerColor = Color(settings.player5Color.toULong())
            })
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = stringResource(Res.string.settings_player, 5),
            modifier = Modifier.align(Alignment.CenterVertically),
        )
    }
    Spacer(modifier = Modifier.height(16.dp))

    Row {
        Box(modifier = Modifier
            .background(Color(settings.player6Color.toULong()))
            .height(32.dp)
            .width(32.dp)
            .clickable(onClick = {
                isColorPickerVisible = true
                playerId = 5
                playerColor = Color(settings.player6Color.toULong())
            })
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = stringResource(Res.string.settings_player, 6),
            modifier = Modifier.align(Alignment.CenterVertically),
        )
    }
    Spacer(modifier = Modifier.height(16.dp))

    Row {
        Box(modifier = Modifier
            .background(Color(settings.player7Color.toULong()))
            .height(32.dp)
            .width(32.dp)
            .clickable(onClick = {
                isColorPickerVisible = true
                playerId = 6
                playerColor = Color(settings.player7Color.toULong())
            })
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = stringResource(Res.string.settings_player, 7),
            modifier = Modifier.align(Alignment.CenterVertically),
        )
    }
    Spacer(modifier = Modifier.height(16.dp))

    Row {
        Box(modifier = Modifier
            .background(Color(settings.player8Color.toULong()))
            .height(32.dp)
            .width(32.dp)
            .clickable(onClick = {
                isColorPickerVisible = true
                playerId = 7
                playerColor = Color(settings.player8Color.toULong())
            })
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = stringResource(Res.string.settings_player, 8),
            modifier = Modifier.align(Alignment.CenterVertically),
        )
    }
    Spacer(modifier = Modifier.height(16.dp))

    Row {
        Box(modifier = Modifier
            .background(Color(settings.player9Color.toULong()))
            .height(32.dp)
            .width(32.dp)
            .clickable(onClick = {
                isColorPickerVisible = true
                playerId = 8
                playerColor = Color(settings.player9Color.toULong())
            })
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = stringResource(Res.string.settings_player, 9),
            modifier = Modifier.align(Alignment.CenterVertically),
        )
    }
    Spacer(modifier = Modifier.height(16.dp))

    Row {
        Box(modifier = Modifier
            .background(Color(settings.player10Color.toULong()))
            .height(32.dp)
            .width(32.dp)
            .clickable(onClick = {
                isColorPickerVisible = true
                playerId = 9
                playerColor = Color(settings.player10Color.toULong())
            })
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = stringResource(Res.string.settings_player, 10),
            modifier = Modifier.align(Alignment.CenterVertically),
        )
    }

    if (isColorPickerVisible) {
        ColorPickerDialog(
            playerId = playerId!!,
            initialColor = playerColor!!,
            onDismiss = {
                isColorPickerVisible = false
                playerId = null
                playerColor = null
            },
            onSave = { playerId, color -> settingsViewModel.onPlayerColorChanged(playerId, color.value.toLong()) })
    }
}

@Composable
private fun ColorPickerDialog(playerId: Int, initialColor: Color, onDismiss:() -> Unit, onSave: (playerId: Int, color: Color) -> Unit) {
    val controller = rememberColorPickerController()
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = stringResource(Res.string.settings_color_title)) },
        text = {
            Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
                Box(modifier = Modifier.weight(5f).fillMaxWidth()) {
                    HsvColorPicker(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .padding(10.dp),
                        controller = controller,
                        initialColor = initialColor
                    )
                }

                BrightnessSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .height(35.dp)
                        .align(Alignment.CenterHorizontally),
                    controller = controller,
                )

                AlphaTile(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .align(Alignment.CenterHorizontally),
                    controller = controller,
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(playerId, controller.selectedColor.value)
                onDismiss()
            }) {
                Text(text = stringResource(Res.string.settings_color_select))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(Res.string.settings_color_cancel))
            }
        }
    )
}