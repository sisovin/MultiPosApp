package com.multipos.app.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.rotate
import androidx.lifecycle.viewmodel.compose.viewModel
import com.multipos.app.viewmodel.BillingViewModel

enum class BillStatus {
    OPEN, PAID, VOID
}

enum class PaymentMethod {
    CARD, CASH, MOBILE
}

data class Bill(
    val id: String,
    val table: String,
    val items: Int,
    val subtotal: Double,
    val tax: Double,
    val total: Double,
    val status: BillStatus,
    val method: PaymentMethod? = null,
    val time: String
)

@Composable
fun BillingViewScreen() {
    val vm: BillingViewModel = viewModel()
    val search by vm.search.collectAsState()
    val filtered by vm.filtered.collectAsState()
    val todayTotal by vm.todayTotal.collectAsState()
    val openCount by vm.openCount.collectAsState()

    val configuration = LocalConfiguration.current
    val isCompact = configuration.screenWidthDp < 600

    var showAddDialog by remember { mutableStateOf(false) }
    var newTable by remember { mutableStateOf("") }
    var newItems by remember { mutableStateOf("") }
    var newTotal by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add bill")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Header and stats
            if (isCompact) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Billing", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Text("Invoices & payments", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { /* filter or settings */ }) { Icon(Icons.Filled.MoreVert, contentDescription = "More") }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        StatCard(value = "$${todayTotal.format(2)}", label = "Collected", color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))
                        StatCard(value = openCount.toString(), label = "Open", color = MaterialTheme.colorScheme.tertiary, modifier = Modifier.width(100.dp))
                    }
                }
            } else {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("Billing", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text("Invoices & payments", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard("$${todayTotal.format(2)}", "Collected Today", MaterialTheme.colorScheme.primary)
                        StatCard(openCount.toString(), "Open Bills", MaterialTheme.colorScheme.tertiary)
                    }
                }
            }

            // Search
            OutlinedTextField(
                value = search,
                onValueChange = { vm.setSearch(it) },
                placeholder = { Text("Search invoices...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                modifier = Modifier.fillMaxWidth()
            )

            // Bills List
            if (isCompact) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filtered) { bill ->
                        BillCardCompact(bill)
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filtered) { bill ->
                        BillCard(bill)
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add new bill") },
            text = {
                Column {
                    OutlinedTextField(value = newTable, onValueChange = { newTable = it }, label = { Text("Table or customer") })
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = newItems, onValueChange = { newItems = it }, label = { Text("Items count") })
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = newTotal, onValueChange = { newTotal = it }, label = { Text("Total amount") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val id = "INV-${System.currentTimeMillis() % 100000}"
                    val itemsCount = newItems.toIntOrNull() ?: 1
                    val totalAmount = newTotal.toDoubleOrNull() ?: 0.0
                    val newBill = Bill(id, if (newTable.isBlank()) "Walk-in" else newTable, itemsCount, totalAmount, 0.0, totalAmount, BillStatus.OPEN, null, "Now")
                    vm.addBill(newBill)
                    newTable = ""; newItems = ""; newTotal = ""; showAddDialog = false
                }) { Text("Add") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun StatCard(value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier.width(120.dp)) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun BillCard(bill: Bill, billingVm: com.multipos.app.viewmodel.BillingViewModel = viewModel()) {
    var showPayConfirm by remember { mutableStateOf(false) }
    var showVoidConfirm by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth().clickable { /* View bill */ }) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Receipt, contentDescription = "Receipt", tint = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(bill.id, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                    StatusBadge(bill.status)
                }
                Text("${bill.table} · ${bill.items} items · ${bill.time}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text("$${bill.total.format(2)}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                if (bill.method != null) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(getMethodIcon(bill.method), contentDescription = bill.method.name, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(bill.method.name.lowercase().replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(Modifier.width(12.dp))

            // Pay button with confirmation
            IconButton(onClick = { showPayConfirm = true }) {
                Icon(Icons.Filled.CreditCard, contentDescription = "Pay ${bill.id}")
            }

            // Void button with confirmation
            IconButton(onClick = { showVoidConfirm = true }) {
                Icon(Icons.Filled.Delete, contentDescription = "Void ${bill.id}")
            }

            IconButton(onClick = { /* View bill */ }) {
                Icon(Icons.Filled.Visibility, contentDescription = "View")
            }
        }
    }

    if (showPayConfirm) {
        AlertDialog(
            onDismissRequest = { showPayConfirm = false },
            title = { Text("Confirm payment") },
            text = { Text("Mark ${bill.id} as paid using card?") },
            confirmButton = {
                TextButton(onClick = {
                    billingVm.markPaid(bill.id, com.multipos.app.screens.PaymentMethod.CARD)
                    showPayConfirm = false
                }) { Text("Confirm") }
            },
            dismissButton = { TextButton(onClick = { showPayConfirm = false }) { Text("Cancel") } }
        )
    }

    if (showVoidConfirm) {
        AlertDialog(
            onDismissRequest = { showVoidConfirm = false },
            title = { Text("Confirm void") },
            text = { Text("Void bill ${bill.id}? This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    billingVm.voidBill(bill.id)
                    showVoidConfirm = false
                }) { Text("Void") }
            },
            dismissButton = { TextButton(onClick = { showVoidConfirm = false }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun BillCardCompact(bill: Bill, billingVm: com.multipos.app.viewmodel.BillingViewModel = viewModel()) {
    var expanded by remember { mutableStateOf(false) }
    var showPayConfirm by remember { mutableStateOf(false) }
    var showVoidConfirm by remember { mutableStateOf(false) }

    // animate expand/collapse smoothly
    val rotation by animateFloatAsState(targetValue = if (expanded) 180f else 0f, animationSpec = tween(durationMillis = 300))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { expanded = !expanded }
            .animateContentSize(animationSpec = tween(300))
            .semantics { contentDescription = "Bill ${bill.id}, ${bill.table}, ${bill.items} items" }
            .testTag("bill_${bill.id}"),
        ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(bill.id, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                        StatusBadge(bill.status)
                    }
                    Text("${bill.table} • ${bill.items} items", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("$${bill.total.format(2)}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    Text(bill.time, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Spacer(Modifier.width(8.dp))

                // Quick actions - card-level accessible buttons with clear content descriptions
                IconButton(
                    onClick = { showPayConfirm = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Filled.CreditCard, contentDescription = "Pay bill ${bill.id}")
                }

                IconButton(onClick = { /* View */ }, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Filled.Visibility, contentDescription = "View bill ${bill.id}")
                }

                // Expand indicator
                Icon(
                    imageVector = Icons.Filled.ExpandMore,
                    contentDescription = if (expanded) "Collapse details" else "Expand details",
                    modifier = Modifier
                        .size(28.dp)
                        .rotate(rotation)
                )
            }

            // Expanded details area (accessible, animated via animateContentSize)
            if (expanded) {
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Subtotal: $${bill.subtotal.format(2)}", style = MaterialTheme.typography.labelSmall)
                    Text("Tax: $${bill.tax.format(2)}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.weight(1f))
                    // Secondary actions
                    TextButton(onClick = { showVoidConfirm = true }) { Text("Void") }
                }
            }
        }
    }

    if (showPayConfirm) {
        AlertDialog(
            onDismissRequest = { showPayConfirm = false },
            title = { Text("Confirm payment") },
            text = { Text("Mark ${bill.id} as paid using card?") },
            confirmButton = {
                TextButton(onClick = {
                    billingVm.markPaid(bill.id, com.multipos.app.screens.PaymentMethod.CARD)
                    showPayConfirm = false
                }) { Text("Confirm") }
            },
            dismissButton = { TextButton(onClick = { showPayConfirm = false }) { Text("Cancel") } }
        )
    }

    if (showVoidConfirm) {
        AlertDialog(
            onDismissRequest = { showVoidConfirm = false },
            title = { Text("Confirm void") },
            text = { Text("Void bill ${bill.id}? This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    billingVm.voidBill(bill.id)
                    showVoidConfirm = false
                }) { Text("Void") }
            },
            dismissButton = { TextButton(onClick = { showVoidConfirm = false }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun StatusBadge(status: BillStatus) {
    val (backgroundColor, textColor) = when (status) {
        BillStatus.OPEN -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        BillStatus.PAID -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        BillStatus.VOID -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
    }

    Box(
        modifier = Modifier
            .background(backgroundColor, CircleShape)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(status.name.lowercase().replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.labelSmall, color = textColor)
    }
}

private fun getMethodIcon(method: PaymentMethod): androidx.compose.ui.graphics.vector.ImageVector {
    return when (method) {
        PaymentMethod.CARD -> Icons.Filled.CreditCard
        PaymentMethod.CASH -> Icons.Filled.Money
        PaymentMethod.MOBILE -> Icons.Filled.Smartphone
    }
}

private fun Double.format(digits: Int) = "%.${digits}f".format(this)
