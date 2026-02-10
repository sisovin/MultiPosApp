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