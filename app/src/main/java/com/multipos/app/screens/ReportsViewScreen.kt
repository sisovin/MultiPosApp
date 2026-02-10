package com.multipos.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class DailySales(
    val day: String,
    val sales: Int
)

data class TopItem(
    val name: String,
    val qty: Int,
    val revenue: Double
)

@Composable
fun ReportsViewScreen() {
    val weekData = listOf(
        DailySales("Mon", 2100),
        DailySales("Tue", 1850),
        DailySales("Wed", 2400),
        DailySales("Thu", 2200),
        DailySales("Fri", 3100),
        DailySales("Sat", 3800),
        DailySales("Sun", 2900)
    )
    val maxSales = weekData.maxOf { it.sales }

    val topItems = listOf(
        TopItem("Ribeye Steak", 28, 980.0),
        TopItem("Pasta Carbonara", 35, 647.5),
        TopItem("Grilled Salmon", 22, 616.0),
        TopItem("Latte", 64, 320.0),
        TopItem("House Wine", 31, 310.0)
    )

    val weeklyRevenue = weekData.sumOf { it.sales }
    val totalOrders = 312
    val avgOrderValue = weeklyRevenue.toDouble() / totalOrders

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Header
        Column {
            Text("Reports", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Weekly performance overview", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // Summary Cards
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SummaryCard("$${weeklyRevenue}", "Weekly Revenue", Icons.Filled.AttachMoney, MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))
            SummaryCard(totalOrders.toString(), "Total Orders", Icons.Filled.ShoppingCart, MaterialTheme.colorScheme.secondary, modifier = Modifier.weight(1f))
            SummaryCard("$${avgOrderValue.format(2)}", "Avg. Order Value", Icons.Filled.TrendingUp, MaterialTheme.colorScheme.tertiary, modifier = Modifier.weight(1f))
        }

        // Charts and Top Items
        Row(modifier = Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // Bar Chart
            Card(modifier = Modifier.weight(1f).fillMaxHeight()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Filled.BarChart, contentDescription = "Bar Chart", tint = MaterialTheme.colorScheme.primary)
                        Text("Daily Sales", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Bottom) {
                        weekData.forEach { data ->
                            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
                                Text("${(data.sales / 1000.0).format(1)}k", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(24.dp)
                                        .height((data.sales.toFloat() / maxSales * 120).dp)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight()
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(data.day, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            // Top Items
            Card(modifier = Modifier.weight(1f).fillMaxHeight()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Top Selling Items", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(16.dp))
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        itemsIndexed(topItems) { index, item ->
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .background(MaterialTheme.colorScheme.secondary, androidx.compose.foundation.shape.CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text((index + 1).toString(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondary, fontWeight = FontWeight.Bold)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                    Text("${item.qty} sold", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text("$${item.revenue.toInt()}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(value: String, label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private fun Double.format(digits: Int) = "%.${digits}f".format(this)
