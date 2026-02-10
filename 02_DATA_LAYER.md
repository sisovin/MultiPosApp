# Data Layer Implementation - DTOs, Models, and Repositories

---

## 1. Remote DTOs (Data Transfer Objects)

### core/data/src/main/java/com/multipos/core/data/remote/dto/AuthDto.kt

```kotlin
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
```

### core/data/src/main/java/com/multipos/core/data/remote/dto/StoreDto.kt

```kotlin
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
```

### core/data/src/main/java/com/multipos/core/data/remote/dto/InventoryItemDto.kt

```kotlin
package com.multipos.core.data.remote.dto

import com.google.gson.annotations.SerializedName

data class InventoryItemResponseDto(
    val id: String,
    @SerializedName("store_id")
    val storeId: String,
    val name: String,
    val sku: String,
    val category: String,
    @SerializedName("unit_of_measure")
    val unitOfMeasure: String, // pcs, kg, ltr, etc.
    @SerializedName("current_stock")
    val currentStock: Double,
    @SerializedName("reorder_level")
    val reorderLevel: Double,
    @SerializedName("unit_cost")
    val unitCost: Double,
    @SerializedName("selling_price")
    val sellingPrice: Double,
    val supplier: SupplierDto?,
    @SerializedName("last_stock_update")
    val lastStockUpdate: String,
    @SerializedName("is_active")
    val isActive: Boolean = true,
    @SerializedName("created_at")
    val createdAt: String
)

data class StockMovementRequestDto(
    @SerializedName("item_id")
    val itemId: String,
    val quantity: Double,
    val type: String, // IN, OUT, ADJUSTMENT
    val reason: String, // PURCHASE, SALE, DAMAGED, THEFT, RECOUNT, etc.
    @SerializedName("reference_number")
    val referenceNumber: String? = null,
    val notes: String? = null,
    @SerializedName("created_by")
    val createdBy: String
)

data class StockMovementResponseDto(
    val id: String,
    @SerializedName("item_id")
    val itemId: String,
    val quantity: Double,
    val type: String,
    val reason: String,
    @SerializedName("reference_number")
    val referenceNumber: String?,
    val notes: String?,
    @SerializedName("previous_stock")
    val previousStock: Double,
    @SerializedName("new_stock")
    val newStock: Double,
    @SerializedName("created_by")
    val createdBy: String,
    @SerializedName("created_at")
    val createdAt: String
)

data class SupplierDto(
    val id: String,
    val name: String,
    val email: String?,
    val phone: String?,
    val address: String?,
    @SerializedName("payment_terms")
    val paymentTerms: String? = null,
    @SerializedName("is_active")
    val isActive: Boolean = true
)
```

### core/data/src/main/java/com/multipos/core/data/remote/dto/OrderDto.kt

```kotlin
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
```

### core/data/src/main/java/com/multipos/core/data/remote/dto/BillingDto.kt

```kotlin
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
```

---

## 2. Domain Models

### core/domain/src/main/java/com/multipos/core/domain/model/User.kt

```kotlin
package com.multipos.core.domain.model

data class User(
    val id: String,
    val email: String,
    val name: String,
    val role: UserRole,
    val avatar: String? = null,
    val status: UserStatus = UserStatus.ACTIVE,
    val createdAt: String,
    val updatedAt: String
)

enum class UserRole {
    ADMIN,
    STORE_MANAGER,
    CASHIER,
    INVENTORY_STAFF;

    fun hasPermission(permission: Permission): Boolean {
        return when (this) {
            ADMIN -> true
            STORE_MANAGER -> permission in listOf(
                Permission.MANAGE_STORE,
                Permission.VIEW_INVENTORY,
                Permission.VIEW_BILLING,
                Permission.CREATE_REPORT
            )
            CASHIER -> permission in listOf(
                Permission.VIEW_INVENTORY,
                Permission.PROCESS_PAYMENT,
                Permission.VIEW_BILLING
            )
            INVENTORY_STAFF -> permission in listOf(
                Permission.VIEW_INVENTORY,
                Permission.UPDATE_STOCK
            )
        }
    }
}

enum class UserStatus {
    ACTIVE,
    INACTIVE,
    SUSPENDED
}

enum class Permission {
    MANAGE_STORE,
    MANAGE_USERS,
    VIEW_INVENTORY,
    UPDATE_STOCK,
    PROCESS_PAYMENT,
    VIEW_BILLING,
    CREATE_REPORT,
    DELETE_TRANSACTION
}
```

