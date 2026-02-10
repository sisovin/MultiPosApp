package com.multipos.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.multipos.app.viewmodel.InventoryViewModel

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
fun InventoryViewScreen(vm: InventoryViewModel = viewModel()) {
    val search by vm.search.collectAsState()
    val filtered by vm.filtered.collectAsState()
    val lowStock by vm.lowStock.collectAsState()
    val inventory by vm.inventory.collectAsState()

    val configuration = LocalConfiguration.current
    val isCompact = configuration.screenWidthDp < 600

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
            onValueChange = { vm.setSearch(it) },
            placeholder = { Text("Search inventory...") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
            modifier = Modifier.fillMaxWidth()
        )

        if (isCompact) {
            // Mobile: card list with expandable rows
            LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filtered) { item ->
                    InventoryCard(item = item)
                }
            }
        } else {
            // Wide: table-style layout, allow horizontal scrolling when needed
            Card(modifier = Modifier.fillMaxWidth().weight(1f)) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header (horizontally scrollable)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        TableHeader("Item", Modifier.width(300.dp))
                        TableHeader("Category", Modifier.width(140.dp))
                        TableHeader("Stock", Modifier.width(100.dp))
                        TableHeader("Min", Modifier.width(80.dp))
                        TableHeader("Unit Cost", Modifier.width(120.dp))
                        TableHeader("Updated", Modifier.width(120.dp))
                    }

                    // Rows
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(filtered) { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Item column wider
                                Column(modifier = Modifier.width(300.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(Icons.Filled.Inventory, contentDescription = "Item", modifier = Modifier.size(16.dp))
                                        Text(item.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                        if (item.stock <= item.minStock) {
                                            Box(modifier = Modifier
                                                .background(MaterialTheme.colorScheme.tertiaryContainer, CircleShape)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)) {
                                                Text("LOW", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onTertiaryContainer)
                                            }
                                        }
                                    }
                                }

                                Text(item.category, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(140.dp))

                                Text(
                                    "${item.stock} ${item.unit}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (item.stock <= item.minStock) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.width(100.dp)
                                )

                                Text(item.minStock.toString(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(80.dp))

                                Text("$${item.cost.format(2)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.width(120.dp))

                                Text(item.lastUpdated, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(120.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InventoryCard(item: InventoryItem, vm: InventoryViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    var expanded by remember { mutableStateOf(false) }
    var showEdit by remember { mutableStateOf(false) }
    var showReceive by remember { mutableStateOf(false) }
    var editValue by remember { mutableStateOf(item.stock.toString()) }
    var receiveValue by remember { mutableStateOf("1") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Filled.Inventory, contentDescription = "Item", modifier = Modifier.size(18.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    Text(item.category, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("${item.stock} ${item.unit}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    Text("$${item.cost.format(2)}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            if (expanded) {
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Min: ${item.minStock}", style = MaterialTheme.typography.labelSmall)
                    Text("Updated: ${item.lastUpdated}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = { editValue = item.stock.toString(); showEdit = true }) { Icon(Icons.Filled.Edit, contentDescription = "Edit") }
                    IconButton(onClick = { receiveValue = "1"; showReceive = true }) { Icon(Icons.Filled.AddBox, contentDescription = "Receive") }
                }
            }
        }
    }

    if (showEdit) {
        AlertDialog(
            onDismissRequest = { showEdit = false },
            confirmButton = {
                TextButton(onClick = {
                    val newStock = editValue.toIntOrNull()
                    if (newStock != null) vm.updateStock(item.id, newStock)
                    showEdit = false
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showEdit = false }) { Text("Cancel") } },
            title = { Text("Edit stock") },
            text = {
                Column {
                    Text("Set the new stock for ${item.name}")
                    OutlinedTextField(value = editValue, onValueChange = { editValue = it }, singleLine = true)
                }
            }
        )
    }

    if (showReceive) {
        AlertDialog(
            onDismissRequest = { showReceive = false },
            confirmButton = {
                TextButton(onClick = {
                    val qty = receiveValue.toIntOrNull() ?: 0
                    if (qty > 0) vm.receiveStock(item.id, qty)
                    showReceive = false
                }) { Text("Receive") }
            },
            dismissButton = { TextButton(onClick = { showReceive = false }) { Text("Cancel") } },
            title = { Text("Receive stock") },
            text = {
                Column {
                    Text("Add received quantity for ${item.name}")
                    OutlinedTextField(value = receiveValue, onValueChange = { receiveValue = it }, singleLine = true)
                }
            }
        )
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
        // Suppress deprecation warning for Sort icon
        @Suppress("Deprecation")
        Icon(imageVector = Icons.Filled.Sort, contentDescription = "Sort", modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private fun Double.format(digits: Int) = "%.${digits}f".format(this)
