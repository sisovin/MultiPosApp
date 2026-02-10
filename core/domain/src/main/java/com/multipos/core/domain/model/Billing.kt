package com.multipos.core.domain.model

import java.math.BigDecimal

data class Invoice(
    val id: String,
    val invoiceNumber: String,
    val storeId: String,
    val orderId: String? = null,
    val status: InvoiceStatus,
    val items: List<InvoiceItem>,
    val subtotal: BigDecimal,
    val taxAmount: BigDecimal,
    val discountAmount: BigDecimal = BigDecimal.ZERO,
    val total: BigDecimal,
    val paymentMethod: PaymentMethod? = null,
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val createdBy: String,
    val createdAt: String,
    val paidAt: String? = null
)

data class InvoiceItem(
    val id: String,
    val description: String,
    val quantity: Int,
    val unitPrice: BigDecimal,
    val lineTotal: BigDecimal
)

data class SalesReport(
    val reportDate: String,
    val totalSales: BigDecimal,
    val totalTransactions: Int,
    val paymentBreakdown: Map<PaymentMethod, BigDecimal>,
    val taxCollected: BigDecimal,
    val topItems: List<TopItem>
)

data class TopItem(
    val name: String,
    val quantity: Int,
    val revenue: BigDecimal
)

enum class InvoiceStatus {
    DRAFT,
    ISSUED,
    PAID,
    CANCELLED
}

enum class PaymentMethod {
    CASH,
    CARD,
    DIGITAL_WALLET,
    CHEQUE
}

enum class PaymentStatus {
    PENDING,
    COMPLETED,
    FAILED,
    REFUNDED
}