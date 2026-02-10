package com.multipos.core.data.remote.service

import com.multipos.core.data.remote.dto.*
import retrofit2.http.*

interface InventoryService {
    @GET("/api/v1/stores/{storeId}/inventory")
    suspend fun getInventory(
        @Path("storeId") storeId: String,
        @Query("category") category: String? = null,
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 50,
        @Header("Authorization") token: String
    ): ApiResponseDto<List<InventoryItemResponseDto>>
    
    @GET("/api/v1/stores/{storeId}/inventory/{itemId}")
    suspend fun getInventoryItem(
        @Path("storeId") storeId: String,
        @Path("itemId") itemId: String,
        @Header("Authorization") token: String
    ): ApiResponseDto<InventoryItemResponseDto>
    
    @GET("/api/v1/stores/{storeId}/inventory/low-stock")
    suspend fun getLowStockItems(
        @Path("storeId") storeId: String,
        @Header("Authorization") token: String
    ): ApiResponseDto<List<InventoryItemResponseDto>>
    
    @POST("/api/v1/stores/{storeId}/stock-movements")
    suspend fun createStockMovement(
        @Path("storeId") storeId: String,
        @Body request: StockMovementRequestDto,
        @Header("Authorization") token: String
    ): ApiResponseDto<StockMovementResponseDto>
    
    @GET("/api/v1/stores/{storeId}/stock-movements/{itemId}")
    suspend fun getStockHistory(
        @Path("storeId") storeId: String,
        @Path("itemId") itemId: String,
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 100,
        @Header("Authorization") token: String
    ): ApiResponseDto<List<StockMovementResponseDto>>
    
    @GET("/api/v1/suppliers")
    suspend fun getSuppliers(
        @Header("Authorization") token: String
    ): ApiResponseDto<List<SupplierDto>>
}