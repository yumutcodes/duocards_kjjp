# Network and DI Setup

## Overview

Set up enterprise-grade network infrastructure and dependency injection using Hilt, Retrofit, OkHttp, and Moshi with DataStore for token management.

## Implementation Steps

### 1. Application Class with Hilt

Create `DuoCardsApplication.kt` in the root package:

- Annotate with `@HiltAndroidApp`
- Initialize Timber for logging
- Update `AndroidManifest.xml` to reference this Application class
- Add INTERNET permission to manifest

### 2. Core Network Module (`core/network/`)

Create the following files:

**NetworkConstants.kt**

- Define `BASE_URL = "http://10.0.2.2:5000/"`
- Define timeout constants (30s connect, 30s read, 30s write)

**AuthInterceptor.kt**

- Read token from DataStore
- Add Authorization header to requests
- Handle token refresh logic

**LoggingInterceptor.kt** (wrapper for OkHttp logging)

- Configure detailed logging for debug builds only

### 3. Core DI Module (`core/di/`)

Create modular Hilt modules:

**NetworkModule.kt**

- `@Provides` OkHttpClient with interceptors (logging, auth)
- `@Provides` Moshi instance with KotlinJsonAdapterFactory
- `@Provides` Retrofit instance with base URL and Moshi converter
- All as `@Singleton`

**ApiModule.kt**

- `@Provides` AuthApiService from Retrofit
- Future API services will be added here

**RepositoryModule.kt**

- `@Binds` IAuthRepository to AuthRepositoryImpl
- Use abstract class with `@Module` and `@InstallIn(SingletonComponent::class)`

**DataStoreModule.kt**

- `@Provides` DataStore<Preferences> for token storage
- Use `preferencesDataStore` delegate

### 4. Token Manager

Create `core/data/TokenManager.kt`:

- Suspend functions to save/get/clear access and refresh tokens
- Use DataStore for encrypted storage
- Provide as singleton via Hilt

### 5. Update Existing Files

**MainActivity.kt**

- Add `@AndroidEntryPoint` annotation

**AuthRepositoryImpl.kt**

- Already has `@Inject constructor` (no changes needed)

**LoginViewModel.kt** (if exists)

- Add `@HiltViewModel` annotation
- Ensure `@Inject constructor`

### 6. Build Configuration

**build.gradle.kts**

- Add `buildConfig = true` to enable BuildConfig
- Add INTERNET permission check

## Key Design Decisions

- **Single source of truth**: All network config in NetworkModule
- **Separation of concerns**: Network, DI, and data layers are separate
- **Testability**: All dependencies injected, easy to mock
- **Security**: Tokens stored in DataStore (encrypted)
- **Logging**: Only enabled in debug builds
- **Error handling**: Centralized through ErrorMapper (already exists)
- **Scalability**: Easy to add new API services and repositories

## File Structure

```
core/
├── di/
│   ├── NetworkModule.kt
│   ├── ApiModule.kt
│   ├── RepositoryModule.kt
│   └── DataStoreModule.kt
├── network/
│   ├── NetworkConstants.kt
│   ├── AuthInterceptor.kt
│   └── interceptors/
│       └── LoggingConfig.kt
└── data/
    └── TokenManager.kt
```