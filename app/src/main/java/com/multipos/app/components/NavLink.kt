package com.multipos.app.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A very small Compose-friendly NavLink helper to mimic React Router NavLink behavior.
 * It doesn't handle routing itself; use it as a styled clickable wrapper and integrate
 * with your navigation solution (e.g., Navigation Compose) by calling the onClick {}.
 */
@Composable
fun NavLink(
    label: String,
    isActive: Boolean = false,
    onClick: () -> Unit = {}
) {
    val style = if (isActive) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall
    Box(modifier = Modifier.clickable(onClick = onClick).padding(8.dp)) {
        Text(text = label, style = style)
    }
}

