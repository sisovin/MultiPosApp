# Backend Architecture - MySQL (Source of Truth) & Redis (Caching)

---

## 1. MySQL Database Schema

### MySQL Setup Script

```sql
-- =====================================================
-- MULTIPOS APPLICATION - MYSQL DATABASE SCHEMA
-- Source of Truth for all data
-- =====================================================

-- Create database
CREATE DATABASE IF NOT EXISTS multipos_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE multipos_db;

-- =====================================================
-- 1. USERS & AUTHENTICATION
-- =====================================================

CREATE TABLE users (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL, -- Argon2id hash
    name VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'STORE_MANAGER', 'CASHIER', 'INVENTORY_STAFF') NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
    avatar_url VARCHAR(500),
    phone VARCHAR(20),
    last_login_at TIMESTAMP NULL,
    password_changed_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_role (role),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE user_tokens (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    user_id VARCHAR(36) NOT NULL,
    refresh_token VARCHAR(500) NOT NULL UNIQUE,
    device_id VARCHAR(255),
    expires_at TIMESTAMP NOT NULL,
    revoked_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE audit_log (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    user_id VARCHAR(36) NOT NULL,
    action VARCHAR(255) NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    entity_id VARCHAR(36),
    old_values JSON,
    new_values JSON,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_action (action),
    INDEX idx_entity (entity_type, entity_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 2. STORES & CONFIGURATION
-- =====================================================

CREATE TABLE stores (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    name VARCHAR(255) NOT NULL,
    type ENUM('RESTAURANT', 'RETAIL', 'CAFE') NOT NULL,
    address TEXT NOT NULL,
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(255),
    timezone VARCHAR(50) DEFAULT 'UTC',
    currency VARCHAR(3) DEFAULT 'USD',
    tax_rate DECIMAL(5, 2) DEFAULT 0.00,
    is_active BOOLEAN DEFAULT true,
    settings JSON, -- Store-specific configurations
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_type (type),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE store_users (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    store_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    role ENUM('ADMIN', 'MANAGER', 'STAFF') NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_store_user (store_id, user_id),
    FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_store_id (store_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 3. INVENTORY & SUPPLIERS
-- =====================================================

CREATE TABLE inventory_items (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    store_id VARCHAR(36) NOT NULL,
    name VARCHAR(255) NOT NULL,
    sku VARCHAR(100) UNIQUE NOT NULL,
    category VARCHAR(100),
    description TEXT,
    unit_of_measure VARCHAR(20) NOT NULL, -- pcs, kg, ltr, etc.
    current_stock DECIMAL(12, 2) NOT NULL DEFAULT 0,
    reorder_level DECIMAL(12, 2) NOT NULL,
    reorder_quantity DECIMAL(12, 2),
    unit_cost DECIMAL(10, 2) NOT NULL,
    selling_price DECIMAL(10, 2) NOT NULL,
    supplier_id VARCHAR(36),
    barcode VARCHAR(100),
    image_url VARCHAR(500),
    is_active BOOLEAN DEFAULT true,
    last_stock_update TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id) ON DELETE SET NULL,
    UNIQUE KEY unique_store_sku (store_id, sku),
    INDEX idx_store_id (store_id),
    INDEX idx_category (category),
    INDEX idx_is_active (is_active),
    INDEX idx_reorder_level (current_stock, reorder_level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE suppliers (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(20),
    address TEXT,
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100),
    contact_person VARCHAR(255),
    payment_terms VARCHAR(100),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE stock_movements (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    store_id VARCHAR(36) NOT NULL,
    item_id VARCHAR(36) NOT NULL,
    quantity DECIMAL(12, 2) NOT NULL,
    type ENUM('IN', 'OUT', 'ADJUSTMENT') NOT NULL,
    reason VARCHAR(100) NOT NULL,
    reference_number VARCHAR(100),
    notes TEXT,
    previous_stock DECIMAL(12, 2),
    new_stock DECIMAL(12, 2),
    created_by VARCHAR(36) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES inventory_items(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT,
    INDEX idx_store_id (store_id),
    INDEX idx_item_id (item_id),
    INDEX idx_type (type),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 4. RESTAURANT OPERATIONS
-- =====================================================

CREATE TABLE tables_config (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    store_id VARCHAR(36) NOT NULL,
    table_number VARCHAR(10),
    zone VARCHAR(50),
    capacity INT,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE,
    UNIQUE KEY unique_store_table (store_id, table_number),
    INDEX idx_store_id (store_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE menu_items (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    store_id VARCHAR(36) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100),
    price DECIMAL(10, 2) NOT NULL,
    cost DECIMAL(10, 2),
    image_url VARCHAR(500),
    is_available BOOLEAN DEFAULT true,
    is_popular BOOLEAN DEFAULT false,
    preparation_time_minutes INT,
    allergens JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE,
    INDEX idx_store_id (store_id),
    INDEX idx_category (category),
    INDEX idx_is_available (is_available)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE menu_modifiers (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    menu_item_id VARCHAR(36) NOT NULL,
    name VARCHAR(100) NOT NULL,
    additional_price DECIMAL(10, 2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (menu_item_id) REFERENCES menu_items(id) ON DELETE CASCADE,
    INDEX idx_menu_item_id (menu_item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE orders (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    store_id VARCHAR(36) NOT NULL,
    table_id VARCHAR(36),
    order_number VARCHAR(20),
    type ENUM('DINE_IN', 'TAKEOUT', 'DELIVERY') NOT NULL,
    status ENUM('PENDING', 'IN_PROGRESS', 'READY', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    customer_name VARCHAR(255),
    customer_phone VARCHAR(20),
    delivery_address TEXT,
    special_instructions TEXT,
    created_by VARCHAR(36) NOT NULL,
    started_at TIMESTAMP NULL,
    completed_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE,
    FOREIGN KEY (table_id) REFERENCES tables_config(id) ON DELETE SET NULL,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT,
    UNIQUE KEY unique_store_order (store_id, order_number),
    INDEX idx_store_id (store_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE order_items (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    order_id VARCHAR(36) NOT NULL,
    menu_item_id VARCHAR(36) NOT NULL,
    item_name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    modifiers JSON,
    notes TEXT,
    status ENUM('PENDING', 'READY', 'SERVED') NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (menu_item_id) REFERENCES menu_items(id) ON DELETE RESTRICT,
    INDEX idx_order_id (order_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 5. BILLING & PAYMENTS
-- =====================================================

CREATE TABLE invoices (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    store_id VARCHAR(36) NOT NULL,
    invoice_number VARCHAR(20) UNIQUE NOT NULL,
    order_id VARCHAR(36),
    status ENUM('DRAFT', 'ISSUED', 'PAID', 'CANCELLED') NOT NULL DEFAULT 'DRAFT',
    subtotal DECIMAL(12, 2) NOT NULL,
    tax_amount DECIMAL(12, 2) NOT NULL DEFAULT 0,
    discount_amount DECIMAL(12, 2) NOT NULL DEFAULT 0,
    total DECIMAL(12, 2) NOT NULL,
    payment_method ENUM('CASH', 'CARD', 'DIGITAL_WALLET', 'CHEQUE') NULL,
    payment_status ENUM('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED') NOT NULL DEFAULT 'PENDING',
    payment_reference VARCHAR(100),
    notes TEXT,
    created_by VARCHAR(36) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    paid_at TIMESTAMP NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE SET NULL,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT,
    INDEX idx_store_id (store_id),
    INDEX idx_invoice_number (invoice_number),
    INDEX idx_status (status),
    INDEX idx_payment_status (payment_status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE invoice_items (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    invoice_id VARCHAR(36) NOT NULL,
    description VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    line_total DECIMAL(12, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE,
    INDEX idx_invoice_id (invoice_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE payments (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    invoice_id VARCHAR(36) NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    payment_method ENUM('CASH', 'CARD', 'DIGITAL_WALLET', 'CHEQUE') NOT NULL,
    status ENUM('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED') NOT NULL DEFAULT 'PENDING',
    reference VARCHAR(100),
    processed_by VARCHAR(36),
    processed_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE,
    FOREIGN KEY (processed_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_invoice_id (invoice_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- INDEXES FOR PERFORMANCE
-- =====================================================

CREATE INDEX idx_inventory_store_category ON inventory_items(store_id, category);
CREATE INDEX idx_stock_movement_date_range ON stock_movements(store_id, created_at);
CREATE INDEX idx_order_date_range ON orders(store_id, created_at);
CREATE INDEX idx_invoice_date_range ON invoices(store_id, created_at);

-- =====================================================
-- VIEWS FOR COMMON QUERIES
-- =====================================================

CREATE VIEW v_low_stock_items AS
SELECT 
    i.id,
    i.store_id,
    i.sku,
    i.name,
    i.current_stock,
    i.reorder_level,
    (i.reorder_level - i.current_stock) as shortage_qty,
    s.name as supplier_name
FROM inventory_items i
LEFT JOIN suppliers s ON i.supplier_id = s.id
WHERE i.current_stock <= i.reorder_level AND i.is_active = true;

CREATE VIEW v_daily_sales AS
SELECT 
    DATE(i.created_at) as sale_date,
    i.store_id,
    COUNT(DISTINCT i.id) as total_invoices,
    SUM(i.total) as total_sales,
    SUM(i.tax_amount) as tax_collected,
    i.payment_method
FROM invoices i
WHERE i.status = 'PAID'
GROUP BY DATE(i.created_at), i.store_id, i.payment_method;

CREATE VIEW v_inventory_value AS
SELECT 
    store_id,
    SUM(current_stock * unit_cost) as total_value,
    COUNT(*) as total_items,
    SUM(CASE WHEN is_active = true THEN 1 ELSE 0 END) as active_items
FROM inventory_items
GROUP BY store_id;

-- =====================================================
-- SAMPLE DATA (for testing)
-- =====================================================

INSERT INTO users (email, password_hash, name, role) VALUES
('admin@multipos.local', '$argon2id$v=19$m=65540,t=3,p=4$...', 'Admin User', 'ADMIN');
```

