# Implementation Guide & Deployment Documentation

---

## 1. Development Environment Setup

### Prerequisites

```bash
# Android Development
- Android Studio Giraffe (2022.3.1) or later
- JDK 17+
- SDK 34 (target), SDK 26 (minimum)
- Kotlin 1.9.22
- Gradle 8.2

# Backend Development
- PHP 8.5+ or Node.js 18+
- MySQL 8.0+
- Redis 7.0+
- Git 2.30+
```

### Clone & Setup Project

```bash
# 1. Clone repository
git clone https://github.com/yourorg/multipos-app.git
cd multipos-app

# 2. Create local.properties
echo "sdk.dir=~/Android/Sdk" > local.properties

# 3. Build Android app
./gradlew assembleDebug

# 4. Setup backend
cd backend
cp .env.example .env
# Edit .env with your database credentials
php artisan key:generate
php artisan migrate
php artisan db:seed
```

---

## 2. Project Structure Deep Dive

### Core:UI Module Components

```kotlin
// Material 3 Theme
com/multipos/core/ui/theme/
  ├── Color.kt          // Color palette
  ├── Typography.kt     // Font definitions
  ├── Theme.kt          // CompositionLocal setup
  └── Dimensions.kt     // Spacing/sizing constants

// Reusable Components
com/multipos/core/ui/components/
  ├── AppButton.kt
  ├── AppTextField.kt
  ├── AppCard.kt
  ├── AppDialog.kt
  ├── LoadingDialog.kt
  ├── AppSnackbar.kt
  └── Modifiers.kt      // Custom modifiers
```

### Navigation Structure

```
app/
  ├── MainActivity.kt
  └── navigation/
      ├── NavGraph.kt          // Main navigation
      ├── AuthNavigation.kt
      ├── StoreNavigation.kt
      ├── InventoryNavigation.kt
      ├── BillingNavigation.kt
      └── RestaurantNavigation.kt

sealed class NavRoute(val route: String) {
    object Splash : NavRoute("splash")
    object Login : NavRoute("auth/login")
    object StoreList : NavRoute("stores/list")
    object StoreDetail : NavRoute("stores/{id}")
    object InventoryList : NavRoute("inventory/list")
    object CheckoutScreen : NavRoute("billing/checkout")
}
```

---

## 3. Use Cases Implementation

### example: Authentication Use Cases

```kotlin
// core/domain/usecase/auth/LoginUseCase.kt
class LoginUseCase @Inject constructor(
    private val authRepository: IAuthRepository,
    private val tokenManager: TokenManager
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return try {
            // Validate input
            if (email.isEmpty() || password.isEmpty()) {
                throw IllegalArgumentException("Email and password required")
            }
            
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                throw IllegalArgumentException("Invalid email format")
            }
            
            // Attempt login
            val (user, accessToken, refreshToken, expiresIn) = authRepository.login(
                email = email,
                password = password
            )
            
            // Save tokens securely
            tokenManager.saveTokens(accessToken, refreshToken, expiresIn)
            
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

// core/domain/usecase/auth/RefreshTokenUseCase.kt
class RefreshTokenUseCase @Inject constructor(
    private val authRepository: IAuthRepository,
    private val tokenManager: TokenManager
) {
    suspend operator fun invoke(): Result<String> {
        return try {
            val refreshToken = tokenManager.getRefreshToken()
                ?: throw IllegalStateException("No refresh token available")
            
            val (newAccessToken, newRefreshToken, expiresIn) = authRepository.refreshToken(
                refreshToken = refreshToken
            )
            
            tokenManager.saveTokens(newAccessToken, newRefreshToken, expiresIn)
            
            Result.Success(newAccessToken)
        } catch (e: Exception) {
            tokenManager.clearTokens() // Clear on failure
            Result.Error(e)
        }
    }
}
```

---

## 4. WorkManager Integration for Background Sync

### Inventory Sync Worker

