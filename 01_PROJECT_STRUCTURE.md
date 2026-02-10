# MultiPosApp - Project Structure & Gradle Configuration

## 1. Directory Tree

```
MultiPosApp/
├── app/
│   ├── build.gradle.kts
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/multipos/
│   │   │   ├── MainActivity.kt
│   │   │   ├── MultiPosApp.kt
│   │   │   └── di/
│   │   │       └── AppModule.kt
│   │   └── res/
│   │       ├── values/
│   │       │   ├── colors.xml
│   │       │   ├── strings.xml
│   │       │   └── themes.xml
│   │       └── drawable/
│
├── core/
│   ├── ui/
│   │   ├── build.gradle.kts
│   │   └── src/main/java/com/multipos/core/ui/
│   │       ├── components/
│   │       │   ├── AppButton.kt
│   │       │   ├── AppTextField.kt
│   │       │   ├── AppCard.kt
│   │       │   └── LoadingDialog.kt
│   │       ├── theme/
│   │       │   ├── Color.kt
│   │       │   ├── Typography.kt
│   │       │   ├── Theme.kt
│   │       │   └── Dimensions.kt
│   │       └── utils/
│   │           └── Modifier.kt
│   │
│   ├── data/
│   │   ├── build.gradle.kts
│   │   └── src/main/java/com/multipos/core/data/
│   │       ├── api/
│   │       │   ├── ApiClient.kt
│   │       │   ├── AuthInterceptor.kt
│   │       │   └── TokenRefreshInterceptor.kt
│   │       ├── cache/
│   │       │   ├── TokenManager.kt
│   │       │   └── EncryptedPreferencesManager.kt
│   │       ├── local/
│   │       │   ├── AppDatabase.kt
│   │       │   ├── dao/
│   │       │   │   ├── UserDao.kt
│   │       │   │   ├── InventoryItemDao.kt
│   │       │   │   └── OrderDao.kt
│   │       │   └── entity/
│   │       │       ├── UserEntity.kt
│   │       │       ├── InventoryItemEntity.kt
│   │       │       └── OrderEntity.kt
│   │       ├── remote/
│   │       │   ├── dto/
│   │       │   │   ├── AuthDto.kt
│   │       │   │   ├── UserDto.kt
│   │       │   │   ├── StoreDto.kt
│   │       │   │   ├── InventoryItemDto.kt
│   │       │   │   ├── OrderDto.kt
│   │       │   │   └── BillingDto.kt
│   │       │   └── service/
│   │       │       ├── AuthService.kt
│   │       │       ├── StoreService.kt
│   │       │       ├── InventoryService.kt
│   │       │       ├── OrderService.kt
│   │       │       └── BillingService.kt
│   │       ├── mapper/
│   │       │   ├── DtoMappers.kt
│   │       │   └── EntityMappers.kt
│   │       └── repository/
│   │           ├── AuthRepository.kt
│   │           ├── StoreRepository.kt
│   │           ├── InventoryRepository.kt
│   │           ├── OrderRepository.kt
│   │           └── BillingRepository.kt
│   │
│   └── domain/
│       ├── build.gradle.kts
│       └── src/main/java/com/multipos/core/domain/
│           ├── model/
│           │   ├── User.kt
│           │   ├── Store.kt
│           │   ├── InventoryItem.kt
│           │   ├── Order.kt
│           │   └── Billing.kt
│           ├── repository/
│           │   ├── IAuthRepository.kt
│           │   ├── IStoreRepository.kt
│           │   ├── IInventoryRepository.kt
│           │   ├── IOrderRepository.kt
│           │   └── IBillingRepository.kt
│           ├── usecase/
│           │   ├── auth/
│           │   │   ├── LoginUseCase.kt
│           │   │   ├── LogoutUseCase.kt
│           │   │   └── RefreshTokenUseCase.kt
│           │   ├── inventory/
│           │   │   ├── GetInventoryItemsUseCase.kt
│           │   │   ├── UpdateStockUseCase.kt
│           │   │   └── GetLowStockItemsUseCase.kt
│           │   └── billing/
│           │       ├── CreateInvoiceUseCase.kt
│           │       ├── ProcessPaymentUseCase.kt
│           │       └── GetDailySalesUseCase.kt
│           └── util/
│               ├── Result.kt
│               └── Validation.kt
│
├── feature/
│   ├── auth/
│   │   ├── build.gradle.kts
│   │   └── src/main/java/com/multipos/feature/auth/
│   │       ├── presentation/
│   │       │   ├── AuthViewModel.kt
│   │       │   ├── screens/
│   │       │   │   ├── LoginScreen.kt
│   │       │   │   └── SplashScreen.kt
│   │       │   └── state/
│   │       │       └── AuthUiState.kt
│   │       └── di/
│   │           └── AuthModule.kt
│   │
│   ├── store/
│   │   ├── build.gradle.kts
│   │   └── src/main/java/com/multipos/feature/store/
│   │       ├── presentation/
│   │       │   ├── StoreViewModel.kt
│   │       │   ├── screens/
│   │       │   │   ├── StoreListScreen.kt
│   │       │   │   ├── StoreDetailScreen.kt
│   │       │   │   └── ReportingDashboard.kt
│   │       │   └── state/
│   │       │       └── StoreUiState.kt
│   │       └── di/
│   │           └── StoreModule.kt
│   │
│   ├── inventory/
│   │   ├── build.gradle.kts
│   │   └── src/main/java/com/multipos/feature/inventory/
│   │       ├── presentation/
│   │       │   ├── InventoryViewModel.kt
│   │       │   ├── screens/
│   │       │   │   ├── InventoryListScreen.kt
│   │       │   │   ├── StockMovementScreen.kt
│   │       │   │   └── SupplierScreen.kt
│   │       │   └── state/
│   │       │       └── InventoryUiState.kt
│   │       └── di/
│   │           └── InventoryModule.kt
│   │
│   ├── billing/
│   │   ├── build.gradle.kts
│   │   └── src/main/java/com/multipos/feature/billing/
│   │       ├── presentation/
│   │       │   ├── BillingViewModel.kt
│   │       │   ├── screens/
│   │       │   │   ├── CheckoutScreen.kt
│   │       │   │   ├── InvoiceScreen.kt
│   │       │   │   ├── PaymentMethodScreen.kt
│   │       │   │   └── SalesReportScreen.kt
│   │       │   └── state/
│   │       │       └── BillingUiState.kt
│   │       └── di/
│   │           └── BillingModule.kt
│   │
│   └── restaurant/
│       ├── build.gradle.kts
│       └── src/main/java/com/multipos/feature/restaurant/
│           ├── presentation/
│           │   ├── RestaurantViewModel.kt
│           │   ├── screens/
│           │   │   ├── TableLayoutScreen.kt
│           │   │   ├── MenuBrowserScreen.kt
│           │   │   └── OrderTrackingScreen.kt
│           │   └── state/
│           │       └── RestaurantUiState.kt
│           └── di/
│               └── RestaurantModule.kt
│
├── build.gradle.kts (root)
└── settings.gradle.kts

```