---

## 2. Redis Caching Strategy

### Redis Key Patterns & TTL

```
# User Sessions & Tokens
user:{userId}:profile -> {serialized user object} | TTL: 1 hour
user:{userId}:permissions -> {serialized permissions} | TTL: 30 mins
user:token:{tokenId}:blacklist -> true | TTL: token expiry

# Store Data (frequently accessed, slower to update)
store:{storeId}:data -> {store config} | TTL: 2 hours
store:{storeId}:summary -> {sales summary} | TTL: 10 mins

# Inventory (write-through caching)
inventory:{storeId}:items -> {list of items} | TTL: 30 mins
inventory:{storeId}:item:{itemId} -> {item details} | TTL: 1 hour
inventory:{storeId}:low-stock -> {low stock items} | TTL: 15 mins
inventory:{storeId}:categories -> {category list} | TTL: 1 hour
inventory:stock:{itemId} -> {current stock level} | TTL: 5 mins (write-through)

# Orders (real-time, high priority)
order:{storeId}:{orderId} -> {order details} | TTL: 24 hours
order:{storeId}:active -> {set of active order IDs} | TTL: no TTL (manual invalidate)

# Billing (frequently updated)
invoice:{invoiceId} -> {invoice data} | TTL: 7 days
billing:{storeId}:daily:{date} -> {daily summary} | TTL: 24 hours (after date)
billing:{storeId}:open-invoices -> {set of open invoice IDs} | TTL: 1 hour

# Counters & Sequences
counter:{storeId}:invoice-number -> {next number} | TTL: no TTL
counter:{storeId}:order-number -> {next number} | TTL: no TTL

# Session Cache
session:{sessionId} -> {session data} | TTL: 24 hours
```

