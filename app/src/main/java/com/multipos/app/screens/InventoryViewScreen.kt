package com.multipos.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

data class InventoryItem(
    val id: String,
    val name: String,
    val category: String,
    val stock: Int,
    val unit: String,
    val minStock: Int,
    val cost: Double,
    val lastUpdated: String
)

@Composable
fun InventoryViewScreen() {
    var search by remember { mutableStateOf("") }

    val inventory = listOf(
        InventoryItem("i1", "Salmon Fillet", "Proteins", 3, "kg", 5, 24.0, "2h ago"),
        InventoryItem("i2", "Ribeye Steak", "Proteins", 12, "pcs", 8, 18.5, "1h ago"),
        InventoryItem("i3", "Chicken Breast", "Proteins", 20, "kg", 10, 8.0, "3h ago"),
        InventoryItem("i4", "Romaine Lettuce", "Produce", 8, "heads", 5, 2.5, "4h ago"),
        InventoryItem("i5", "Tomatoes", "Produce", 15, "kg", 8, 3.0, "2h ago"),
        InventoryItem("i6", "Pasta (Spaghetti)", "Dry Goods", 25, "kg", 10, 2.0, "1d ago"),
        InventoryItem("i7", "Olive Oil", "Dry Goods", 6, "L", 4, 12.0, "2d ago"),
        InventoryItem("i8", "Espresso Beans", "Beverages", 4, "kg", 5, 22.0, "6h ago"),
        InventoryItem("i9", "House Red Wine", "Beverages", 18, "bottles", 12, 8.0, "1d ago"),
        InventoryItem("i10", "Craft IPA", "Beverages", 24, "cans", 12, 4.0, "5h ago")
    )

    val filtered = inventory.filter { it.name.contains(search, ignoreCase = true) }
    val lowStock = inventory.filter { it.stock <= it.minStock }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Header
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Inventory", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("${inventory.size} items tracked", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (lowStock.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.shapes.medium)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Filled.Warning, contentDescription = "Warning", tint = MaterialTheme.colorScheme.tertiary)
                    Text("${lowStock.size} low stock", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onTertiaryContainer)
                }
            }
        }

        // Search
        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            placeholder = { Text("Search inventory...") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
            modifier = Modifier.fillMaxWidth()
        )

        // Table
        Card(modifier = Modifier.fillMaxWidth().weight(1f)) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TableHeader("Item", Modifier.weight(1f))
                    TableHeader("Category", Modifier.width(100.dp))
                    TableHeader("Stock", Modifier.width(80.dp))
                    TableHeader("Min", Modifier.width(60.dp))
                    TableHeader("Unit Cost", Modifier.width(80.dp))
                    TableHeader("Updated", Modifier.width(80.dp))
                }

                // Rows
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filtered) { item ->
                        InventoryRow(item)
                    }
                }
            }
        }
    }
}

@Composable
private fun TableHeader(text: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Start
        )
        Spacer(Modifier.width(4.dp))
        Icon(Icons.Filled.Sort, contentDescription = "Sort", modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun InventoryRow(item: InventoryItem) {
    val isLow = item.stock <= item.minStock

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Item
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(
                Icons.Filled.Inventory,
                contentDescription = "Item",
                tint = if (isLow) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Text(item.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            if (isLow) {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.tertiaryContainer, CircleShape)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("LOW", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onTertiaryContainer)
                }
            }
        }

        // Category
        Text(item.category, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(100.dp))

        // Stock
        Text(
            "${item.stock} ${item.unit}",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = if (isLow) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.width(80.dp),
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
        )

        // Min
        Text(
            item.minStock.toString(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(60.dp),
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
        )

        // Unit Cost
        Text(
            "$${item.cost.format(2)}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.width(80.dp),
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
        )

        // Updated
        Text(
            item.lastUpdated,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(80.dp)
        )
    }
}

private fun Double.format(digits: Int) = "%.${digits}f".format(this)
