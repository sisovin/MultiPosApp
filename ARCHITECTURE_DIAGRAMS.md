# MultiPosApp - Architecture Diagrams & Visual References

---

## 🏗️ System Architecture Overview

```
╔════════════════════════════════════════════════════════════════════════════╗
║                    MULTIPOSAPP SYSTEM ARCHITECTURE                         ║
╠════════════════════════════════════════════════════════════════════════════╣
║                                                                             ║
║  ┌─────────────────────────────────────────────────────────────────────┐  ║
║  │              ANDROID APPLICATION LAYER                              │  ║
║  │                   (Jetpack Compose + Kotlin)                        │  ║
║  │                                                                     │  ║
║  │  ┌──────────────────────────────────────────────────────────────┐ │  ║
║  │  │ PRESENTATION LAYER                                           │ │  ║
║  │  │ • Jetpack Compose Screens (Material 3)                      │ │  ║
║  │  │ • Navigation Graph                                           │ │  ║
║  │  │ • UI State Management                                        │ │  ║
║  │  └──────────────────┬───────────────────────────────────────────┘ │  ║
║  │                     │                                             │  ║
║  │  ┌──────────────────▼───────────────────────────────────────────┐ │  ║
║  │  │ DOMAIN LAYER                                                 │ │  ║
║  │  │ • ViewModels (StateFlow)                                    │ │  ║
║  │  │ • Use Cases                                                  │ │  ║
║  │  │ • Domain Models                                              │ │  ║
║  │  │ • Repository Interfaces                                      │ │  ║
║  │  └──────────────────┬───────────────────────────────────────────┘ │  ║
║  │                     │                                             │  ║
║  │  ┌──────────────────▼───────────────────────────────────────────┐ │  ║
║  │  │ DATA LAYER                                                   │ │  ║
║  │  │ • Repository Implementations                                 │ │  ║
║  │  │ • Retrofit API Clients                                       │ │  ║
║  │  │ • OkHttp Interceptors                                        │ │  ║
║  │  │ • Token Manager (Encrypted)                                  │ │  ║
║  │  │ • DTOs & Mappers                                             │ │  ║
║  │  │ • Room Database (Optional)                                   │ │  ║
║  │  └──────────────────┬───────────────────────────────────────────┘ │  ║
║  │                     │                                             │  ║
║  │  ┌──────────────────▼───────────────────────────────────────────┐ │  ║
║  │  │ SECURITY LAYER                                               │ │  ║
║  │  │ • EncryptedSharedPreferences                                 │ │  ║
║  │  │ • JWT Token Storage                                          │ │  ║
║  │  │ • HTTPS Communication                                        │ │  ║
║  │  │ • Request Signing                                            │ │  ║
║  │  └──────────────────┬───────────────────────────────────────────┘ │  ║
║  │                     │                                             │  ║
║  └─────────────────────┼─────────────────────────────────────────────┘  ║
║                        │                                                 ║
║                        │ (HTTPS + JWT Tokens)                           ║
║                        ▼                                                 ║
║  ┌─────────────────────────────────────────────────────────────────────┐  ║
║  │               BACKEND API SERVER LAYER                              │  ║
║  │                (Laravel/Node.js)                                    │  ║
║  │                                                                     │  ║
║  │  ┌──────────────────────────────────────────────────────────────┐ │  ║
║  │  │ REQUEST PROCESSING                                           │ │  ║
║  │  │ • JWT Validation                                             │ │  ║
║  │  │ • RBAC Authorization                                         │ │  ║
║  │  │ • Input Validation                                           │ │  ║
║  │  │ • Business Logic                                             │ │  ║
║  │  └──────────────────┬───────────────────────────────────────────┘ │  ║
║  │                     │                                             │  ║
║  │    ┌────────────────┼────────────────┐                           │  ║
║  │    ▼                ▼                ▼                           │  ║
║  │ ┌────────┐     ┌─────────┐     ┌──────────┐                     │  ║
║  │ │ MySQL  │     │  Redis  │     │Background│                     │  ║
║  │ │(Truth) │     │(Cache)  │     │  Jobs    │                     │  ║
║  │ │        │     │         │     │(Sync)    │                     │  ║
║  │ └────────┘     └─────────┘     └──────────┘                     │  ║
║  │                                                                     │  ║
║  └─────────────────────────────────────────────────────────────────────┘  ║
║                                                                             ║
╚════════════════════════════════════════════════════════════════════════════╝
```

