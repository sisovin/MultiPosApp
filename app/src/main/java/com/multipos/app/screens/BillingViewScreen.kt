package com.multipos.app.screens

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

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
    var search by remember { mutableStateOf("") }

    val bills = listOf(
        Bill("INV-1047", "Table 5", 4, 71.36, 7.14, 78.50, BillStatus.OPEN, null, "12:45 PM"),
        Bill("INV-1046", "Table 12", 2, 30.91, 3.09, 34.00, BillStatus.PAID, PaymentMethod.CARD, "12:32 PM"),
        Bill("INV-1045", "Table 3", 6, 129.77, 12.98, 142.75, BillStatus.OPEN, null, "12:15 PM"),
        Bill("INV-1044", "Takeaway", 1, 13.64, 1.36, 15.00, BillStatus.PAID, PaymentMethod.CASH, "12:05 PM"),
        Bill("INV-1043", "Table 8", 3, 50.23, 5.02, 55.25, BillStatus.PAID, PaymentMethod.MOBILE, "11:48 AM"),
        Bill("INV-1042", "Table 7", 5, 89.09, 8.91, 98.00, BillStatus.OPEN, null, "11:30 AM")
    )

    val filtered = bills.filter {
        it.id.contains(search, ignoreCase = true) || it.table.contains(search, ignoreCase = true)
    }

    val todayTotal = bills.filter { it.status == BillStatus.PAID }.sumOf { it.total }
    val openCount = bills.count { it.status == BillStatus.OPEN }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Header
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

        // Search
        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            placeholder = { Text("Search invoices...") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
            modifier = Modifier.fillMaxWidth()
        )

        // Bills List
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filtered) { bill ->
                BillCard(bill)
            }
        }
    }
}

@Composable
private fun StatCard(value: String, label: String, color: Color) {
    Card(modifier = Modifier.width(120.dp)) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun BillCard(bill: Bill) {
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

            IconButton(onClick = { /* View bill */ }) {
                Icon(Icons.Filled.Visibility, contentDescription = "View")
            }
        }
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
