package org.darchacheron.gofirst.settings


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import gofirst.composeapp.generated.resources.Res
import gofirst.composeapp.generated.resources.ic_back
import gofirst.composeapp.generated.resources.ic_reset
import gofirst.composeapp.generated.resources.ic_save
import gofirst.composeapp.generated.resources.settings_content_description_back
import gofirst.composeapp.generated.resources.settings_content_description_reset
import gofirst.composeapp.generated.resources.settings_content_description_save
import gofirst.composeapp.generated.resources.settings_theme
import gofirst.composeapp.generated.resources.settings_title
import kotlinx.coroutines.launch
import org.darchacheron.gofirst.ui.UiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView(settingsViewModel: SettingsViewModel = koinInject()) {
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
//                        navHostController.navigateUp()
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
//                        settingsViewModel.saveSettings { navHostController.navigateUp() }
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
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
}