```kotlin
// feature/inventory/src/main/java/com/multipos/feature/inventory/work/InventorySyncWorker.kt

class InventorySyncWorker(
    context: Context,
    params: WorkerParameters,
    private val inventoryRepository: IInventoryRepository
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Get store ID from input data
            val storeId = inputData.getString("store_id") ?: return@withContext Result.retry()
            
            // Sync inventory with backend
            val items = inventoryRepository.getInventory(storeId)
            
            // Update local cache
            // (would update Room database if offline support implemented)
            
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Sync failed", e)
            Result.retry()
        }
    }
    
    companion object {
        private const val TAG = "InventorySyncWorker"
        const val WORK_NAME = "inventory_sync"
        
        fun enqueuePeriodicWork(context: Context, storeId: String) {
            val syncWork = PeriodicWorkRequestBuilder<InventorySyncWorker>(
                30, TimeUnit.MINUTES
            ).setInputData(
                workDataOf("store_id" to storeId)
            ).setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                15, TimeUnit.MINUTES
            ).build()
            
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                syncWork
            )
        }
    }
}

// Register in Hilt
@Module
@InstallIn(SingletonComponent::class)
object WorkerModule {
    
    @Provides
    fun provideWorkerFactory(
        inventoryRepository: IInventoryRepository
    ): WorkerFactory {
        return object : WorkerFactory() {
            override fun createWorker(
                appContext: Context,
                workerClassName: String,
                params: WorkerParameters
            ) = when (workerClassName) {
                InventorySyncWorker::class.java.name ->
                    InventorySyncWorker(appContext, params, inventoryRepository)
                else -> null
            }
        }
    }
}
```

---

## 5. Repository Implementation Pattern

### Inventory Repository

```kotlin
// core/data/repository/InventoryRepository.kt

class InventoryRepository @Inject constructor(
    private val inventoryService: InventoryService,
    private val tokenManager: TokenManager,
    private val gson: Gson
) : IInventoryRepository {
    
    override suspend fun getInventory(
        storeId: String,
        category: String?,
        skip: Int,
        limit: Int
    ): List<InventoryItem> {
        try {
            val token = tokenManager.getAccessToken()
                ?: throw IllegalStateException("No auth token")
            
            val response = inventoryService.getInventory(
                storeId = storeId,
                category = category,
                skip = skip,
                limit = limit,
                token = "Bearer $token"
            )
            
            return response.data?.map { it.toDomain() } ?: emptyList()
        } catch (e: HttpException) {
            when (e.code()) {
                401 -> throw AuthenticationException("Token expired")
                403 -> throw AuthorizationException("Insufficient permissions")
                404 -> throw NotFoundException("Store not found")
                else -> throw e
            }
        }
    }
    
    override suspend fun updateStock(
        itemId: String,
        storeId: String,
        quantity: Double,
        type: StockMovementType,
        reason: String,
        userId: String
    ): StockMovement {
        val token = tokenManager.getAccessToken()
            ?: throw IllegalStateException("No auth token")
        
        val request = StockMovementRequestDto(
            itemId = itemId,
            quantity = quantity,
            type = type.name,
            reason = reason,
            createdBy = userId
        )
        
        val response = inventoryService.createStockMovement(
            storeId = storeId,
            request = request,
            token = "Bearer $token"
        )
        
        return response.data?.toDomain()
            ?: throw RuntimeException("No response data")
    }
    
    override suspend fun getLowStockItems(storeId: String): List<InventoryItem> {
        val token = tokenManager.getAccessToken()
            ?: throw IllegalStateException("No auth token")
        
        val response = inventoryService.getLowStockItems(
            storeId = storeId,
            token = "Bearer $token"
        )
        
        return response.data?.map { it.toDomain() } ?: emptyList()
    }
}
```

---

## 6. Material 3 Theme Implementation

### core/ui/theme/Color.kt