---

## 🔐 Authentication & Token Flow

```
┌─────────────────┐
│  User Login     │
│  Screen         │
└────────┬────────┘
         │
         │ Enter credentials
         ▼
┌─────────────────┐         ┌──────────────────┐
│ LoginUseCase    │────────▶│ AuthRepository   │
│ • Validate      │         │ • Call API       │
│ • Call Repo     │         │ • Handle errors  │
└────────┬────────┘         └────────┬─────────┘
         │                           │
         │                           │ HTTPS POST
         │                           │ /api/v1/auth/login
         │                           ▼
         │                   ┌──────────────────┐
         │                   │ Backend Server   │
         │                   │ • Hash password  │
         │                   │ • Validate       │
         │                   └────────┬─────────┘
         │                           │
         │    ◄─────────────────────┘
         │    Response:
         │    {
         │      access_token: "jwt...",
         │      refresh_token: "token...",
         │      expires_in: 3600
         │    }
         │
         ▼
┌─────────────────────────────────┐
│ TokenManager.saveTokens()       │
│ • Encrypt tokens                │
│ • Store in SharedPreferences    │
│ • Save expiry time              │
└────────┬────────────────────────┘
         │
         ▼
┌──────────────────────────────────┐
│ EncryptedSharedPreferences       │
│ ┌────────────────────────────┐   │
│ │ token_data (encrypted):    │   │
│ │ {                          │   │
│ │   accessToken: "...",      │   │
│ │   refreshToken: "...",     │   │
│ │   expiryTime: 1234567890   │   │
│ │ }                          │   │
│ └────────────────────────────┘   │
└──────────────────────────────────┘

═══════════════════════════════════════════════════════════════════

                    API REQUEST WITH TOKEN

┌─────────────────┐
│ Next API Call   │
└────────┬────────┘
         │
         ▼
┌──────────────────────────┐
│ AuthInterceptor          │
│ • Get token from Manager │
│ • Add to headers:        │
│   Authorization: Bearer  │
│   {token}                │
└────────┬─────────────────┘
         │
         ▼
┌──────────────────────────┐
│ Backend validates JWT    │
│ • Verify signature       │
│ • Check expiry           │
│ • Extract user/role      │
└────────┬─────────────────┘
         │
      Success?
         │
    ┌────┴────┐
    │          │
   YES        NO (401)
    │          │
    │          ▼
    │      ┌─────────────────────────┐
    │      │ TokenRefreshInterceptor │
    │      │ • Get refresh token     │
    │      │ • Call /refresh endpoint│
    │      │ • Get new access token  │
    │      │ • Retry original request│
    │      └────────┬────────────────┘
    │              │
    │              ▼
    │      ┌──────────────────┐
    │      │ Update cached    │
    │      │ token with new   │
    │      │ access token     │
    │      └────────┬─────────┘
    │              │
    └──────┬───────┘
           │
           ▼
    ┌────────────────┐
    │ Process Result │
    │ Return Data    │
    └────────────────┘
```

---

## 💾 MySQL ↔ Redis Sync Flow

