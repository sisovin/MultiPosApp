package com.multipos.core.data.remote.service

import com.multipos.core.data.remote.dto.*
import retrofit2.http.*

interface AuthService {
    @POST("/api/v1/auth/login")
    suspend fun login(@Body request: LoginRequestDto): AuthResponseDto
    
    @POST("/api/v1/auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequestDto): AuthResponseDto
    
    @POST("/api/v1/auth/logout")
    suspend fun logout(@Header("Authorization") token: String): ApiResponseDto<Unit>
    
    @GET("/api/v1/auth/me")
    suspend fun getCurrentUser(@Header("Authorization") token: String): UserResponseDto
    
    @POST("/api/v1/auth/validate")
    suspend fun validateToken(@Header("Authorization") token: String): ApiResponseDto<Boolean>
}

data class ApiResponseDto<T>(
    val success: Boolean,
    val data: T? = null,
    val error: ErrorResponseDto? = null
)