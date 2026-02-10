package com.multipos.app.components

import androidx.compose.ui.graphics.vector.ImageVector

enum class POSView {
    DASHBOARD, TABLES, ORDERS, INVENTORY, BILLING, REPORTS, SETTINGS
}

data class NavItem(val view: POSView, val label: String, val icon: ImageVector)