```
DATA WRITE (Create/Update)
━━━━━━━━━━━━━━━━━━━━━━━━━━

     Android          Backend           MySQL         Redis
     Client            API              (Truth)      (Cache)
       │                │                 │             │
       │  POST Request  │                 │             │
       ├───────────────▶│                 │             │
       │                │ BEGIN TXACT    │             │
       │                ├────────────────▶│             │
       │                │                 │             │
       │                │  Validate       │             │
       │                │  & Write        │             │
       │                │                 │ Write       │
       │                │                 ├────────────▶│
       │                │                 │             │
       │                │ COMMIT TXACT   │             │
       │                ├────────────────▶│             │
       │                │                 │             │
       │    Response    │                 │             │
       │◀───────────────┤                 │             │
       │ (Success)      │                 │             │
       │                │ Invalidate      │             │
       │                │ Cache Keys      │             │
       │                ├─────────────────────────────▶│
       │                │                 │             │
       │                │                 │             │
       │                │                 │ New Data    │
       │                │                 ├────────────▶│
       │                │                 │             │

═════════════════════════════════════════════════════════════════

DATA READ (Query)
━━━━━━━━━━━━━━━

     Android          Backend           MySQL         Redis
     Client            API              (Truth)      (Cache)
       │                │                 │             │
       │  GET Request   │                 │             │
       ├───────────────▶│                 │             │
       │                │                 │             │
       │                │ Check Redis     │             │
       │                ├────────────────────────────▶│
       │                │                 │            │
       │                │                 │  Cache HIT │
       │                │◀───────────────────────────┤
       │                │ (Return cached data)        │
       │    Response    │                 │             │
       │◀───────────────┤                 │             │
       │ (Fast!)        │                 │             │
       │                │                 │             │
       │                │                 │             │
       │                │         (If Cache MISS)     │
       │                │                 │             │
       │                │ Query MySQL     │             │
       │                ├────────────────▶│             │
       │                │                 │             │
       │                │◀────────────────┤             │
       │                │ (Data)          │             │
       │                │                 │             │
       │                │ Store in Redis  │             │
       │                ├─────────────────────────────▶│
       │                │                 │             │
       │    Response    │                 │             │
       │◀───────────────┤                 │             │
       │                │                 │             │

═════════════════════════════════════════════════════════════════

DAILY RECONCILIATION (2 AM UTC)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Background Job runs:

1. Check all stores
2. For each store:
   ├─ Get counts from MySQL
   ├─ Get counts from Redis
   ├─ Compare:
   │  ├─ Inventory items match?
   │  ├─ Invoice totals match?
   │  ├─ Payment status consistent?
   │  └─ Stock levels reconciled?
   ├─ On mismatch:
   │  └─ Log discrepancy
   │     Update Redis from MySQL
   │     Send alert if critical
   └─ Update sync timestamp

3. Report results
   └─ Alert admin if issues found
```

---

## 📦 Module Dependency Graph

```
┌───────────────────────────────────────────────────────────────┐
│                         :app                                  │
│  (Main Application)                                           │
├───────────────────────────────────────────────────────────────┤
│                                                               │
│  Depends on:                                                 │
│  ├─▶ :core:ui        (UI Components)                         │
│  ├─▶ :core:data      (Networking, Repos)                     │
│  ├─▶ :core:domain    (Business Logic)                        │
│  ├─▶ :feature:auth   (Authentication)                        │
│  ├─▶ :feature:store  (Store Management)                      │
│  ├─▶ :feature:inventory (Inventory System)                   │
│  ├─▶ :feature:billing (Billing System)                       │
│  └─▶ :feature:restaurant (Restaurant Mode)                   │
│                                                               │
└───────────────────────────────────────────────────────────────┘
                     │                    │
        ┌────────────┴────────────┬───────┴──────────┐
        │                         │                  │
        ▼                         ▼                  ▼
┌─────────────────┐      ┌──────────────┐   ┌────────────────┐
│   :core:ui      │      │ :core:data   │   │  :core:domain  │
│                 │      │              │   │                │
│ • Components    │      │ • Retrofit   │   │ • Models       │
│ • Theme         │      │ • OkHttp     │   │ • UseCases     │
│ • Composables   │      │ • DTOs       │   │ • Interfaces   │
│ • Material 3    │      │ • Repos      │   │ • Validators   │
│                 │      │ • Mappers    │   │                │
└─────────────────┘      │ • Security   │   └────────────────┘
                         └──────────────┘
                                │
                                ▼
                    ┌──────────────────────┐
                    │  External Libraries  │
                    │                      │
                    │ • androidx.compose   │
                    │ • androidx.lifecycle │
                    │ • com.squareup..*    │
                    │ • com.google.dagger  │
                    │ • androidx.room      │
                    │ • kotlinx.coroutines │
                    └──────────────────────┘

═══════════════════════════════════════════════════════════════════

        Dependency Direction: Features depend on Core,
                   Core depends on Libraries only
```

---

## 🔄 Inventory Management Data Flow