---

## 3. MySQL ↔ Redis Synchronization Strategy

### Write-Through Caching Pattern

```
When data is written:
1. Write to MySQL (source of truth)
2. Invalidate/update relevant Redis keys
3. Return response

When data is read:
1. Check Redis (fast)
2. If miss or nil, query MySQL
3. Cache result in Redis
4. Return response
```

### Conflict Resolution

```
MySQL is the source of truth.
Redis is ephemeral and can be cleared.

Daily reconciliation job:
1. For each updated record in MySQL (since last sync)
2. Update corresponding Redis cache
3. Log any conflicts (rare if sync working properly)
```

---

## 4. Backend API Specification (REST)

### Authentication Endpoints

```
POST /api/v1/auth/login
Request:
{
  "email": "user@example.com",
  "password": "password",
  "device_id": "device-uuid"
}

Response (200 OK):
{
  "access_token": "eyJ0eXAiOiJKV1QiLCJhbGc...",
  "refresh_token": "refresh_token_string",
  "expires_in": 3600,
  "token_type": "Bearer",
  "user": {
    "id": "user-id",
    "email": "user@example.com",
    "name": "User Name",
    "role": "STORE_MANAGER",
    "avatar": "https://...",
    "status": "ACTIVE",
    "created_at": "2024-01-01T00:00:00Z",
    "updated_at": "2024-01-01T00:00:00Z"
  }
}

POST /api/v1/auth/refresh
Request:
{
  "refresh_token": "refresh_token_string"
}

Response (200 OK):
{
  "access_token": "new_access_token",
  "refresh_token": "new_refresh_token",
  "expires_in": 3600,
  "token_type": "Bearer"
}

POST /api/v1/auth/logout
Headers: Authorization: Bearer {access_token}
Response (200 OK):
{
  "success": true,
  "message": "Logged out successfully"
}
```

