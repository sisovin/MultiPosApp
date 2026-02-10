package com.multipos.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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

enum class TableStatus {
    AVAILABLE, OCCUPIED, BILLING, RESERVED
}

data class TableData(
    val id: Int,
    val name: String,
    val seats: Int,
    val status: TableStatus,
    val guests: Int? = null,
    val order: String? = null,
    val total: String? = null,
    val time: String? = null
)

@Composable
fun TablesViewScreen(onSelectTable: (Int) -> Unit) {
    var filter by remember { mutableStateOf<TableStatus?>(null) } // null for all

    val tables = listOf(
        TableData(1, "T1", 2, TableStatus.AVAILABLE),
        TableData(2, "T2", 2, TableStatus.OCCUPIED, 2, "#1040", "$45.00", "32m"),
        TableData(3, "T3", 4, TableStatus.BILLING, 4, "#1045", "$142.75", "55m"),
        TableData(4, "T4", 4, TableStatus.AVAILABLE),
        TableData(5, "T5", 4, TableStatus.OCCUPIED, 3, "#1047", "$78.50", "12m"),
        TableData(6, "T6", 6, TableStatus.AVAILABLE),
        TableData(7, "T7", 6, TableStatus.OCCUPIED, 5, "#1042", "$98.00", "45m"),
        TableData(8, "T8", 4, TableStatus.OCCUPIED, 3, "#1043", "$55.25", "38m"),
        TableData(9, "T9", 2, TableStatus.RESERVED),
        TableData(10, "T10", 8, TableStatus.AVAILABLE),
        TableData(11, "T11", 4, TableStatus.OCCUPIED, 4, "#1041", "$67.00", "50m"),
        TableData(12, "T12", 2, TableStatus.OCCUPIED, 2, "#1046", "$34.00", "20m")
    )

    val filtered = if (filter == null) tables else tables.filter { it.status == filter }
    val counts = mapOf(
        null to tables.size,
        TableStatus.AVAILABLE to tables.count { it.status == TableStatus.AVAILABLE },
        TableStatus.OCCUPIED to tables.count { it.status == TableStatus.OCCUPIED },
        TableStatus.BILLING to tables.count { it.status == TableStatus.BILLING },
        TableStatus.RESERVED to tables.count { it.status == TableStatus.RESERVED }
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Header
        Column {
            Text("Tables", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Manage restaurant floor", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // Filters
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(null to "All", TableStatus.AVAILABLE to "Available", TableStatus.OCCUPIED to "Occupied", TableStatus.BILLING to "Billing", TableStatus.RESERVED to "Reserved").forEach { (status, label) ->
                OutlinedButton(
                    onClick = { filter = status },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (filter == status) MaterialTheme.colorScheme.primary else Color.Transparent,
                        contentColor = if (filter == status) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text("$label (${counts[status]})", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        // Table Grid
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 120.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(filtered) { table ->
                TableCard(table, onSelectTable)
            }
        }

        // Legend
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            listOf(TableStatus.AVAILABLE, TableStatus.OCCUPIED, TableStatus.BILLING, TableStatus.RESERVED).forEach { status ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(getStatusColor(status), CircleShape)
                    )
                    Text(getStatusLabel(status), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun TableCard(table: TableData, onSelectTable: (Int) -> Unit) {
    val borderColor = getStatusBorderColor(table.status)
    val backgroundColor = getStatusBackgroundColor(table.status)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onSelectTable(table.id) },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = androidx.compose.foundation.BorderStroke(2.dp, borderColor)
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            // Status dot
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(8.dp)
                    .background(getStatusColor(table.status), CircleShape)
            )

            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(table.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.People, contentDescription = "Seats", modifier = Modifier.size(12.dp))
                        Text("${table.guests ?: 0}/${table.seats}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                if (table.status == TableStatus.OCCUPIED || table.status == TableStatus.BILLING) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Schedule, contentDescription = "Time", modifier = Modifier.size(12.dp))
                            Text(table.time ?: "", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.AttachMoney, contentDescription = "Total", modifier = Modifier.size(12.dp))
                            Text(table.total ?: "", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun getStatusColor(status: TableStatus): Color {
    return when (status) {
        TableStatus.AVAILABLE -> Color.Green
        TableStatus.OCCUPIED -> Color.Yellow
        TableStatus.BILLING -> Color.Blue
        TableStatus.RESERVED -> Color.Gray
    }
}

@Composable
private fun getStatusBorderColor(status: TableStatus): Color {
    return when (status) {
        TableStatus.AVAILABLE -> Color.Green.copy(alpha = 0.3f)
        TableStatus.OCCUPIED -> Color.Yellow.copy(alpha = 0.3f)
        TableStatus.BILLING -> Color.Blue.copy(alpha = 0.3f)
        TableStatus.RESERVED -> Color.Gray.copy(alpha = 0.3f)
    }
}

@Composable
private fun getStatusBackgroundColor(status: TableStatus): Color {
    return when (status) {
        TableStatus.AVAILABLE -> Color.Green.copy(alpha = 0.05f)
        TableStatus.OCCUPIED -> Color.Yellow.copy(alpha = 0.05f)
        TableStatus.BILLING -> Color.Blue.copy(alpha = 0.05f)
        TableStatus.RESERVED -> Color.Gray.copy(alpha = 0.05f)
    }
}

private fun getStatusLabel(status: TableStatus): String {
    return when (status) {
        TableStatus.AVAILABLE -> "Available"
        TableStatus.OCCUPIED -> "Occupied"
        TableStatus.BILLING -> "Billing"
        TableStatus.RESERVED -> "Reserved"
    }
}