```
┌──────────────────────────────────────────────────────────────┐
│           INVENTORY MANAGEMENT FLOW                          │
└──────────────────────────────────────────────────────────────┘

1. LIST INVENTORY
═══════════════════

User Opens        InventoryViewModel   InventoryRepository   API
Inventory          .loadInventory()    .getInventory()      Service
   │                    │                    │                │
   │                    ├──────────────────▶ │                │
   │                    │                    ├───────────────▶│
   │                    │                    │                │
   │                    │                    │ Hits Redis,    │
   │                    │                    │ Returns cached │
   │                    │                    │◀───────────────┤
   │                    │ [DTO List]        │                │
   │                    │◀───────────────────┤                │
   │                    │                    │                │
   │                    ├─ Map to Domain ──┐                  │
   │                    │                  │                  │
   │                    │                  ├─ Update State   │
   │                    │                 ┌─▶ (StateFlow)    │
   │                    │                 │                  │
   │    [Domain         │                 │                  │
   │     Models]        │                 │                  │
   │◀────────────────────────────────────┘                   │
   │                    │                                     │
   ├─ Recompose       │                                     │
   │   UI              │                                     │
   └────────────────────────────────────────────────────────┘


2. SEARCH & FILTER
═══════════════════

User Types      InventoryViewModel    UI Recomposes
Search Query           │                  │
   │                   │                  │
   │  updateSearchQuery│                  │
   ├──────────────────▶│                  │
   │                   │                  │
   │                   ├─ Update State ──▶│
   │                   │  (filtered = ... )
   │                   │                  │
   │                   │                  │ Apply filters:
   │                   │                  │ • Search by name/SKU
   │                   │                  │ • Filter by category
   │                   │                  │ • Sort by stock level
   │                   │                  │
   │   [Filtered      │                  │
   │    Results]      │                  │
   │◀─────────────────┤◀─────────────────┤
   │                  │                  │
   └──────────────────────────────────────┘


3. UPDATE STOCK
═══════════════

User Enters     InventoryViewModel    InventoryRepository   API    MySQL  Redis
Stock Qty       .updateStock()         .updateStock()      Server
   │                   │                    │                │      │      │
   │                   │                    ├───────────────▶│      │      │
   │                   │                    │                │      │      │
   │                   │                    │ Build Request: │      │      │
   │                   │                    │ {item_id,      │      │      │
   │                   │                    │  qty, reason}  │      │      │
   │                   │                    │                │      │      │
   │                   │                    │                │ POST │      │
   │                   │                    │                ├─────▶│      │
   │                   │                    │                │      │      │
   │                   │                    │                │      │ Write│
   │                   │                    │                │      ├─────▶│
   │                   │                    │                │      │      │
   │                   │                    │                │◀─────┤      │
   │                   │                    │                │ Done │      │
   │                   │                    │◀───────────────┤      │      │
   │                   │                    │ [Response]     │      │      │
   │                   │ [Domain Model]    │                │      │      │
   │                   │◀────────────────────               │      │      │
   │                   │                   │                │      │      │
   │                   ├─ Show Success ──┐ │                │      │      │
   │                   │                 │ │                │      │      │
   │ Success Badge    │◀────────────────┘ │                │      │      │
   │◀─────────────────┤ (Clear message   │                │      │      │
   │                   │  after 3s)       │                │      │      │
   │                  │                  │                │      │      │
   │                  ├─ Reload List ───▶ (restart from 1)       │      │
   │                  │                  │                │      │      │
   └───────────────────────────────────────────────────────────────────┘


4. LOW STOCK ALERTS
════════════════════

Backend          Mobile App          UI Displays
Auto-detects     (WorkManager)       
Low Stock   ──▶  Periodic Sync  ──▶  Alert Banner
Items            (every 30 min)       (with item list)
   │                  │                    │
   │                  ├─ Check low    ──▶ Badge on items
   │                  │   stock items     showing:
   │                  │                   • Quantity
   │                  ├─ Get from     ──▶ • Reorder level
   │                  │   Redis            • Action button
   │                  │   (fast!)     ──▶  (Reorder?)
   │                  │
   │                  └─ If missing,
   │                     query MySQL
```

---

## 💳 Billing & Payment Flow

