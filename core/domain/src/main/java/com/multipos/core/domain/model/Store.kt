package com.multipos.core.domain.model

data class Store(
    val id: String,
    val name: String,
    val type: StoreType,
    val address: String,
    val phone: String? = null,
    val email: String? = null,
    val timezone: String = "UTC",
    val currency: String = "USD",
    val taxRate: Double = 0.0,
    val isActive: Boolean = true,
    val createdAt: String,
    val updatedAt: String
)

enum class StoreType {
    RESTAURANT,
    RETAIL,
    CAFE
}