### core/domain/src/main/java/com/multipos/core/domain/model/Store.kt

```kotlin
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
```

### core/domain/src/main/java/com/multipos/core/domain/model/InventoryItem.kt

```kotlin
package com.multipos.core.domain.model

data class InventoryItem(
    val id: String,
    val storeId: String,
    val name: String,
    val sku: String,
    val category: String,
    val unitOfMeasure: String,
    val currentStock: Double,
    val reorderLevel: Double,
    val unitCost: Double,
    val sellingPrice: Double,
    val supplier: Supplier? = null,
    val lastStockUpdate: String,
    val isActive: Boolean = true,
    val createdAt: String
) {
    val isLowStock: Boolean
        get() = currentStock <= reorderLevel
    
    val stockValue: Double
        get() = currentStock * unitCost
}

data class Supplier(
    val id: String,
    val name: String,
    val email: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val paymentTerms: String? = null,
    val isActive: Boolean = true
)

data class StockMovement(
    val id: String,
    val itemId: String,
    val quantity: Double,
    val type: StockMovementType,
    val reason: String,
    val referenceNumber: String? = null,
    val notes: String? = null,
    val previousStock: Double,
    val newStock: Double,
    val createdBy: String,
    val createdAt: String
)

enum class StockMovementType {
    IN,     // Stock received
    OUT,    // Stock removed
    ADJUSTMENT // Inventory correction
}
```

### core/domain/src/main/java/com/multipos/core/domain/model/Order.kt

```kotlin
package com.multipos.core.domain.model

data class Order(
    val id: String,
    val storeId: String,
    val tableId: String? = null,
    val status: OrderStatus,
    val items: List<OrderItem>,
    val createdBy: String,
    val createdAt: String,
    val updatedAt: String,
    val completedAt: String? = null
) {
    val subtotal: Double
        get() = items.sumOf { it.quantity * it.unitPrice }
    
    val itemCount: Int
        get() = items.sumOf { it.quantity }
}

data class OrderItem(
    val id: String,
    val menuItemId: String,
    val name: String,
    val quantity: Int,
    val unitPrice: Double,
    val modifiers: List<Modifier> = emptyList(),
    val notes: String? = null,
    val status: OrderItemStatus = OrderItemStatus.PENDING
) {
    val lineTotal: Double
        get() = quantity * unitPrice + modifiers.sumOf { it.additionalPrice * quantity }
}

data class Modifier(
    val id: String,
    val name: String,
    val additionalPrice: Double = 0.0
)

enum class OrderStatus {
    PENDING,
    IN_PROGRESS,
    READY,
    COMPLETED,
    CANCELLED
}

enum class OrderItemStatus {
    PENDING,
    READY,
    SERVED
}
```

### core/domain/src/main/java/com/multipos/core/domain/model/Billing.kt

```kotlin
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
```

---

## 3. API Services (Retrofit)

### core/data/src/main/java/com/multipos/core/data/remote/service/AuthService.kt

```kotlin
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
```

### core/data/src/main/java/com/multipos/core/data/remote/service/StoreService.kt

```kotlin
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
```

### core/data/src/main/java/com/multipos/core/data/remote/service/InventoryService.kt

```kotlin
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
```

### core/data/src/main/java/com/multipos/core/data/remote/service/BillingService.kt

```kotlin
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
```

---

## 4. Mappers (DTO ↔ Domain Models)

### core/data/src/main/java/com/multipos/core/data/mapper/DtoMappers.kt

