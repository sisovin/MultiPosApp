### 1. System overview

A multi-store Point of Sales (POS) Android application built with Kotlin and Jetpack Compose for modern restaurants and retail environments.

**Key features:**
- **Multi-store support:** Centralized and store-specific data handling via a shared MySQL backend.
- **Domain modules:** Inventory, billing, and restaurant-focused workflows (tables, menus, orders).
- **Secure authentication:** Argon2id password hashing, JWT access tokens, and refresh tokens handled by the backend.
- **Android architecture:** Modularized app using MVVM/Clean Architecture with Jetpack libraries.
- **UI layer:** Jetpack Compose for reactive, modern Android UI.
- **Data layer:** MySQL backend (via REST/gRPC) with Redis for caching and real-time consistency on the server side.
- **Build system:** Gradle-based multi-module Android project.

---

### 2. Architecture

#### App architecture (Android)

- **Presentation layer (UI):**  
  - Jetpack Compose screens for authentication, multi-store management, restaurant mode, inventory, and billing.  
  - Navigation handled via `Navigation Compose` with type-safe routes.

- **Domain layer:**
  - Use cases for authentication, inventory operations, billing, and reporting.
  - Interfaces for repositories (Users, Stores, Inventory, Orders, Billing).

- **Data layer:**
  - Repository implementations using:
    - **Remote:** Retrofit/Ktor client to a PHP/MySQL backend.
    - **Local (optional):** Room/Jetpack DataStore for offline caching on device.
  - DTO ↔ domain model mappers.

#### Backend architecture (preserved, but reframed)

- **Model:** MySQL + Redis data model (bi-directional synchronization on the server).
- **Service/API layer:** Exposes REST/gRPC endpoints consumed by the Android app.
- **Redis ⇄ MySQL synchronization:**
  - **Write-through caching:** Backend writes to MySQL and Redis.
  - **Background jobs:** Periodic reconciliation between Redis and MySQL.
  - **Conflict resolution:** MySQL remains the source of truth.

---

### 3. Security and authorization

- **Password hashing (backend):** Argon2id with configurable memory, iterations, and parallelism.
- **Authentication flow (Android + backend):**
  1. User logs in from Android app → credentials sent over HTTPS.
  2. Backend verifies password with Argon2id.
  3. Backend issues short-lived JWT access token and long-lived refresh token.
  4. Android app stores tokens securely (EncryptedSharedPreferences / Encrypted DataStore).
  5. An OkHttp/Retrofit interceptor attaches JWT to each request.
  6. Token refresh flow triggers when access token expires.

- **RBAC (enforced on backend, reflected in UI):**
  - **Roles:** Admin, Store Manager, Cashier, Inventory Staff.
  - **Permissions:** Backend validates access per endpoint; Android app adapts visible screens/actions based on role claims in JWT.

---

### 4. Modules (Android app modules + feature scopes)

You can structure the Gradle project as:

- `:app` – Application entry, DI wiring, navigation graph.
- `:core:ui` – Shared Compose components, themes, typography.
- `:core:data` – Networking, repositories, DTOs, token handling.
- `:core:domain` – Use cases, domain models, validation.
- `:feature:auth`, `:feature:store`, `:feature:restaurant`, `:feature:inventory`, `:feature:billing`.

#### A. Multi-store management

- **Features:**
  - Store creation, configuration, and linking (Admin/Manager only).
  - Centralized reporting across all stores (summary dashboards).
  - Store-specific inventory and billing views.

- **UI:**
  - Store selector screen.
  - Store detail & configuration screens.
  - Reporting dashboard with charts (e.g., using Compose + MPAndroidChart or similar).

#### B. Restaurant mode

- **Features:**
  - Table management (assign, merge, split tables).
  - Menu management with categories, modifiers, and variants.
  - Order tracking for dine-in, takeout, and delivery.

- **UI:**
  - Table layout screen (grid/zone-based).
  - Menu browser with filters and modifiers.
  - Order detail and status tracking screens.

