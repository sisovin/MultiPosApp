package com.multipos.core.data.remote.dto

import com.google.gson.annotations.SerializedName

data class StoreResponseDto(
    val id: String,
    val name: String,
    val type: String, // RESTAURANT, RETAIL, CAFE
    val address: String,
    val phone: String?,
    val email: String?,
    val timezone: String = "UTC",
    val currency: String = "USD",
    @SerializedName("tax_rate")
    val taxRate: Double = 0.0,
    @SerializedName("is_active")
    val isActive: Boolean = true,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)

data class StoreCreateRequestDto(
    val name: String,
    val type: String,
    val address: String,
    val phone: String? = null,
    val email: String? = null,
    val timezone: String = "UTC",
    val currency: String = "USD",
    @SerializedName("tax_rate")
    val taxRate: Double = 0.0
)