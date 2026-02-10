package com.multipos.core.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class InvoiceResponseDto(
    val id: String,
    @SerializedName("invoice_number")
    val invoiceNumber: String,
    @SerializedName("store_id")
    val storeId: String,
    @SerializedName("order_id")
    val orderId: String?,
    val status: String, // DRAFT, ISSUED, PAID, CANCELLED
    val items: List<InvoiceItemDto>,
    @SerializedName("subtotal")
    val subtotal: BigDecimal,
    @SerializedName("tax_amount")
    val taxAmount: BigDecimal,
    @SerializedName("discount_amount")
    val discountAmount: BigDecimal = BigDecimal.ZERO,
    val total: BigDecimal,
    @SerializedName("payment_method")
    val paymentMethod: String?, // CASH, CARD, DIGITAL_WALLET, CHEQUE
    @SerializedName("payment_status")
    val paymentStatus: String = "PENDING", // PENDING, COMPLETED, FAILED
    @SerializedName("created_by")
    val createdBy: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("paid_at")
    val paidAt: String? = null
)

data class InvoiceItemDto(
    val id: String,
    val description: String,
    val quantity: Int,
    @SerializedName("unit_price")
    val unitPrice: BigDecimal,
    @SerializedName("line_total")
    val lineTotal: BigDecimal
)

data class CreateInvoiceRequestDto(
    @SerializedName("store_id")
    val storeId: String,
    @SerializedName("order_id")
    val orderId: String?,
    val items: List<InvoiceItemDto>,
    @SerializedName("discount_amount")
    val discountAmount: BigDecimal = BigDecimal.ZERO,
    val notes: String? = null,
    @SerializedName("created_by")
    val createdBy: String
)

data class ProcessPaymentRequestDto(
    @SerializedName("payment_method")
    val paymentMethod: String, // CASH, CARD, DIGITAL_WALLET, CHEQUE
    @SerializedName("amount_paid")
    val amountPaid: BigDecimal,
    val reference: String? = null, // Transaction ID, cheque number, etc.
    @SerializedName("processed_by")
    val processedBy: String
)

data class SalesReportDto(
    @SerializedName("report_date")
    val reportDate: String,
    @SerializedName("total_sales")
    val totalSales: BigDecimal,
    @SerializedName("total_transactions")
    val totalTransactions: Int,
    @SerializedName("payment_breakdown")
    val paymentBreakdown: Map<String, BigDecimal>,
    @SerializedName("tax_collected")
    val taxCollected: BigDecimal,
    @SerializedName("top_items")
    val topItems: List<TopItemDto>
)

data class TopItemDto(
    val name: String,
    val quantity: Int,
    @SerializedName("revenue")
    val revenue: BigDecimal
)