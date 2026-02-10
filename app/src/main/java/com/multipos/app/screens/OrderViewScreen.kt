package com.multipos.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.multipos.app.viewmodel.OrderViewModel

// suppress deprecated icon usage across Compose versions
@Suppress("DEPRECATION")
private val arrowBackIcon = Icons.Filled.ArrowBack
@Suppress("DEPRECATION")
private val sendIcon = Icons.Filled.Send
@Suppress("DEPRECATION")
private val creditCardIcon = Icons.Filled.CreditCard

data class MenuItem(
    val id: String,
    val name: String,
    val price: Double,
    val category: String
)

data class OrderItem(
    val id: String,
    val name: String,
    val price: Double,
    val category: String,
    val quantity: Int
)

@Composable
fun OrderViewScreen(tableId: Int?, onBack: () -> Unit, vm: OrderViewModel = viewModel()) {
    // responsive breakpoint
    val configuration = LocalConfiguration.current
    val isCompact = configuration.screenWidthDp < 600

    // ViewModel-backed state
    val category by vm.category.collectAsState()
    val orderItems by vm.orderItems.collectAsState()
    val orderPanelExpanded by vm.panelExpanded.collectAsState()

    val menuCategories = listOf("Popular", "Appetizers", "Mains", "Drinks", "Desserts")

    val menuItems = listOf(
        MenuItem("m1", "Caesar Salad", 12.5, "Appetizers"),
        MenuItem("m2", "Bruschetta", 9.0, "Appetizers"),
        MenuItem("m3", "Soup of the Day", 8.5, "Appetizers"),
        MenuItem("m4", "Grilled Salmon", 28.0, "Mains"),
        MenuItem("m5", "Ribeye Steak", 35.0, "Mains"),
        MenuItem("m6", "Chicken Parmesan", 22.0, "Mains"),
        MenuItem("m7", "Pasta Carbonara", 18.5, "Mains"),
        MenuItem("m8", "Fish & Chips", 19.0, "Mains"),
        MenuItem("m9", "Espresso", 3.5, "Drinks"),
        MenuItem("m10", "Latte", 5.0, "Drinks"),
        MenuItem("m11", "Fresh Juice", 6.5, "Drinks"),
        MenuItem("m12", "Craft Beer", 8.0, "Drinks"),
        MenuItem("m13", "House Wine", 10.0, "Drinks"),
        MenuItem("m14", "Tiramisu", 11.0, "Desserts"),
        MenuItem("m15", "Crème Brûlée", 10.5, "Desserts"),
        MenuItem("m16", "Chocolate Cake", 9.5, "Desserts")
    )

    val popularIds = listOf("m4", "m5", "m7", "m10", "m14")

    val displayItems = if (category == "Popular") {
        menuItems.filter { popularIds.contains(it.id) }
    } else {
        menuItems.filter { it.category == category }
    }

    val addItem = { item: MenuItem -> vm.addItem(item) }

    val updateQty = { id: String, delta: Int -> vm.updateQty(id, delta) }

    val removeItem = { id: String -> vm.removeItem(id) }

    // Totals derived in VM as StateFlows — read them here
    val subtotal by vm.subtotal.collectAsState()
    val tax by vm.tax.collectAsState()
    val total by vm.total.collectAsState()

    if (isCompact) {
        // Mobile: menu on top, collapsible order panel at bottom
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = arrowBackIcon, contentDescription = "Back")
                }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (tableId != null) "Table $tableId" else "New Order",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Select items to add",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Categories
            ScrollableTabRow(
                selectedTabIndex = menuCategories.indexOf(category),
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 12.dp
            ) {
                menuCategories.forEach { cat ->
                    Tab(
                        selected = category == cat,
                        onClick = { vm.setCategory(cat) },
                        text = { Text(cat, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            // Menu grid (scrollable)
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 140.dp),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(displayItems.size) { idx ->
                    val item = displayItems[idx]
                    val inOrder = orderItems.find { it.id == item.id }
                    MenuItemCard(item, inOrder?.quantity ?: 0, onClick = { addItem(item) })
                }
            }

            // Collapsible Order panel
            Box(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    tonalElevation = 6.dp,
                    shadowElevation = 6.dp,
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = if (orderPanelExpanded) 220.dp else 56.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Drag handle / header
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .clickable { vm.togglePanel() }
                            .padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier
                                .size(width = 40.dp, height = 4.dp)
                                .clip(MaterialTheme.shapes.small)
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)))
                            Spacer(Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Current Order", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                Text("${orderItems.size} items", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (!orderPanelExpanded) {
                                Text("$${total.format(2)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (orderPanelExpanded) {
                            // Order items
                            LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                if (orderItems.isEmpty()) {
                                    item {
                                        Box(modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 28.dp), contentAlignment = Alignment.Center) {
                                            Text("No items yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                } else {
                                    items(orderItems) { item ->
                                        OrderItemRow(item, onUpdateQty = { delta -> updateQty(item.id, delta) }, onRemove = { removeItem(item.id) })
                                    }
                                }
                            }

                            // Totals & actions
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Subtotal", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("$${subtotal.format(2)}", style = MaterialTheme.typography.bodySmall)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Tax (10%)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("$${tax.format(2)}", style = MaterialTheme.typography.bodySmall)
                                }
                                HorizontalDivider()
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Total", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text("$${total.format(2)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                }

                                Spacer(Modifier.height(12.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(onClick = { /* Send to Kitchen */ }, enabled = orderItems.isNotEmpty(), modifier = Modifier.weight(1f)) {
                                        Icon(imageVector = sendIcon, contentDescription = "Send", modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(6.dp))
                                        Text("Send", style = MaterialTheme.typography.labelSmall)
                                    }
                                    Button(onClick = { /* Pay */ }, enabled = orderItems.isNotEmpty(), modifier = Modifier.weight(1f)) {
                                        Icon(imageVector = creditCardIcon, contentDescription = "Pay", modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(6.dp))
                                        Text("Pay", style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    } else {
        // Wide: side-by-side layout
        Row(modifier = Modifier.fillMaxSize()) {
            // Menu Side
            Column(modifier = Modifier.weight(1f)) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = arrowBackIcon, contentDescription = "Back")
                    }
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (tableId != null) "Table $tableId" else "New Order",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Select items to add",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Categories
                ScrollableTabRow(
                    selectedTabIndex = menuCategories.indexOf(category),
                    modifier = Modifier.fillMaxWidth(),
                    edgePadding = 16.dp
                ) {
                    menuCategories.forEach { cat ->
                        Tab(
                            selected = category == cat,
                            onClick = { vm.setCategory(cat) },
                            text = { Text(cat, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                // Menu Grid
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 120.dp),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(displayItems.size) { idx ->
                        val item = displayItems[idx]
                        val inOrder = orderItems.find { it.id == item.id }
                        MenuItemCard(item, inOrder?.quantity ?: 0, onClick = { addItem(item) })
                    }
                }
            }

            // Order Panel
            OrderPanel(
                orderItems = orderItems,
                subtotal = subtotal,
                tax = tax,
                total = total,
                onUpdateQty = { id, delta -> updateQty(id, delta) },
                onRemove = { id -> removeItem(id) }
            )
        }
    }
}

@Composable
private fun OrderPanel(
    orderItems: List<OrderItem>,
    subtotal: Double,
    tax: Double,
    total: Double,
    onUpdateQty: (String, Int) -> Unit,
    onRemove: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .width(320.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Current Order", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(text = "${orderItems.size} items", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            if (orderItems.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp), contentAlignment = Alignment.Center) {
                        Text(text = "No items yet", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                items(orderItems) { item ->
                    OrderItemRow(item, onUpdateQty = { delta -> onUpdateQty(item.id, delta) }, onRemove = { onRemove(item.id) })
                }
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Subtotal", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("$${subtotal.format(2)}", style = MaterialTheme.typography.bodySmall)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Tax (10%)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("$${tax.format(2)}", style = MaterialTheme.typography.bodySmall)
            }
            HorizontalDivider()
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text("$${total.format(2)}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { /* Send to Kitchen */ }, enabled = orderItems.isNotEmpty(), modifier = Modifier.weight(1f)) {
                    Icon(imageVector = sendIcon, contentDescription = "Send", modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Send", style = MaterialTheme.typography.labelSmall)
                }
                Button(onClick = { /* Pay */ }, enabled = orderItems.isNotEmpty(), modifier = Modifier.weight(1f)) {
                    Icon(imageVector = creditCardIcon, contentDescription = "Pay", modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Pay", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
private fun MenuItemCard(item: MenuItem, quantity: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (quantity > 0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        )
    ) {
        Box(modifier = Modifier.padding(12.dp)) {
            if (quantity > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(20.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = quantity.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            Column {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "$${item.price.format(2)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun OrderItemRow(item: OrderItem, onUpdateQty: (Int) -> Unit, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.small)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "$${item.price.format(2)} × ${item.quantity}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(onClick = { onUpdateQty(-1) }, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Filled.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
            }
            Text(
                text = item.quantity.toString(),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.width(24.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            IconButton(onClick = { onUpdateQty(1) }, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Filled.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
            }
            IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Filled.Delete, contentDescription = "Remove", modifier = Modifier.size(16.dp))
            }
        }
    }
}

private fun Double.format(digits: Int) = "%.${digits}f".format(this)

@Preview(name = "Order - Phone", widthDp = 360, heightDp = 800, showBackground = true)
@Composable
private fun OrderPreviewPhone() {
    MaterialTheme {
        OrderViewScreen(tableId = 5, onBack = {})
    }
}

@Preview(name = "Order - Tablet", widthDp = 900, heightDp = 1200, showBackground = true)
@Composable
private fun OrderPreviewTablet() {
    MaterialTheme {
        OrderViewScreen(tableId = 5, onBack = {})
    }
}
