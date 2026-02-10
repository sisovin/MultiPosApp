package com.multipos.core.domain.model

data class InventoryItem(
    val id: String,
    val storeId: String,
    val name: String,
    val sku: String,
    val category: String,
    val unitOfMeasure: String,
    val currentStock: Double,
    val reorderLevel: Double,
    val unitCost: Double,
    val sellingPrice: Double,
    val supplier: Supplier? = null,
    val lastStockUpdate: String,
    val isActive: Boolean = true,
    val createdAt: String
) {
    val isLowStock: Boolean
        get() = currentStock <= reorderLevel
    
    val stockValue: Double
        get() = currentStock * unitCost
}

data class Supplier(
    val id: String,
    val name: String,
    val email: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val paymentTerms: String? = null,
    val isActive: Boolean = true
)

data class StockMovement(
    val id: String,
    val itemId: String,
    val quantity: Double,
    val type: StockMovementType,
    val reason: String,
    val referenceNumber: String? = null,
    val notes: String? = null,
    val previousStock: Double,
    val newStock: Double,
    val createdBy: String,
    val createdAt: String
)

enum class StockMovementType {
    IN,     // Stock received
    OUT,    // Stock removed
    ADJUSTMENT // Inventory correction
}