#### C. Inventory system

- **Features:**
  - Stock-in, stock-out, and adjustments.
  - Supplier management.
  - Real-time stock levels (via backend using Redis).
  - Low-stock alerts (push notifications or in-app banners).

- **UI:**
  - Inventory list with search and filters.
  - Stock movement forms.
  - Supplier list and detail screens.

#### D. Billing system

- **Features:**
  - Invoice generation with tax and discount handling.
  - Multiple payment methods (cash, card, digital wallets).
  - Daily sales reports and summaries.

- **UI:**
  - Checkout screen with payment method selection.
  - Invoice preview and history.
  - Sales report screens (per store, per day, per cashier).

---

### 5. Data model (MySQL + Redis, exposed to Android)

**Backend entities (Android sees them via APIs):**

| Entity          | MySQL Table | Redis Key Example             |
|-----------------|-------------|-------------------------------|
| Users           | `users`     | `user:{id}`                   |
| Stores          | `stores`    | `store:{id}`                  |
| Inventory Items | `inventory` | `inventory:{store_id}:{id}`   |
| Orders          | `orders`    | `order:{store_id}:{id}`       |
| Billing         | `billing`   | `billing:{store_id}:{id}`     |

On Android, each of these maps to:
- **DTOs:** For network transport.
- **Domain models:** For use cases and UI.
- **Optional local entities:** For Room-based offline caching.

---

### 6. App workflow vs. legacy CLI

The original CLI routing is replaced by navigation and ViewModels:

- **Entry point:** `MainActivity` sets up NavHost with Compose.
- **Navigation:** Routes like `auth/login`, `store/list`, `restaurant/tables`, `inventory/list`, `billing/checkout`.
- **ViewModels:**
  - Each feature module has its own ViewModel(s) using `viewModelScope` + coroutines.
  - ViewModels call use cases, which call repositories.

The backend can still internally keep its PHP 8.5 CLI tools for maintenance, batch jobs, or admin scripts, but the primary user interaction is now through the Android app.

---

### 7. Deployment and scaling

- **Backend scaling:**
  - Multiple Android clients (across many stores) connect to a central MySQL + Redis cluster.
  - Redis clustering for high availability and low-latency reads.
- **Security hardening:**
  - HTTPS for all API endpoints.
  - Secure storage of refresh tokens on device.
  - Regular Argon2id parameter tuning on backend.
  - JWT rotation and revocation strategies.

- **Android release:**
  - Gradle build variants for staging/production.
  - ProGuard/R8, code shrinking, and obfuscation.
  - Play Store or private distribution for store devices.

---

### 8. Example use cases (Android flows)

- **Restaurant:**
  1. Cashier logs in from the Android POS.
  2. Selects store and table, creates an order.
  3. Items added from menu; order synced to backend.
  4. Inventory updates in Redis via backend; Android receives updated stock on next fetch/poll.
  5. Billing module generates invoice; receipt shown/printed.

- **Retail store:**
  1. Manager opens inventory screen and adds new stock.
  2. Backend updates MySQL and Redis; Android inventory list reflects changes.
  3. Cashier processes sales in billing screen.
  4. Reports screen aggregates sales across stores (for Admin/Manager roles).

---

### 9. Documentation deliverables (Android-focused)

- **System architecture diagram:**  
  Android layers (UI/Domain/Data) + Backend (API, MySQL, Redis).

- **API and database schema:**  
  MySQL tables, Redis keys, and REST/gRPC contracts.

- **Authentication flowchart:**  
  Login, token storage, refresh, and logout from Android perspective.

- **Module specifications:**  
  For each Gradle module and feature (auth, store, restaurant, inventory, billing).

- **Navigation and screen map:**  
  All Compose screens and navigation routes.

- **Deployment guide:**  
  Backend deployment + Android build/release steps.

- **Security guidelines:**  
  Token handling on Android, HTTPS, RBAC mapping to UI, and backend hardening notes.

---