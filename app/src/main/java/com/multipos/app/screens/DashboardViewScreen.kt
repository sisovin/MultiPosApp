package com.multipos.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

data class Stat(
    val label: String,
    val value: String,
    val change: String,
    val icon: ImageVector,
    val color: Color
)

data class RecentOrder(
    val id: String,
    val table: String,
    val items: Int,
    val total: String,
    val status: String,
    val time: String
)

data class Alert(
    val message: String,
    val type: String // "warning" or "danger"
)

// suppress the deprecated icon usage in one place to avoid the deprecation warning
@Suppress("DEPRECATION")
private val trendingUpIcon = Icons.Filled.TrendingUp

@Composable
fun DashboardViewScreen() {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val isCompact = screenWidthDp < 600

    val stats = listOf(
        Stat("Today's Sales", "$2,847.50", "+12.5%", Icons.Filled.AttachMoney, MaterialTheme.colorScheme.primary),
        Stat("Orders", "47", "+8", Icons.Filled.ShoppingCart, MaterialTheme.colorScheme.secondary),
        Stat("Active Tables", "12/20", "", Icons.Filled.People, MaterialTheme.colorScheme.tertiary),
        Stat("Avg. Ticket", "$60.58", "+5.2%", trendingUpIcon, MaterialTheme.colorScheme.primary)
    )

    val recentOrders = listOf(
        RecentOrder("#1047", "Table 5", 4, "$78.50", "Preparing", "2m ago"),
        RecentOrder("#1046", "Table 12", 2, "$34.00", "Served", "8m ago"),
        RecentOrder("#1045", "Table 3", 6, "$142.75", "Billing", "12m ago"),
        RecentOrder("#1044", "Takeaway", 1, "$15.00", "Ready", "15m ago"),
        RecentOrder("#1043", "Table 8", 3, "$55.25", "Served", "22m ago")
    )

    val alerts = listOf(
        Alert("Low stock: Salmon Fillet (3 left)", "warning"),
        Alert("Table 7 waiting 15+ min", "danger")
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Header
        Column {
            Text("Dashboard", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Real-time overview", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // Stats - responsive: horizontal scroll on small screens, grid-like weights on large screens
        if (isCompact) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(stats) { stat ->
                    StatCard(stat, modifier = Modifier.width(220.dp).height(120.dp))
                }
            }
        } else {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                stats.forEach { stat ->
                    StatCard(stat, modifier = Modifier.weight(1f).height(120.dp))
                }
            }
        }

        // Main content - stack on compact, side-by-side on wide
        if (isCompact) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxSize()) {
                // Recent Orders (full width)
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Recent Orders", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(8.dp))
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(recentOrders) { order ->
                                OrderItem(order)
                            }
                        }
                    }
                }

                // Alerts and Quick Stats (full width)
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("Alerts", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        alerts.forEach { alert ->
                            AlertItem(alert)
                        }

                        Spacer(Modifier.height(8.dp))

                        Text("Quick Stats", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        QuickStat("Table Occupancy", 0.6f, "60%")
                        QuickStat("Daily Target", 0.71f, "71%")
                    }
                }
            }
        } else {
            Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Recent Orders
                Card(modifier = Modifier.weight(2f).fillMaxHeight()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Recent Orders", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(8.dp))
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(recentOrders) { order ->
                                OrderItem(order)
                            }
                        }
                    }
                }

                // Alerts and Quick Stats
                Card(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("Alerts", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        alerts.forEach { alert ->
                            AlertItem(alert)
                        }

                        Spacer(Modifier.height(16.dp))

                        Text("Quick Stats", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        QuickStat("Table Occupancy", 0.6f, "60%")
                        QuickStat("Daily Target", 0.71f, "71%")
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(stat: Stat, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // icon with circular background for better legibility on small screens
                    Box(modifier = Modifier
                        .size(36.dp)
                        .background(color = stat.color.copy(alpha = 0.12f), shape = MaterialTheme.shapes.small), contentAlignment = Alignment.Center) {
                        Icon(stat.icon, contentDescription = stat.label, tint = stat.color)
                    }
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(stat.label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(stat.value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    }
                }

                if (stat.change.isNotEmpty()) {
                    Text(
                        stat.change,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.small).padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderItem(order: RecentOrder) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(order.id, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                // Combine table + items into a single line to prevent wrapping on small screens
                Text(
                    "${order.table} • ${order.items} items",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                order.status,
                style = MaterialTheme.typography.labelSmall,
                color = getStatusColor(order.status),
                modifier = Modifier.background(getStatusBackground(order.status), MaterialTheme.shapes.small).padding(horizontal = 6.dp, vertical = 2.dp)
            )
            Text(order.total, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Schedule, contentDescription = "Time", modifier = Modifier.size(12.dp))
                Text(order.time, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun getStatusColor(status: String): Color {
    return when (status) {
        "Preparing" -> MaterialTheme.colorScheme.tertiary
        "Served" -> MaterialTheme.colorScheme.primary
        "Billing" -> MaterialTheme.colorScheme.secondary
        "Ready" -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface
    }
}

@Composable
private fun getStatusBackground(status: String): Color {
    return when (status) {
        "Preparing" -> MaterialTheme.colorScheme.tertiaryContainer
        "Served" -> MaterialTheme.colorScheme.primaryContainer
        "Billing" -> MaterialTheme.colorScheme.secondaryContainer
        "Ready" -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
}

@Composable
private fun AlertItem(alert: Alert) {
    val color = if (alert.type == "warning") MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
    val background = if (alert.type == "warning") MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.errorContainer

    Row(modifier = Modifier.fillMaxWidth().background(background, MaterialTheme.shapes.medium).padding(12.dp), verticalAlignment = Alignment.Top) {
        Icon(Icons.Filled.Warning, contentDescription = "Alert", tint = color, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(8.dp))
        Text(alert.message, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun QuickStat(label: String, progress: Float, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(4.dp),
            color = if (label == "Table Occupancy") MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
        )
    }
}

@Preview(name = "Dashboard - Phone", widthDp = 360, heightDp = 800, showBackground = true)
@Composable
private fun DashboardPreviewPhone() {
    MaterialTheme {
        DashboardViewScreen()
    }
}

@Preview(name = "Dashboard - Tablet", widthDp = 900, heightDp = 1200, showBackground = true)
@Composable
private fun DashboardPreviewTablet() {
    MaterialTheme {
        DashboardViewScreen()
    }
}
