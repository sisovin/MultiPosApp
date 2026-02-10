package com.multipos.app.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

// suppress deprecated icon usage (Icons.Filled.Logout) on platforms where AutoMirrored isn't available
@Suppress("DEPRECATION")
private val logoutIcon = Icons.Filled.Logout

@Composable
fun POSSidebar(
    currentView: POSView,
    onNavigate: (POSView) -> Unit,
    storeName: String,
    username: String,
    role: String,
    onLogout: () -> Unit,
    collapsed: Boolean,
    onToggleCollapse: () -> Unit
) {
    // Internal collapsed state: default to expanded (false) when user is logged in (username non-empty)
    // We keep a saveable state keyed by username so that after login the sidebar shows expanded
    val internalCollapsedKey = username.ifEmpty { "__anon__" }
    var internalCollapsed by rememberSaveable(internalCollapsedKey) {
        // If username is non-empty (user is logged in), initialize to false (expanded)
        mutableStateOf(if (username.isNotBlank()) false else collapsed)
    }

    val navItems = listOf(
        NavItem(POSView.DASHBOARD, "Dashboard", Icons.Filled.GridView),
        NavItem(POSView.TABLES, "Tables", Icons.Filled.RestaurantMenu),
        NavItem(POSView.ORDERS, "Orders", Icons.Filled.Receipt),
        NavItem(POSView.INVENTORY, "Inventory", Icons.Filled.Inventory),
        NavItem(POSView.BILLING, "Billing", Icons.Filled.Receipt),
        NavItem(POSView.REPORTS, "Reports", Icons.Filled.BarChart),
        NavItem(POSView.SETTINGS, "Settings", Icons.Filled.Settings)
    )

    Column(
        modifier = Modifier
            .width(if (internalCollapsed) 64.dp else 224.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Store,
                    contentDescription = "Store",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            AnimatedVisibility(visible = !internalCollapsed) {
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    Text(
                        text = "MultiPOS",
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = storeName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = {
                // toggle internal state and inform parent
                internalCollapsed = !internalCollapsed
                onToggleCollapse()
            }) {
                Icon(
                    imageVector = if (internalCollapsed) Icons.Filled.ChevronRight else Icons.Filled.ChevronLeft,
                    contentDescription = "Toggle sidebar"
                )
            }
        }

        // Navigation
        Column(modifier = Modifier.weight(1f).padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            navItems.forEach { item ->
                NavigationButton(
                    item = item,
                    isSelected = currentView == item.view,
                    collapsed = internalCollapsed,
                    onClick = { onNavigate(item.view) }
                )
            }
        }

        // User
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = username.firstOrNull()?.uppercase() ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
            AnimatedVisibility(visible = !internalCollapsed) {
                Column(modifier = Modifier.padding(start = 8.dp).weight(1f)) {
                    Text(
                        text = username,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = role,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onLogout) {
                Icon(
                    imageVector = logoutIcon,
                    contentDescription = "Logout",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun NavigationButton(
    item: NavItem,
    isSelected: Boolean,
    collapsed: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            tint = contentColor
        )
        AnimatedVisibility(visible = !collapsed) {
            Text(
                text = item.label,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
    }
}