### Inventory Endpoints

```
GET /api/v1/stores/{storeId}/inventory
Query params: category, skip, limit, sort
Headers: Authorization: Bearer {token}

Response (200 OK):
{
  "success": true,
  "data": [
    {
      "id": "item-id",
      "store_id": "store-id",
      "name": "Item Name",
      "sku": "SKU001",
      "category": "Beverages",
      "unit_of_measure": "pcs",
      "current_stock": 50,
      "reorder_level": 10,
      "unit_cost": 5.00,
      "selling_price": 12.00,
      "supplier": {
        "id": "supplier-id",
        "name": "Supplier Name"
      },
      "last_stock_update": "2024-01-15T10:00:00Z",
      "is_active": true,
      "created_at": "2024-01-01T00:00:00Z"
    }
  ]
}

POST /api/v1/stores/{storeId}/stock-movements
Request:
{
  "item_id": "item-id",
  "quantity": 20,
  "type": "IN",
  "reason": "PURCHASE",
  "reference_number": "PO-2024-001",
  "notes": "New stock from supplier",
  "created_by": "user-id"
}

Response (201 Created):
{
  "success": true,
  "data": {
    "id": "movement-id",
    "item_id": "item-id",
    "quantity": 20,
    "type": "IN",
    "previous_stock": 50,
    "new_stock": 70,
    "created_at": "2024-01-15T11:00:00Z"
  }
}

GET /api/v1/stores/{storeId}/inventory/low-stock
Response includes only items where current_stock <= reorder_level
```

### Billing Endpoints