```kotlin
package com.multipos.core.ui.theme

import androidx.compose.ui.graphics.Color

// Primary Brand Colors
val Primary = Color(0xFF1976D2)
val OnPrimary = Color.White
val PrimaryContainer = Color(0xFFBBDEFB)
val OnPrimaryContainer = Color(0xFF0D47A1)

// Secondary Colors
val Secondary = Color(0xFF455A64)
val OnSecondary = Color.White
val SecondaryContainer = Color(0xFFCFD8DC)
val OnSecondaryContainer = Color(0xFF263238)

// Tertiary Colors (Accent)
val Tertiary = Color(0xFF00BCD4)
val OnTertiary = Color.White
val TertiaryContainer = Color(0xFFB2EBF2)
val OnTertiaryContainer = Color(0xFF00838F)

// Error Colors
val Error = Color(0xFFD32F2F)
val OnError = Color.White
val ErrorContainer = Color(0xFFFFCDD2)
val OnErrorContainer = Color(0xFFB71C1C)

// Success Colors
val Success = Color(0xFF388E3C)
val OnSuccess = Color.White
val SuccessContainer = Color(0xFFC8E6C9)
val OnSuccessContainer = Color(0xFF1B5E20)

// Warning Colors
val Warning = Color(0xFFFFA726)
val OnWarning = Color.White
val WarningContainer = Color(0xFFFFE0B2)
val OnWarningContainer = Color(0xFFE65100)

// Neutral Colors
val Neutral = Color(0xFF757575)
val NeutralVariant = Color(0xFF9E9E9E)
val Background = Color(0xFFFAFAFA)
val Surface = Color.White
val Outline = Color(0xFFBDBDBD)
val OutlineVariant = Color(0xFFE0E0E0)
```

### core/ui/theme/Typography.kt

```kotlin
package com.multipos.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.multipos.core.ui.R

// Custom font families
val PlusJakartaSans = FontFamily(
    Font(R.font.plus_jakarta_sans_regular),
    Font(R.font.plus_jakarta_sans_bold, FontWeight.Bold),
    Font(R.font.plus_jakarta_sans_semi_bold, FontWeight.SemiBold)
)

val Poppins = FontFamily(
    Font(R.font.poppins_regular),
    Font(R.font.poppins_bold, FontWeight.Bold)
)

val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp
    ),
    displayMedium = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp
    ),
    displaySmall = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    titleLarge = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    titleSmall = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelLarge = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelMedium = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        lineHeight = 16.sp
    )
)
```

---

## 7. Testing Strategy

### Unit Tests (ViewModel)

```kotlin
// feature/billing/src/test/java/com/multipos/feature/billing/BillingViewModelTest.kt

@RunWith(RobolectricTestRunner::class)
class BillingViewModelTest {
    
    private lateinit var viewModel: BillingViewModel
    
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    private val dispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        
        val mockCreateInvoiceUseCase: CreateInvoiceUseCase = mockk()
        val mockProcessPaymentUseCase: ProcessPaymentUseCase = mockk()
        val mockGetDailySalesUseCase: GetDailySalesUseCase = mockk()
        
        viewModel = BillingViewModel(
            mockCreateInvoiceUseCase,
            mockProcessPaymentUseCase,
            mockGetDailySalesUseCase
        )
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun testProcessPaymentSuccess() = runTest {
        // Arrange
        val invoiceId = "test-invoice-id"
        val amountPaid = BigDecimal("100.00")
        
        viewModel.setPaymentMethod(PaymentMethod.CASH)
        viewModel.setAmountPaid("100.00")
        
        // Act
        viewModel.processPayment(invoiceId, "user-id")
        advanceUntilIdle()
        
        // Assert
        val state = viewModel.uiState.value
        assertEquals(PaymentMethod.CASH, state.selectedPaymentMethod)
        assertEquals("", state.amountPaid) // Reset after success
    }
}
```

---

## 8. Release & Deployment

### Android Release Checklist

```
Pre-Release:
[ ] Update version in build.gradle.kts
[ ] Run all tests: ./gradlew testDebug
[ ] Lint check: ./gradlew lintRelease
[ ] ProGuard verification
[ ] Update CHANGELOG.md
[ ] Test on multiple devices

Build Release:
[ ] Generate signed APK: ./gradlew assembleRelease
[ ] Sign APK with keystore
[ ] Generate App Bundle: ./gradlew bundleRelease

Upload:
[ ] Create release notes
[ ] Upload to Firebase App Distribution
[ ] Or upload to Play Store beta track
[ ] Rollout to 10% users first
```

