package com.multipos.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multipos.app.screens.InventoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class InventoryViewModel : ViewModel() {
    private val _inventory = MutableStateFlow<List<InventoryItem>>(listOf(
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
    ))
    val inventory: StateFlow<List<InventoryItem>> = _inventory

    private val _search = MutableStateFlow("")
    val search: StateFlow<String> = _search

    // Filtered list reacts to inventory and search
    val filtered = combine(_inventory, _search) { list, s ->
        if (s.isBlank()) list else list.filter { it.name.contains(s, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, _inventory.value)

    // Low stock items
    val lowStock = _inventory.map { list -> list.filter { it.stock <= it.minStock } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun setSearch(value: String) {
        _search.value = value
    }

    // Example mutation functions (no persistence) — useful for hooking up Edit/Receive
    fun updateStock(id: String, newStock: Int) {
        _inventory.value = _inventory.value.map { if (it.id == id) it.copy(stock = newStock) else it }
    }

    fun receiveStock(id: String, qty: Int) {
        _inventory.value = _inventory.value.map { if (it.id == id) it.copy(stock = it.stock + qty) else it }
    }
}

