package com.multipos.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.multipos.app.viewmodel.SettingsViewModel

// suppress deprecated icons for compatibility
@Suppress("DEPRECATION")
private val arrowForwardIcon = Icons.Filled.ArrowForward
@Suppress("DEPRECATION")
private val logoutIcon = Icons.Filled.Logout

@Composable
fun SettingsScreen(
    username: String = "Admin",
    role: String = "Manager",
    onSignOut: () -> Unit = {},
    viewModel: SettingsViewModel? = null
) {
    // viewModel (DataStore-backed)
    val vm = viewModel ?: viewModel<SettingsViewModel>()
    val state by vm.uiState.collectAsState()

    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val isCompact = screenWidthDp < 600

    // Local UI state mirrors DataStore state
    var notificationsEnabled by remember { mutableStateOf(state.notificationsEnabled) }
    var darkModeEnabled by remember { mutableStateOf(state.darkModeEnabled) }
    var autoPrintEnabled by remember { mutableStateOf(state.autoPrintEnabled) }
    var lowStockAlerts by remember { mutableStateOf(state.lowStockAlerts) }
    var pin by remember { mutableStateOf(state.pin) }
    var showPin by remember { mutableStateOf(false) }
    var appVersion by remember { mutableStateOf(state.appVersion) }

    LaunchedEffect(state) {
        notificationsEnabled = state.notificationsEnabled
        darkModeEnabled = state.darkModeEnabled
        autoPrintEnabled = state.autoPrintEnabled
        lowStockAlerts = state.lowStockAlerts
        pin = state.pin
        appVersion = state.appVersion
    }

    val contentModifier = if (isCompact) Modifier.fillMaxSize().padding(16.dp) else Modifier.fillMaxSize().padding(24.dp)

    Column(modifier = contentModifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(imageVector = Icons.Filled.Settings, contentDescription = "Settings", modifier = Modifier.size(28.dp))
            Column {
                Text("Settings", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Manage account, preferences and app settings", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        if (isCompact) {
            // Mobile: stack sections vertically
            AccountCard(username = username, role = role, modifier = Modifier.fillMaxWidth())
            PreferencesCard(
                notificationsEnabled = notificationsEnabled,
                onNotificationsChange = { notificationsEnabled = it; vm.setNotifications(it) },
                darkModeEnabled = darkModeEnabled,
                onDarkModeChange = { darkModeEnabled = it; vm.setDarkMode(it) },
                autoPrintEnabled = autoPrintEnabled,
                onAutoPrintChange = { autoPrintEnabled = it; vm.setAutoPrint(it) },
                lowStockAlerts = lowStockAlerts,
                onLowStockAlertsChange = { lowStockAlerts = it; vm.setLowStockAlerts(it) },
                modifier = Modifier.fillMaxWidth()
            )

            SecurityCard(pin = pin, showPin = showPin, onPinChange = { pin = it; vm.setPin(it) }, onToggleShowPin = { showPin = !showPin }, modifier = Modifier.fillMaxWidth())

            AppInfoCard(appVersion = appVersion, onSignOut = onSignOut, modifier = Modifier.fillMaxWidth())
        } else {
            // Tablet / wide: two-column layout
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    AccountCard(username = username, role = role, modifier = Modifier.fillMaxWidth())
                    SecurityCard(pin = pin, showPin = showPin, onPinChange = { pin = it; vm.setPin(it) }, onToggleShowPin = { showPin = !showPin }, modifier = Modifier.fillMaxWidth())
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    PreferencesCard(
                        notificationsEnabled = notificationsEnabled,
                        onNotificationsChange = { notificationsEnabled = it; vm.setNotifications(it) },
                        darkModeEnabled = darkModeEnabled,
                        onDarkModeChange = { darkModeEnabled = it; vm.setDarkMode(it) },
                        autoPrintEnabled = autoPrintEnabled,
                        onAutoPrintChange = { autoPrintEnabled = it; vm.setAutoPrint(it) },
                        lowStockAlerts = lowStockAlerts,
                        onLowStockAlertsChange = { lowStockAlerts = it; vm.setLowStockAlerts(it) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    AppInfoCard(appVersion = appVersion, onSignOut = onSignOut, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
private fun AccountCard(username: String, role: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.size(56.dp).background(MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                Text(text = username.firstOrNull()?.uppercase() ?: "", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(username, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(role, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = { /* navigate to profile */ }) {
                Icon(imageVector = arrowForwardIcon, contentDescription = "Edit profile")
            }
        }
    }
}

@Composable
private fun PreferencesCard(
    notificationsEnabled: Boolean,
    onNotificationsChange: (Boolean) -> Unit,
    darkModeEnabled: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    autoPrintEnabled: Boolean,
    onAutoPrintChange: (Boolean) -> Unit,
    lowStockAlerts: Boolean,
    onLowStockAlertsChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Preferences", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Column { Text("Notifications", style = MaterialTheme.typography.bodyMedium); Text("Receive order & system alerts", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                Switch(checked = notificationsEnabled, onCheckedChange = onNotificationsChange)
            }

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Column { Text("Dark mode", style = MaterialTheme.typography.bodyMedium); Text("Reduce eye strain and save power", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                Switch(checked = darkModeEnabled, onCheckedChange = onDarkModeChange)
            }

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Column { Text("Auto-print receipts", style = MaterialTheme.typography.bodyMedium); Text("Automatically print customer receipts", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                Switch(checked = autoPrintEnabled, onCheckedChange = onAutoPrintChange)
            }

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Column { Text("Low stock alerts", style = MaterialTheme.typography.bodyMedium); Text("Notify when inventory is low", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                Switch(checked = lowStockAlerts, onCheckedChange = onLowStockAlertsChange)
            }
        }
    }
}

@Composable
private fun SecurityCard(pin: String, showPin: Boolean, onPinChange: (String) -> Unit, onToggleShowPin: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Security", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

            OutlinedTextField(value = pin, onValueChange = onPinChange, label = { Text("Login PIN") }, visualTransformation = if (showPin) VisualTransformation.None else PasswordVisualTransformation(), singleLine = true, modifier = Modifier.fillMaxWidth())
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { /* save pin */ }, modifier = Modifier.weight(1f)) { Text("Save PIN") }
                OutlinedButton(onClick = onToggleShowPin, modifier = Modifier.weight(1f)) { Text(if (showPin) "Hide" else "Show") }
            }

            HorizontalDivider()

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column { Text("App lock", style = MaterialTheme.typography.bodyMedium); Text("Require PIN to open app", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                Switch(checked = false, onCheckedChange = {})
            }
        }
    }
}

@Composable
private fun AppInfoCard(appVersion: String, onSignOut: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("App", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = Icons.Filled.Info, contentDescription = "Version")
                    Column { Text("Version", style = MaterialTheme.typography.bodyMedium); Text(appVersion, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                }
                TextButton(onClick = { /* check updates */ }) { Text("Check updates") }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Sign out", style = MaterialTheme.typography.bodyMedium)
                TextButton(onClick = onSignOut) { Icon(imageVector = logoutIcon, contentDescription = "Logout"); Spacer(Modifier.width(6.dp)); Text("Logout") }
            }
        }
    }
}

@Preview(name = "Settings - Phone", widthDp = 360, heightDp = 800, showBackground = true)
@Composable
private fun SettingsPreviewPhone() {
    MaterialTheme {
        SettingsScreen()
    }
}

@Preview(name = "Settings - Tablet", widthDp = 900, heightDp = 1200, showBackground = true)
@Composable
private fun SettingsPreviewTablet() {
    MaterialTheme {
        SettingsScreen()
    }
}
