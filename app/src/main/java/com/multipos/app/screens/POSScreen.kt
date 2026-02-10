package com.multipos.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.multipos.app.components.POSSidebar
import com.multipos.app.components.POSView
import com.multipos.app.screens.SettingsScreen

@Composable
fun POSScreen(
    storeName: String,
    username: String,
    role: String,
    onLogout: () -> Unit
) {
    var currentView by remember { mutableStateOf(POSView.DASHBOARD) }
    var collapsed by remember { mutableStateOf(false) }
    var selectedTableId by remember { mutableStateOf<Int?>(null) }

    Row(modifier = Modifier.fillMaxSize()) {
        POSSidebar(
            currentView = currentView,
            onNavigate = { currentView = it },
            storeName = storeName,
            username = username,
            role = role,
            onLogout = onLogout,
            collapsed = collapsed,
            onToggleCollapse = { collapsed = !collapsed }
        )

        // Main content
        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
            when (currentView) {
                POSView.DASHBOARD -> DashboardViewScreen()
                POSView.TABLES -> TablesViewScreen(onSelectTable = { tableId ->
                    selectedTableId = tableId
                    currentView = POSView.ORDERS
                })
                POSView.ORDERS -> OrderViewScreen(tableId = selectedTableId, onBack = {
                    currentView = POSView.TABLES
                    selectedTableId = null
                })
                POSView.INVENTORY -> InventoryViewScreen()
                POSView.BILLING -> BillingViewScreen()
                POSView.REPORTS -> ReportsViewScreen()
                POSView.SETTINGS -> SettingsScreen(username = username, role = role, onSignOut = onLogout)
            }
        }
    }
}