```kotlin
package com.multipos.core.data.mapper

import com.multipos.core.data.remote.dto.*
import com.multipos.core.domain.model.*
import java.math.BigDecimal

// User Mappers
fun UserResponseDto.toDomain(): User = User(
    id = id,
    email = email,
    name = name,
    role = UserRole.valueOf(role),
    avatar = avatar,
    status = UserStatus.valueOf(status),
    createdAt = createdAt,
    updatedAt = updatedAt
)

// Store Mappers
fun StoreResponseDto.toDomain(): Store = Store(
    id = id,
    name = name,
    type = StoreType.valueOf(type),
    address = address,
    phone = phone,
    email = email,
    timezone = timezone,
    currency = currency,
    taxRate = taxRate,
    isActive = isActive,
    createdAt = createdAt,
    updatedAt = updatedAt
)

// Inventory Mappers
fun InventoryItemResponseDto.toDomain(): InventoryItem = InventoryItem(
    id = id,
    storeId = storeId,
    name = name,
    sku = sku,
    category = category,
    unitOfMeasure = unitOfMeasure,
    currentStock = currentStock,
    reorderLevel = reorderLevel,
    unitCost = unitCost,
    sellingPrice = sellingPrice,
    supplier = supplier?.toDomain(),
    lastStockUpdate = lastStockUpdate,
    isActive = isActive,
    createdAt = createdAt
)

fun SupplierDto.toDomain(): Supplier = Supplier(
    id = id,
    name = name,
    email = email,
    phone = phone,
    address = address,
    paymentTerms = paymentTerms,
    isActive = isActive
)

fun StockMovementResponseDto.toDomain(): StockMovement = StockMovement(
    id = id,
    itemId = itemId,
    quantity = quantity,
    type = StockMovementType.valueOf(type),
    reason = reason,
    referenceNumber = referenceNumber,
    notes = notes,
    previousStock = previousStock,
    newStock = newStock,
    createdBy = createdBy,
    createdAt = createdAt
)

// Order Mappers
fun OrderResponseDto.toDomain(): Order = Order(
    id = id,
    storeId = storeId,
    tableId = tableId,
    status = OrderStatus.valueOf(status),
    items = items.map { it.toDomain() },
    createdBy = createdBy,
    createdAt = createdAt,
    updatedAt = updatedAt,
    completedAt = completedAt
)

fun OrderItemDto.toDomain(): OrderItem = OrderItem(
    id = id,
    menuItemId = menuItemId,
    name = name,
    quantity = quantity,
    unitPrice = unitPrice,
    modifiers = modifiers?.map { it.toDomain() } ?: emptyList(),
    notes = notes,
    status = OrderItemStatus.valueOf(status)
)

fun ModifierDto.toDomain(): Modifier = Modifier(
    id = id,
    name = name,
    additionalPrice = additionalPrice
)

// Billing Mappers
fun InvoiceResponseDto.toDomain(): Invoice = Invoice(
    id = id,
    invoiceNumber = invoiceNumber,
    storeId = storeId,
    orderId = orderId,
    status = InvoiceStatus.valueOf(status),
    items = items.map { it.toDomain() },
    subtotal = subtotal,
    taxAmount = taxAmount,
    discountAmount = discountAmount,
    total = total,
    paymentMethod = paymentMethod?.let { PaymentMethod.valueOf(it) },
    paymentStatus = PaymentStatus.valueOf(paymentStatus),
    createdBy = createdBy,
    createdAt = createdAt,
    paidAt = paidAt
)

fun InvoiceItemDto.toDomain(): InvoiceItem = InvoiceItem(
    id = id,
    description = description,
    quantity = quantity,
    unitPrice = unitPrice,
    lineTotal = lineTotal
)

fun SalesReportDto.toDomain(): SalesReport = SalesReport(
    reportDate = reportDate,
    totalSales = totalSales,
    totalTransactions = totalTransactions,
    paymentBreakdown = paymentBreakdown.mapKeys { PaymentMethod.valueOf(it.key) },
    taxCollected = taxCollected,
    topItems = topItems.map { it.toDomain() }
)

fun TopItemDto.toDomain(): TopItem = TopItem(
    name = name,
    quantity = quantity,
    revenue = revenue
)
```

---

This comprehensive data layer provides:
- ✅ Type-safe DTOs for all domain entities
- ✅ Retrofit API service interfaces
- ✅ Mapper functions for clean domain isolation
- ✅ Support for JSON serialization/deserialization
- ✅ Ready for Room entity mapping in next section