```
POST /api/v1/stores/{storeId}/invoices
Request:
{
  "store_id": "store-id",
  "order_id": "order-id",
  "items": [
    {
      "description": "Item 1",
      "quantity": 2,
      "unit_price": "10.00",
      "line_total": "20.00"
    }
  ],
  "discount_amount": "5.00",
  "notes": "Special discount applied",
  "created_by": "user-id"
}

Response (201 Created):
{
  "success": true,
  "data": {
    "id": "invoice-id",
    "invoice_number": "INV-2024-00001",
    "store_id": "store-id",
    "status": "ISSUED",
    "subtotal": "20.00",
    "tax_amount": "1.50",
    "discount_amount": "5.00",
    "total": "16.50",
    "payment_status": "PENDING",
    "created_at": "2024-01-15T11:00:00Z"
  }
}

POST /api/v1/invoices/{invoiceId}/payment
Request:
{
  "payment_method": "CASH",
  "amount_paid": "16.50",
  "reference": "TXN-12345",
  "processed_by": "user-id"
}

Response (200 OK):
{
  "success": true,
  "data": {
    "invoice_id": "invoice-id",
    "status": "PAID",
    "payment_status": "COMPLETED",
    "paid_at": "2024-01-15T11:05:00Z"
  }
}

GET /api/v1/stores/{storeId}/sales-report?date=2024-01-15
Response (200 OK):
{
  "success": true,
  "data": {
    "report_date": "2024-01-15",
    "total_sales": "1250.75",
    "total_transactions": 45,
    "payment_breakdown": {
      "CASH": "500.00",
      "CARD": "750.75"
    },
    "tax_collected": "93.81",
    "top_items": [
      {
        "name": "Espresso",
        "quantity": 120,
        "revenue": "360.00"
      }
    ]
  }
}
```

---

## 5. Backend Technology Stack

### Recommended Backend Framework

- **Framework**: Laravel 11 or Node.js/Express
- **Database**: MySQL 8.0+
- **Cache**: Redis 7.0+
- **Task Queue**: Laravel Queue or Bull Queue
- **Authentication**: JWT (jsonwebtoken)
- **Password Hashing**: Argon2id

### Key Dependencies

```
# PHP/Laravel Stack
- laravel/framework
- tymondesigns/jwt-auth (JWT)
- predis/predis (Redis)
- laravel/sanctum (optional additional auth)
- spatie/laravel-permission (RBAC)

# Node.js/Express Stack
- express
- jsonwebtoken
- redis
- mysql2/promise
- argon2
- passport (optional)
- bull (task queue)
```

---

## 6. Background Jobs & Reconciliation

### Daily Reconciliation Job (runs at 2 AM UTC)

```
1. Iterate through all stores
2. For each store, check:
   - Inventory discrepancies
   - Unreconciled invoices
   - Pending payments
   - Stock movement logs
3. Compare Redis counts vs MySQL
4. Log any discrepancies
5. Update Redis cache from MySQL
6. Send alerts if major issues found
```

### Stock Level Updates

```
Real-time: Write to both MySQL and Redis immediately
Cache invalidation: When stock changes, update:
  - inventory:{storeId}:item:{itemId}
  - inventory:stock:{itemId}
  - inventory:{storeId}:low-stock (if threshold crossed)
  - inventory:{storeId}:items (list)
```

---

## 7. Error Handling & Status Codes

```
200 OK - Successful request
201 Created - Resource created successfully
400 Bad Request - Invalid input data
401 Unauthorized - Missing/invalid token
403 Forbidden - Insufficient permissions
404 Not Found - Resource not found
409 Conflict - Duplicate resource (e.g., SKU already exists)
422 Unprocessable Entity - Validation failed
429 Too Many Requests - Rate limited
500 Internal Server Error
503 Service Unavailable

Standard Error Response:
{
  "success": false,
  "error": {
    "code": "INVALID_REQUEST",
    "message": "Human readable error message",
    "details": {
      "field": "error details"
    },
    "timestamp": "2024-01-15T11:00:00Z"
  }
}
```

---

## Security Implementation Summary

✅ **Argon2id Password Hashing**
- Memory: 65540 KiB
- Time cost: 3
- Parallelism: 4

✅ **JWT Tokens**
- Access Token TTL: 1 hour (short-lived)
- Refresh Token TTL: 7 days (long-lived)
- Signed with RS256 or HS256

✅ **Rate Limiting**
- Login endpoint: 5 attempts per minute per IP
- API endpoints: 100 requests per minute per user

✅ **RBAC Enforcement**
- All endpoints validate user role
- Database queries filtered by store_id
- Sensitive fields masked in responses

