package com.multipos.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multipos.app.screens.MenuItem
import com.multipos.app.screens.OrderItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OrderViewModel : ViewModel() {
    private val _orderItems = MutableStateFlow<List<OrderItem>>(emptyList())
    val orderItems: StateFlow<List<OrderItem>> = _orderItems

    private val _category = MutableStateFlow("Popular")
    val category: StateFlow<String> = _category

    private val _panelExpanded = MutableStateFlow(true)
    val panelExpanded: StateFlow<Boolean> = _panelExpanded

    // Derived totals as StateFlows
    val subtotal = _orderItems
        .map { list -> list.sumOf { it.price * it.quantity } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    val tax = subtotal
        .map { it * 0.1 }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    val total = subtotal
        .map { it + it * 0.1 }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    fun setCategory(cat: String) {
        _category.value = cat
    }

    fun togglePanel() {
        _panelExpanded.value = !_panelExpanded.value
    }

    fun addItem(item: MenuItem) {
        viewModelScope.launch {
            val existing = _orderItems.value.find { it.id == item.id }
            _orderItems.value = if (existing != null) {
                _orderItems.value.map { if (it.id == item.id) it.copy(quantity = it.quantity + 1) else it }
            } else {
                _orderItems.value + OrderItem(item.id, item.name, item.price, item.category, 1)
            }
            _panelExpanded.value = true
        }
    }

    fun updateQty(id: String, delta: Int) {
        viewModelScope.launch {
            _orderItems.value = _orderItems.value.map {
                if (it.id == id) it.copy(quantity = (it.quantity + delta).coerceAtLeast(0)) else it
            }.filter { it.quantity > 0 }
        }
    }

    fun removeItem(id: String) {
        viewModelScope.launch {
            _orderItems.value = _orderItems.value.filter { it.id != id }
        }
    }
}
