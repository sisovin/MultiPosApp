package com.multipos.core.domain.model

data class Order(
    val id: String,
    val storeId: String,
    val tableId: String? = null,
    val status: OrderStatus,
    val items: List<OrderItem>,
    val createdBy: String,
    val createdAt: String,
    val updatedAt: String,
    val completedAt: String? = null
) {
    val subtotal: Double
        get() = items.sumOf { it.quantity * it.unitPrice }
    
    val itemCount: Int
        get() = items.sumOf { it.quantity }
}

data class OrderItem(
    val id: String,
    val menuItemId: String,
    val name: String,
    val quantity: Int,
    val unitPrice: Double,
    val modifiers: List<Modifier> = emptyList(),
    val notes: String? = null,
    val status: OrderItemStatus = OrderItemStatus.PENDING
) {
    val lineTotal: Double
        get() = quantity * unitPrice + modifiers.sumOf { it.additionalPrice * quantity }
}

data class Modifier(
    val id: String,
    val name: String,
    val additionalPrice: Double = 0.0
)

enum class OrderStatus {
    PENDING,
    IN_PROGRESS,
    READY,
    COMPLETED,
    CANCELLED
}

enum class OrderItemStatus {
    PENDING,
    READY,
    SERVED
}