```
┌──────────────────────────────────────────────────────────────┐
│           BILLING & PAYMENT PROCESS                          │
└──────────────────────────────────────────────────────────────┘

1. CREATE INVOICE
═════════════════

Order Created      BillingViewModel      BillingRepository     API
   │               .createInvoice()      .createInvoice()      │
   │                    │                    │                 │
   │ User selects   Create Invoice      Build Request:        │
   │ items to bill  screen opens        {items, discounts}    │
   │                    │                    │                 │
   │                    ├───────────────────▶│                 │
   │                    │                    ├────────────────▶│
   │                    │                    │                 │
   │                    │                    │ Generate        │
   │                    │                    │ invoice number  │
   │                    │                    │ from DB         │
   │                    │                    │                 │
   │                    │                    │◀────────────────┤
   │                    │ [Invoice Data]     │ Response        │
   │                    │◀──────────────────┤ (id, number,    │
   │                    │                    │  total)         │
   │                    │                    │                 │
   │ Invoice Summary  ◀─┤◀──────────────────┤                 │
   │ Displayed            │  Items           │                 │
   │                      │  Subtotal        │                 │
   │                      │  Tax             │                 │
   │                      │  Total           │                 │


2. PROCESS PAYMENT
═══════════════════

User Selects      BillingViewModel       BillingRepository    API
Payment Method    .processPayment()      .processPayment()    │
   │                    │                     │               │
   │ Choose:        Button enabled:          │               │
   │ • Cash    ──▶  • Amount entered      ┌──────────────┐   │
   │ • Card    │     • Method selected    │ Validate:    │   │
   │ • Digital │                         │ • Amount ≥   │   │
   │ • Cheque  │                         │   Total      │   │
   │                  │                   │ • Method set │   │
   │                  ├──────────────────▶│ └──────────────┘   │
   │                  │                     │                 │
   │                  │                     ├────────────────▶│
   │                  │                     │                 │
   │                  │                     │ POST /payment   │
   │                  │                     │ {amount, method}│
   │                  │                     │                 │
   │                  │                     │◀────────────────┤
   │                  │                     │ Status: PAID    │
   │                  │ [Domain Model]     │                 │
   │                  │◀───────────────────┤                 │
   │                  │                     │                 │
   │ ┌─ Loading ──┐   │                     │                 │
   │ │ Spinner    │   │ Update State        │                 │
   │ └────────────┘   ├─ isProcessing=true┐ │                 │
   │                  │                  │ │                 │
   │ ┌─ Success ───┐  │ Show Success   │ │                 │
   │ │ Badge       │  │ Message        └─▶│                 │
   │ └─────────────┘  │                    │                 │
   │                  │ Clear amount   ┐   │                 │
   │ ✓ Payment        │ Reset method   ├─ isProcessing=false│
   │   Complete       │                    │                 │
   │                  │ Schedule      ┐    │                 │
   │                  │ Message clear └─▶ (after 3s)         │
   │                  │                    │                 │
   │ [Print Receipt] ◀─┤                    │                 │
   │                  │                    │                 │


3. CALCULATE CHANGE
════════════════════

User Enters        BillingViewModel       UI Updates
Amount Paid             │
   │              setAmountPaid()         │
   │  "100.00"    ─────▶│                 │
   │                    │                 │
   │                    │ Calculate:      │
   │                    │ change =        │
   │                    │   amount_paid   │
   │                    │   - total       │
   │                    │                 │
   │                    │ Update State ──▶│
   │                    │ changeAmount    │
   │                    │ = 20.50         │
   │                    │                 │
   │ Display:           │                 │
   │ ┌──────────────┐   │                 │
   │ │ Amount Paid  │   │                 │
   │ │ Total        │   │                 │
   │ │ ┌──────────┐ │   │                 │
   │ │ │ Change   │ │   │                 │
   │ │ │ 20.50    │ │   │                 │
   │ │ │ (Green)  │ │   │                 │
   │ │ └──────────┘ │   │                 │
   │ └──────────────┘   │                 │


4. SALES REPORT
════════════════

Report Period      BillingViewModel       BillingRepository    API
Selected           .loadSalesReport()    .getSalesReport()    │
   │                    │                     │               │
   │ "2024-01-15"   ├──────────────────────▶│               │
   │                │                     ├──────────────────▶│
   │                │                     │                 │
   │                │                     │ GROUP BY:       │
   │                │                     │ • Payment method│
   │                │                     │ • Hour/Day      │
   │                │                     │ • Top items     │
   │                │                     │                 │
   │                │                     │◀────────────────┤
   │                │ [Report Data]       │                 │
   │                │◀─────────────────────                 │
   │                │                     │                 │
   │ Display:       ├─ Update State       │                 │
   │ • Total Sales  │  salesReport        │                 │
   │ • Transactions │                     │                 │
   │ • By Method    │ State → UI ────────▶│                 │
   │ • Tax          │                     │                 │
   │ • Top 5 Items  │                     │                 │
   │ • Charts       │                     │                 │
   │                │                     │                 │
   │ ┌────────────┐ │                     │                 │
   │ │ CASH    500│ │                     │                 │
   │ │ CARD    750│ │                     │                 │
   │ │ DIGITAL 100│ │                     │                 │
   │ └────────────┘ │                     │                 │
```

