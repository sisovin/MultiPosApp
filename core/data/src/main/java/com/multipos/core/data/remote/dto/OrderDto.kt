package com.multipos.core.data.remote.dto

import com.google.gson.annotations.SerializedName

data class OrderResponseDto(
    val id: String,
    @SerializedName("store_id")
    val storeId: String,
    @SerializedName("table_id")
    val tableId: String?,
    val status: String, // PENDING, IN_PROGRESS, READY, COMPLETED, CANCELLED
    val items: List<OrderItemDto>,
    @SerializedName("created_by")
    val createdBy: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("completed_at")
    val completedAt: String? = null
)

data class OrderItemDto(
    val id: String,
    @SerializedName("menu_item_id")
    val menuItemId: String,
    val name: String,
    val quantity: Int,
    @SerializedName("unit_price")
    val unitPrice: Double,
    val modifiers: List<ModifierDto>? = null,
    val notes: String? = null,
    val status: String = "PENDING" // PENDING, READY, SERVED
)

data class ModifierDto(
    val id: String,
    val name: String,
    @SerializedName("additional_price")
    val additionalPrice: Double = 0.0
)

data class CreateOrderRequestDto(
    @SerializedName("store_id")
    val storeId: String,
    @SerializedName("table_id")
    val tableId: String?,
    val type: String, // DINE_IN, TAKEOUT, DELIVERY
    @SerializedName("created_by")
    val createdBy: String
)

data class AddOrderItemRequestDto(
    @SerializedName("menu_item_id")
    val menuItemId: String,
    val quantity: Int,
    val modifiers: List<ModifierDto>? = null,
    val notes: String? = null
)