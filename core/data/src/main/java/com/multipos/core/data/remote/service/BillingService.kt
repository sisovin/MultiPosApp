package com.multipos.core.data.remote.service

import com.multipos.core.data.remote.dto.*
import retrofit2.http.*
import java.math.BigDecimal

interface BillingService {
    @POST("/api/v1/stores/{storeId}/invoices")
    suspend fun createInvoice(
        @Path("storeId") storeId: String,
        @Body request: CreateInvoiceRequestDto,
        @Header("Authorization") token: String
    ): ApiResponseDto<InvoiceResponseDto>
    
    @GET("/api/v1/invoices/{invoiceId}")
    suspend fun getInvoice(
        @Path("invoiceId") invoiceId: String,
        @Header("Authorization") token: String
    ): ApiResponseDto<InvoiceResponseDto>
    
    @POST("/api/v1/invoices/{invoiceId}/payment")
    suspend fun processPayment(
        @Path("invoiceId") invoiceId: String,
        @Body request: ProcessPaymentRequestDto,
        @Header("Authorization") token: String
    ): ApiResponseDto<InvoiceResponseDto>
    
    @GET("/api/v1/stores/{storeId}/sales-report")
    suspend fun getSalesReport(
        @Path("storeId") storeId: String,
        @Query("date") date: String, // ISO 8601: YYYY-MM-DD
        @Header("Authorization") token: String
    ): ApiResponseDto<SalesReportDto>
    
    @GET("/api/v1/stores/{storeId}/invoices")
    suspend fun getInvoices(
        @Path("storeId") storeId: String,
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 50,
        @Header("Authorization") token: String
    ): ApiResponseDto<List<InvoiceResponseDto>>
}