---

## 2. Root build.gradle.kts

```kotlin
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("com.android.library") version "8.2.0" apply false
    kotlin("android") version "1.9.22" apply false
    kotlin("multiplatform") version "1.9.22" apply false
    id("com.google.dagger.hilt.android") version "2.50" apply false
}

subprojects {
    repositories {
        google()
        mavenCentral()
    }
}
```

---

## 3. app/build.gradle.kts

```kotlin
plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    compileSdk = 34
    
    defaultConfig {
        applicationId = "com.multipos"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
        
        buildConfigField("String", "API_BASE_URL", "\"https://api.multipos.local\"")
        buildConfigField("String", "API_TIMEOUT_SECONDS", "30")
    }
    
    buildFeatures {
        compose = true
        buildConfig = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
    
    kotlinOptions {
        jvmTarget = "17"
    }
    
    packaging {
        resources.excludes.add("META-INF/**")
    }
}

dependencies {
    // Kotlin & Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")
    
    // Compose & Material 3
    implementation("androidx.compose.ui:ui:1.6.3")
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.compose.material:material-icons-extended:1.6.3")
    implementation("androidx.activity:activity-compose:1.8.1")
    implementation("androidx.navigation:navigation-compose:2.7.6")
    
    // Lifecycle & ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    
    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.10.0")
    implementation("com.squareup.retrofit2:converter-gson:2.10.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    
    // DI - Hilt
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-compiler:2.50")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    
    // Database
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    
    // DataStore (Encrypted)
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    
    // Work Manager
    implementation("androidx.work:work-runtime-ktx:2.8.1")
    implementation("androidx.hilt:hilt-work:1.1.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // Serialization
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    
    // Feature Modules
    implementation(project(":core:ui"))
    implementation(project(":core:data"))
    implementation(project(":core:domain"))
    implementation(project(":feature:auth"))
    implementation(project(":feature:store"))
    implementation(project(":feature:inventory"))
    implementation(project(":feature:billing"))
    implementation(project(":feature:restaurant"))
}
```

---

## 4. Core Module Dependencies Pattern

### core/ui/build.gradle.kts
```kotlin
plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdk = 34
    defaultConfig { minSdk = 26 }
    buildFeatures { compose = true }
    composeOptions { kotlinCompilerExtensionVersion = "1.5.10" }
}

dependencies {
    implementation("androidx.compose.ui:ui:1.6.3")
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
}
```

### core/data/build.gradle.kts
```kotlin
plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    compileSdk = 34
    defaultConfig { minSdk = 26 }
}

dependencies {
    implementation(project(":core:domain"))
    
    // Retrofit, OkHttp, Room, DataStore, Hilt, Coroutines
    // (same as app/build.gradle.kts networking & storage deps)
}
```

### core/domain/build.gradle.kts
```kotlin
plugins {
    id("org.jetbrains.kotlin.jvm")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("com.google.code.gson:gson:2.10.1")
}
```

### Feature Module Pattern (e.g., feature/billing/build.gradle.kts)
```kotlin
plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    compileSdk = 34
    defaultConfig { minSdk = 26 }
    buildFeatures { compose = true }
}

dependencies {
    implementation(project(":core:ui"))
    implementation(project(":core:data"))
    implementation(project(":core:domain"))
    
    // Compose, Hilt, ViewModel, Navigation
}
```

---

## 5. settings.gradle.kts

```kotlin
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

include(
    ":app",
    ":core:ui",
    ":core:data",
    ":core:domain",
    ":feature:auth",
    ":feature:store",
    ":feature:inventory",
    ":feature:billing",
    ":feature:restaurant"
)
```

---

## Summary

This structure follows **Clean Architecture + MVVM** with:
- **Separation of concerns** across data, domain, and presentation layers
- **Modularization** by feature (auth, billing, inventory, etc.)
- **Reusable core components** (UI, data access, domain logic)
- **Gradle multi-module setup** for scalability
- **Hilt DI** wired at app level with feature-level modules
- **Security-first**: EncryptedSharedPreferences, token management in core:data

Next, implement the **Data Layer** (DTOs, Repositories, API Clients).
