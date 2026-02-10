package com.multipos.core.data.remote.service

import com.multipos.core.data.remote.dto.*
import retrofit2.http.*

interface StoreService {
    @GET("/api/v1/stores")
    suspend fun getStores(
        @Header("Authorization") token: String
    ): ApiResponseDto<List<StoreResponseDto>>
    
    @GET("/api/v1/stores/{id}")
    suspend fun getStore(
        @Path("id") storeId: String,
        @Header("Authorization") token: String
    ): ApiResponseDto<StoreResponseDto>
    
    @POST("/api/v1/stores")
    suspend fun createStore(
        @Body request: StoreCreateRequestDto,
        @Header("Authorization") token: String
    ): ApiResponseDto<StoreResponseDto>
    
    @GET("/api/v1/stores/{id}/summary")
    suspend fun getStoreSummary(
        @Path("id") storeId: String,
        @Header("Authorization") token: String
    ): ApiResponseDto<StoreSummaryDto>
}

data class StoreSummaryDto(
    val storeId: String,
    val name: String,
    val totalInventoryValue: Double,
    val lowStockItems: Int,
    val todaySales: Double,
    val totalTransactions: Int,
    val averageTransactionValue: Double
)