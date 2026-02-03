package org.darchacheron.gofirst.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import gofirst.composeapp.generated.resources.*
import gofirst.composeapp.generated.resources.Res
import gofirst.composeapp.generated.resources.arrow_drop_down

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView(viewModel: SettingsViewModel = koinInject()) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.settings_title)) }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is SettingsUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is SettingsUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }
                is SettingsUiState.Success -> {
                    SettingsContent(
                        settings = state.settings,
                        onThemeModeSelected = viewModel::onThemeModeSelected
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsContent(
    settings: Settings,
    onThemeModeSelected: (ThemeMode) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Theme Section
        Column {
            Text(
                text = stringResource(Res.string.settings_theme),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            ThemeSelection(
                selectedTheme = settings.themeMode,
                onThemeSelected = onThemeModeSelected
            )
        }
    }
}

@Composable
private fun ThemeSelection(
    selectedTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit
) {
    Column {
        ThemeMode.entries.forEach { mode ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onThemeSelected(mode) }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (mode == selectedTheme),
                    onClick = { onThemeSelected(mode) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(mode.toStringResource()))
            }
        }
    }
}