---

## 🔒 RBAC Authorization Flow

```
┌──────────────────────────────────────────────────────────────┐
│      ROLE-BASED ACCESS CONTROL (RBAC)                       │
└──────────────────────────────────────────────────────────────┘

1. LOGIN & TOKEN CREATION
═════════════════════════

User (CASHIER)      Backend Auth       Backend Storage
      │             Endpoint           (MySQL)
      │                 │                   │
      ├─ Email/Pass───▶ │                   │
      │                 │                   │
      │            Validate password        │
      │                 │                   │
      │            Check role      ◀──────────┤
      │                 │          (CASHIER) │
      │                 │                   │
      │            Create JWT:              │
      │            {user_id, role,         │
      │             store_id,              │
      │             exp: 1h}               │
      │                 │                  │
      │    JWT Token   │                  │
      │◀────────────────│                  │
      │                 │                  │
      └─ Store Token ───────────────────────┘


2. REQUEST WITH AUTHORIZATION
═══════════════════════════════

Android Client     Backend API        Middleware        Handler
   │  Attach JWT     │                 │                  │
   │  header ┌──────▶│                 │                  │
   │  GET    │        │                 │                  │
   │  /stores│        │ Validate JWT    │                  │
   │         │        │                 │                  │
   │         │        ├─ Decode token──▶│                  │
   │         │        │                 │ Verify          │
   │         │        │◀────────────────┤ signature       │
   │         │        │ {user_id, role}│ Check expiry    │
   │         │        │                 │                  │
   │         │        │ Check RBAC ────▶│ Can CASHIER    │
   │         │        │ (route, role)   │ access         │
   │         │        │                 │ /stores?       │
   │         │        │                 │                 │
   │         │        │ ✓ Allowed       │ (YES)          │
   │         │        │                 │                 │
   │         │        ├─ Request handler├────────────────▶│
   │         │        │ context: user,  │ Process:       │
   │         │        │ role, store_id  │ • Get stores   │
   │         │        │                 │   for user     │
   │         │        │                 │ • Filter by    │
   │         │        │                 │   store_id     │
   │         │        │                 │ • Return       │
   │         │        │                 │   results      │
   │         │        │                 │                │
   │         │        │◀────────────────┤ [Data]        │
   │         │        │ Response        │                │
   │  Result │◀───────│                 │                │
   │         │        │                 │                │
   └────────────────────────────────────────────────────────┘


3. PERMISSION MATRIX
════════════════════

┌─────────────────────────────────────────────────────────────┐
│ Role              Permissions                              │
├─────────────────────────────────────────────────────────────┤
│ ADMIN             • Manage users & stores                  │
│                   • All inventory operations               │
│                   • All billing operations                 │
│                   • View all reports                       │
│                   • System configuration                   │
├─────────────────────────────────────────────────────────────┤
│ STORE_MANAGER     • Manage store users                     │
│                   • View inventory                         │
│                   • View all billing                       │
│                   • Create reports                         │
│                   (Cannot delete, manage other stores)     │
├─────────────────────────────────────────────────────────────┤
│ CASHIER           • View inventory (read-only)             │
│                   • Process payments                       │
│                   • View own invoices                      │
│                   (Cannot modify pricing, delete)          │
├─────────────────────────────────────────────────────────────┤
│ INVENTORY_STAFF   • View inventory                         │
│                   • Create stock movements                 │
│                   • View stock history                     │
│                   (Cannot manage pricing or billing)       │
└─────────────────────────────────────────────────────────────┘


4. UNAUTHORIZED REQUEST
═════════════════════════

Android Client      Backend API         Middleware
   │  Attach JWT     │                   │
   │  header ┌──────▶│                   │
   │  POST   │        │                   │
   │  /users │        │ Validate JWT      │
   │ (create)│        │                   │
   │         │        ├─ Decode token ───▶│
   │         │        │ {user_id, role}  │
   │         │        │ (CASHIER)         │
   │         │        │                   │
   │         │        │ Check RBAC ──────▶│ Can CASHIER
   │         │        │ (POST /users)     │ create users?
   │         │        │                   │
   │         │        │ ✗ Denied          │ (NO)
   │         │        │                   │
   │  403    │◀───────│ 403 Forbidden     │
   │  Error  │        │ {                 │
   │ ┌─────┐ │        │   "success": false│
   │ │Insuf.│ │        │   "error": {      │
   │ │Perm. │ │        │     "code":       │
   │ │      │ │        │      "FORBIDDEN" │
   │ │      │ │        │   }               │
   │ └─────┘ │        │ }                 │
   │         │        │                   │
   └─────────────────────────────────────┘
```

