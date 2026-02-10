package com.multipos.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

data class Store(val id: String, val name: String, val type: String)

@Composable
fun LoginScreen(onLogin: (String, String, String) -> Unit) {
    var username by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var selectedStore by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("Cashier") }

    // Enforce these roles
    val roles = listOf("Admin", "Manager", "Cashier")

    val stores = listOf(
        Store("store-1", "Downtown Bistro", "Restaurant"),
        Store("store-2", "Market Fresh", "Retail"),
        Store("store-3", "Harbor Café", "Restaurant")
    )

    val selectedStoreName = stores.find { it.id == selectedStore }?.name ?: ""

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Helper to validate PIN and other fields
    fun attemptLogin() {
        if (pin == "123456" && roles.contains(selectedRole) && selectedStoreName.isNotBlank() && username.isNotBlank()) {
            onLogin(username, selectedRole, selectedStoreName)
        } else {
            scope.launch {
                val message = when {
                    username.isBlank() -> "Enter a username"
                    selectedStoreName.isBlank() -> "Select a store"
                    selectedRole.isBlank() -> "Select a role"
                    pin != "123456" -> "Invalid PIN"
                    else -> "Invalid credentials"
                }
                snackbarHostState.showSnackbar(message)
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(64.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                shadowElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Store,
                        contentDescription = "Store Icon",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Text("MultiPOS", style = MaterialTheme.typography.headlineMedium)
            Text("Multi-Store Point of Sale", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(32.dp))

            // Demo Info
            Card(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Demo Credentials: Username: \"demo\", PIN: \"123456\", Store: \"Downtown Bistro\", Role: \"Admin\"",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Spacer(Modifier.height(16.dp))

            // Store Selection
            Text("Select Store", style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(8.dp))
            stores.forEach { store ->
                OutlinedButton(
                    onClick = { selectedStore = store.id },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (selectedStore == store.id) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                    )
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(store.name, style = MaterialTheme.typography.bodyLarge)
                        Text(store.type, style = MaterialTheme.typography.bodySmall)
                    }
                    if (selectedStore == store.id) {
                        Icon(Icons.Filled.ChevronRight, "Selected")
                    }
                }
                Spacer(Modifier.height(4.dp))
            }
            Spacer(Modifier.height(16.dp))

            // Username
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                leadingIcon = { Icon(Icons.Filled.Person, "User") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            // Role
            Text("Role", style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                roles.forEach { role ->
                    OutlinedButton(
                        onClick = { selectedRole = role },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (selectedRole == role) MaterialTheme.colorScheme.primary else Color.Transparent
                        )
                    ) {
                        Text(role, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))

            // PIN
            Text("PIN Code", style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = pin,
                onValueChange = { if (it.length <= 6) pin = it.filter { ch -> ch.isDigit() } },
                label = { Text("PIN") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            // Demo Login Button
            OutlinedButton(
                onClick = {
                    username = "demo"
                    pin = "123456"
                    selectedStore = "store-1"
                    selectedRole = "Admin"
                    attemptLogin()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("🚀 Quick Demo Login (Admin)")
            }
            Spacer(Modifier.height(16.dp))

            // Login Button
            Button(
                onClick = { attemptLogin() },
                modifier = Modifier.fillMaxWidth(),
                enabled = username.isNotBlank() && pin.length >= 4 && selectedStore.isNotBlank()
            ) {
                Text("Sign In", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
