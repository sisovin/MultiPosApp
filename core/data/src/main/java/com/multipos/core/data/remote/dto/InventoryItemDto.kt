package com.multipos.core.data.remote.dto

import com.google.gson.annotations.SerializedName

data class InventoryItemResponseDto(
    val id: String,
    @SerializedName("store_id")
    val storeId: String,
    val name: String,
    val sku: String,
    val category: String,
    @SerializedName("unit_of_measure")
    val unitOfMeasure: String, // pcs, kg, ltr, etc.
    @SerializedName("current_stock")
    val currentStock: Double,
    @SerializedName("reorder_level")
    val reorderLevel: Double,
    @SerializedName("unit_cost")
    val unitCost: Double,
    @SerializedName("selling_price")
    val sellingPrice: Double,
    val supplier: SupplierDto?,
    @SerializedName("last_stock_update")
    val lastStockUpdate: String,
    @SerializedName("is_active")
    val isActive: Boolean = true,
    @SerializedName("created_at")
    val createdAt: String
)

data class StockMovementRequestDto(
    @SerializedName("item_id")
    val itemId: String,
    val quantity: Double,
    val type: String, // IN, OUT, ADJUSTMENT
    val reason: String, // PURCHASE, SALE, DAMAGED, THEFT, RECOUNT, etc.
    @SerializedName("reference_number")
    val referenceNumber: String? = null,
    val notes: String? = null,
    @SerializedName("created_by")
    val createdBy: String
)

data class StockMovementResponseDto(
    val id: String,
    @SerializedName("item_id")
    val itemId: String,
    val quantity: Double,
    val type: String,
    val reason: String,
    @SerializedName("reference_number")
    val referenceNumber: String?,
    val notes: String?,
    @SerializedName("previous_stock")
    val previousStock: Double,
    @SerializedName("new_stock")
    val newStock: Double,
    @SerializedName("created_by")
    val createdBy: String,
    @SerializedName("created_at")
    val createdAt: String
)

data class SupplierDto(
    val id: String,
    val name: String,
    val email: String?,
    val phone: String?,
    val address: String?,
    @SerializedName("payment_terms")
    val paymentTerms: String? = null,
    @SerializedName("is_active")
    val isActive: Boolean = true
)