### Backend Deployment (Docker Compose)

```yaml
# docker-compose.yml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: ${DB_ROOT_PASSWORD}
      MYSQL_DATABASE: multipos_db
      MYSQL_USER: ${DB_USER}
      MYSQL_PASSWORD: ${DB_PASSWORD}
    volumes:
      - mysql_data:/var/lib/mysql
      - ./database/schema.sql:/docker-entrypoint-initdb.d/schema.sql
    ports:
      - "3306:3306"
    networks:
      - multipos_network

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    networks:
      - multipos_network

  app:
    build: .
    environment:
      DB_HOST: mysql
      DB_USER: ${DB_USER}
      DB_PASSWORD: ${DB_PASSWORD}
      REDIS_HOST: redis
      APP_ENV: production
    ports:
      - "8000:8000"
    depends_on:
      - mysql
      - redis
    networks:
      - multipos_network
    restart: always

volumes:
  mysql_data:
  redis_data:

networks:
  multipos_network:
    driver: bridge
```

---

## 9. Performance Optimization

### Compose Performance Tips

```kotlin
// 1. Use remember to cache expensive computations
val filteredItems = remember(items, searchQuery) {
    items.filter { it.name.contains(searchQuery) }
}

// 2. Use keys in LazyColumn
LazyColumn {
    items(items, key = { it.id }) { item ->
        ItemRow(item)
    }
}

// 3. Avoid recomposition with primitives
val quantity by remember { mutableStateOf(10) } // Good
// instead of
val item by remember { mutableStateOf(InventoryItem(...)) } // Potentially bad

// 4. Use derivedStateOf for complex state
val isValid by remember {
    derivedStateOf {
        email.isNotBlank() && password.length >= 8
    }
}
```

---

## 10. Architecture Summary Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                     ANDROID APPLICATION                      │
├──────────────────────────────────────────────────────────────┤
│                       PRESENTATION LAYER                      │
│  (Jetpack Compose UI + Material 3 + ViewModels + State)      │
├──────────────────────────────────────────────────────────────┤
│                        DOMAIN LAYER                           │
│           (Use Cases, Domain Models, Interfaces)             │
├──────────────────────────────────────────────────────────────┤
│                        DATA LAYER                             │
│  ┌─────────────────┐  ┌──────────────┐  ┌─────────────────┐ │
│  │  Remote (REST)  │  │  Token Mgmt   │  │  Repositories   │ │
│  │  (Retrofit)     │  │  (Encrypted)  │  │  (Mappers)      │ │
│  └─────────────────┘  └──────────────┘  └─────────────────┘ │
│         ↓                    ↓                   ↓            │
│  ┌──────────────────────────────────────────────────────┐   │
│  │          OkHttp + Interceptors                        │   │
│  │  (Auth, Token Refresh, Logging)                       │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                              ↓ (HTTPS)
┌─────────────────────────────────────────────────────────────┐
│                     BACKEND API SERVER                        │
│                   (Laravel/Node.js)                           │
├──────────────────────────────────────────────────────────────┤
│  ┌──────────────────────────────────────────────────────┐   │
│  │         REQUEST PROCESSING                            │   │
│  │  (JWT Validation, RBAC, Input Validation)            │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
         ↙                                              ↘
   ┌──────────────────────────┐     ┌──────────────────────┐
   │   MySQL DATABASE         │     │   Redis Cache        │
   │  (Source of Truth)       │     │  (Real-time Data)    │
   │                          │     │                      │
   │ • Users                  │     │ • User Sessions      │
   │ • Stores                 │     │ • Store Data         │
   │ • Inventory              │     │ • Inventory Lists    │
   │ • Orders                 │     │ • Active Orders      │
   │ • Invoices               │     │ • Daily Reports      │
   │ • Audit Logs             │     │ • Stock Levels       │
   │                          │     │ • Token Blacklist    │
   └──────────────────────────┘     └──────────────────────┘
            ↑ Write-Through ↓            ↑ Cache Miss ↓
            └─────────────────────────────────────────┘
```

---

