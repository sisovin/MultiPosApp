package com.multipos.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multipos.app.screens.Bill
import com.multipos.app.screens.BillStatus
import com.multipos.app.screens.PaymentMethod
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BillingViewModel : ViewModel() {
    private val _bills = MutableStateFlow<List<Bill>>(listOf(
        Bill("INV-1047", "Table 5", 4, 71.36, 7.14, 78.50, BillStatus.OPEN, null, "12:45 PM"),
        Bill("INV-1046", "Table 12", 2, 30.91, 3.09, 34.00, BillStatus.PAID, PaymentMethod.CARD, "12:32 PM"),
        Bill("INV-1045", "Table 3", 6, 129.77, 12.98, 142.75, BillStatus.OPEN, null, "12:15 PM"),
        Bill("INV-1044", "Takeaway", 1, 13.64, 1.36, 15.00, BillStatus.PAID, PaymentMethod.CASH, "12:05 PM"),
        Bill("INV-1043", "Table 8", 3, 50.23, 5.02, 55.25, BillStatus.PAID, PaymentMethod.MOBILE, "11:48 AM"),
        Bill("INV-1042", "Table 7", 5, 89.09, 8.91, 98.00, BillStatus.OPEN, null, "11:30 AM")
    ))

    private val _search = MutableStateFlow("")
    val search: StateFlow<String> = _search

    val filtered = combine(_bills, _search) { list, s ->
        if (s.isBlank()) list else list.filter { it.id.contains(s, ignoreCase = true) || it.table.contains(s, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, _bills.value)

    val todayTotal = _bills.map { list -> list.filter { it.status == BillStatus.PAID }.sumOf { it.total } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    val openCount = _bills.map { list -> list.count { it.status == BillStatus.OPEN } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    fun setSearch(value: String) {
        _search.value = value
    }

    fun markPaid(id: String, method: PaymentMethod) {
        viewModelScope.launch {
            _bills.value = _bills.value.map { if (it.id == id) it.copy(status = BillStatus.PAID, method = method) else it }
        }
    }

    fun voidBill(id: String) {
        viewModelScope.launch {
            _bills.value = _bills.value.map { if (it.id == id) it.copy(status = BillStatus.VOID) else it }
        }
    }

    fun addBill(bill: Bill) {
        viewModelScope.launch {
            _bills.value = listOf(bill) + _bills.value
        }
    }
}
