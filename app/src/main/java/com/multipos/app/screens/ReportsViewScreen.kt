package com.multipos.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview

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
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val isCompact = screenWidthDp < 600

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

        // Summary Cards: horizontal scroll on compact, row on wide
        if (isCompact) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listOf(
                    Pair("$${weeklyRevenue}", "Weekly Revenue"),
                    Pair(totalOrders.toString(), "Total Orders"),
                    Pair("$${avgOrderValue.format(2)}", "Avg. Order Value")
                )) { pair ->
                    SummaryCard(pair.first, pair.second, Icons.Filled.AttachMoney, MaterialTheme.colorScheme.primary, modifier = Modifier.width(260.dp))
                }
            }
        } else {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SummaryCard("$${weeklyRevenue}", "Weekly Revenue", Icons.Filled.AttachMoney, MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))
                SummaryCard(totalOrders.toString(), "Total Orders", Icons.Filled.ShoppingCart, MaterialTheme.colorScheme.secondary, modifier = Modifier.weight(1f))
                SummaryCard("$${avgOrderValue.format(2)}", "Avg. Order Value", Icons.Filled.TrendingUp, MaterialTheme.colorScheme.tertiary, modifier = Modifier.weight(1f))
            }
        }

        // Charts and Top Items: stacked on compact, side-by-side on wide
        if (isCompact) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxSize()) {
                BarChartCard(weekData = weekData, maxSales = maxSales, chartHeight = 160.dp)
                TopItemsCard(topItems = topItems)
            }
        } else {
            Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                BarChartCard(weekData = weekData, maxSales = maxSales, modifier = Modifier.weight(1f))
                TopItemsCard(topItems = topItems, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun SummaryCard(value: String, label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(24.dp))
                Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun BarChartCard(weekData: List<DailySales>, maxSales: Int, modifier: Modifier = Modifier, chartHeight: Dp = 140.dp) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Filled.BarChart, contentDescription = "Bar Chart", tint = MaterialTheme.colorScheme.primary)
                Text("Daily Sales", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Bottom) {
                weekData.forEach { data ->
                    val barHeight = (data.sales.toFloat() / maxSales * chartHeight.value).dp
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
                        Text("${(data.sales / 1000.0).format(1)}k", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(4.dp))
                        Box(modifier = Modifier.width(24.dp).height(barHeight)) {
                            Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)))
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(data.day, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun TopItemsCard(topItems: List<TopItem>, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Top Selling Items", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(12.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                itemsIndexed(topItems) { index, item ->
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.size(28.dp).background(MaterialTheme.colorScheme.secondary, CircleShape), contentAlignment = Alignment.Center) {
                            Text((index + 1).toString(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondary, fontWeight = FontWeight.Bold)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("${item.qty} sold", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text("$${item.revenue.toInt()}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}

private fun Double.format(digits: Int) = "%.${digits}f".format(this)

@Preview(name = "Reports - Phone", widthDp = 360, heightDp = 800, showBackground = true)
@Composable
private fun ReportsPreviewPhone() {
    MaterialTheme {
        ReportsViewScreen()
    }
}

@Preview(name = "Reports - Tablet", widthDp = 900, heightDp = 1200, showBackground = true)
@Composable
private fun ReportsPreviewTablet() {
    MaterialTheme {
        ReportsViewScreen()
    }
}
