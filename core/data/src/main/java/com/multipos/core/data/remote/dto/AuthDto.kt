package com.multipos.core.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Request DTOs
 */
data class LoginRequestDto(
    val email: String,
    val password: String,
    @SerializedName("device_id")
    val deviceId: String = UUID.randomUUID().toString()
)

data class RefreshTokenRequestDto(
    @SerializedName("refresh_token")
    val refreshToken: String
)

/**
 * Response DTOs
 */
data class AuthResponseDto(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String,
    @SerializedName("expires_in")
    val expiresIn: Long, // seconds
    @SerializedName("token_type")
    val tokenType: String = "Bearer",
    val user: UserResponseDto
)

data class UserResponseDto(
    val id: String,
    val email: String,
    val name: String,
    val role: String, // ADMIN, STORE_MANAGER, CASHIER, INVENTORY_STAFF
    val avatar: String?,
    val status: String = "ACTIVE",
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)

data class ErrorResponseDto(
    val code: String,
    val message: String,
    val details: Map<String, Any>? = null,
    val timestamp: String = System.currentTimeMillis().toString()
)