---

## 📊 State Management Flow

```
┌──────────────────────────────────────────────────────────────┐
│          JETPACK COMPOSE STATE FLOW                         │
└──────────────────────────────────────────────────────────────┘

ViewModel Layer
═══════════════

┌────────────────────────────────────────┐
│ InventoryViewModel                     │
│                                        │
│  private _uiState =                   │
│    MutableStateFlow<InventoryUiState>()│
│                                        │
│  val uiState: StateFlow<...> =        │
│    _uiState.asStateFlow()              │
│                                        │
│  fun loadInventory(storeId) {          │
│    _uiState.update {                  │
│      it.copy(isLoading = true)        │
│    }                                   │
│                                        │
│    // Async operation                 │
│    viewModelScope.launch {            │
│      val items = useCase(storeId)    │
│      _uiState.update {               │
│        it.copy(                       │
│          items = items,               │
│          isLoading = false            │
│        )                              │
│      }                                │
│    }                                   │
│  }                                     │
│                                        │
└────────────────────────────────────────┘


UI Collection & Recomposition
══════════════════════════════

Composable Function (InventoryListScreen)
         │
         ├─ Collect StateFlow
         │  └─ val uiState by viewModel.uiState
         │     .collectAsStateWithLifecycle()
         │
         ├─ Subscribe to changes
         │  └─ Lifecycle-aware collection
         │     (Pauses during background)
         │
         │
         ▼
    ┌──────────────────┐
    │ UI State changes │
    │ (e.g., items=[]  │
    │  → items=[...])  │
    └────────┬─────────┘
             │
             ├─ Triggers recomposition
             │  (Diff detection)
             │
             ▼
    ┌──────────────────────┐
    │ Affected Composables │
    │ are redrawn          │
    │ (only changes)       │
    └────────┬─────────────┘
             │
             ▼
    ┌──────────────────────┐
    │ New UI displayed     │
    │ to user              │
    └──────────────────────┘


Example: Search Flow
══════════════════

UI Action:        ViewModel:           State Change:
   │                  │                    │
   │ Type in        updateSearchQuery()   │
   │ search box       │                   │
   │  "Espresso"      │                   │
   │  ┌────┐          │                   │
   │  │E   │─────────▶│                  │
   │  └────┘          │                  │
   │                  ├─ _uiState.update {
   │                  │   it.copy(
   │                  │     searchQuery =
   │                  │     "E"
   │                  │   )
   │                  │ }
   │                  │              searchQuery: "E"
   │                  │              ◀─ State Updated
   │                  │
   │                  ├─ filteredItems =
   │                  │   items.filter {
   │                  │     contains("E")
   │                  │   }
   │                  │
   │                  ├─ New filteredItems
   │                  │  list computed
   │                  │
   │  ┌────────────┐  │
   │  │[Espresso]  │◀─┤ UI Recomposes
   │  │[Matcha]    │  │ Shows matching
   │  └────────────┘  │ items only
   │                  │
   └──────────────────┘

(Repeat for each character typed)
```

---

These visual diagrams complement the detailed documentation and provide quick reference for understanding:
- System architecture and layers
- Security and authentication flows
- Data synchronization between MySQL and Redis
- Module dependencies
- Data flows for key features
- RBAC authorization
- State management with Jetpack Compose

---
