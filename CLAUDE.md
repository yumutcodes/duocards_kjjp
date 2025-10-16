# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

DuoCards is an Android application built with Kotlin and Jetpack Compose. The app implements a flashcard/study system with user authentication and token-based session management.

**Tech Stack:**
- Kotlin with Jetpack Compose (Material3)
- Hilt for dependency injection
- Retrofit + OkHttp + Moshi for networking
- Room for local database
- DataStore for secure token storage
- Timber for logging

## Build Commands

```bash
# Build the project
./gradlew build

# Run debug build on device/emulator
./gradlew installDebug

# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Clean build
./gradlew clean

# Specific test
./gradlew test --tests "com.example.duocardsapplication2.SpecificTestClass"
```

## Project Structure

The codebase follows Clean Architecture with feature-based organization:

```
app/src/main/java/com/example/duocardsapplication2/
├── core/                           # Shared infrastructure
│   ├── data/                       # TokenManager (DataStore)
│   ├── di/                         # Hilt modules
│   │   ├── NetworkModule.kt        # Provides @AuthenticatedClient & @UnauthenticatedClient OkHttpClient
│   │   ├── ApiModule.kt            # Provides Retrofit API services
│   │   ├── RepositoryModule.kt     # Repository bindings
│   │   └── DataStoreModule.kt      # DataStore configuration
│   ├── navigation/                 # Navigation graph
│   ├── network/                    # Interceptors & authenticators
│   └── utiluties/                  # Common utilities
│       ├── error/                  # AppError hierarchy
│       ├── resource/               # Resource<T> wrapper
│       ├── theme/                  # Compose theming
│       └── ui/                     # UiText, UiState
└── features/                       # Feature modules
    ├── auth/
    │   ├── data/                   # DTOs & AuthRepositoryImpl
    │   ├── domain/                 # IAuthRepository interface
    │   ├── commoncomposables/      # Shared auth UI components
    │   ├── login/presentation/     # LoginScreen & LoginViewModel
    │   ├── register/presentation/  # RegisterScreen & RegisterViewModel
    │   └── splash/presentation/    # SplashScreen & SplashViewModel
    └── home/presentation/          # HomeScreen
```

## Architecture Patterns

### 1. Error Handling

The app uses a centralized error handling system:

- **AppError** (`core/utiluties/error/AppError.kt`): Sealed class hierarchy for all app errors with `UiText` for user-facing messages
- **ErrorMapper** (`core/utiluties/error/ErrorMapper.kt`): Converts HTTP codes and exceptions to AppError
- **Resource<T>** (`core/utiluties/resource/Resource.kt`): Wrapper type for async operations with Loading/Success/Error states
- **Error strings**: Defined in `app/src/main/res/values/app_errors.xml`

When adding new error handling:
1. Add error string to `app_errors.xml` if needed
2. Use appropriate AppError subclass or add new one
3. Wrap repository responses in `Resource<T>`

### 2. Token Refresh System

Automatic token refresh is implemented using OkHttp's Authenticator pattern (see `app/todo/TOKEN_REFRESH_IMPLEMENTATION.md` for detailed docs):

- **TokenAuthenticator** (`core/network/TokenAuthenticator.kt`): Intercepts 401 responses and refreshes tokens automatically
- **Two OkHttpClient instances**:
  - `@AuthenticatedClient`: For regular API calls (with auth interceptor + authenticator)
  - `@UnauthenticatedClient`: For refresh token endpoint (prevents circular dependency)
- **Thread-safe**: Uses Mutex to prevent concurrent refresh attempts
- **Retry limit**: Max 3 authentication attempts to prevent infinite loops

When adding new authenticated endpoints, use the default `AuthApiService` (automatically uses `@AuthenticatedClient`).

### 3. Dependency Injection

Hilt is used throughout the app. Key modules:

- **NetworkModule**: Provides both authenticated and unauthenticated HTTP clients
- **ApiModule**: Provides API service instances
- **RepositoryModule**: Binds repository interfaces to implementations
- **DataStoreModule**: Provides DataStore instance

When adding new features:
1. Create repository interface in `domain/` package
2. Implement in `data/` package and inject API service
3. Add binding in `RepositoryModule`
4. Inject into ViewModel with `@HiltViewModel`

### 4. Navigation

Type-safe navigation using Compose Navigation with kotlinx.serialization:

- Screen definitions in `core/navigation/Screens.kt` (using `@Serializable` data objects)
- Navigation graph in `core/navigation/AppNavigation.kt`
- ViewModels emit navigation events via `SharedFlow` (not direct navigation calls)

Navigation pattern in ViewModels:
```kotlin
private val _navigationEvent = MutableSharedFlow<NavigationEvent>(
    replay = 1,
    extraBufferCapacity = 1,
    onBufferOverflow = BufferOverflow.DROP_OLDEST
)
```

### 5. UI State Management

Consistent state management pattern across features:

- **StateFlow**: For UI state (e.g., `LoginUiState`)
- **SharedFlow**: For one-time events (e.g., navigation, snackbar messages)
- **UiState<T>**: Wrapper for async operations in UI layer (Loading/Success/Error)

Example ViewModel pattern:
```kotlin
private val _uiState = MutableStateFlow(FeatureUiState())
val uiState: StateFlow<FeatureUiState> = _uiState.asStateFlow()
```

### 6. Repository Pattern

Repositories return `Flow<Resource<T>>` for all async operations:

```kotlin
override fun login(loginRequest: LoginRequest): Flow<Resource<LoginResponse>> = flow {
    emit(Resource.Loading)
    try {
        val res = api.login(loginRequest)
        if (res.isSuccessful) {
            res.body()?.let { emit(Resource.Success(it)) }
                ?: emit(Resource.Error(errorMapper.mapHttpCode(res.code())))
        } else {
            emit(Resource.Error(errorMapper.mapHttpCode(res.code())))
        }
    } catch (t: Throwable) {
        emit(Resource.Error(errorMapper.mapToAppError(t)))
    }
}.flowOn(Dispatchers.IO)
```

Always use `ErrorMapper` to convert errors and emit on `Dispatchers.IO`.

## Important Configuration

### Network Configuration

Base URL and timeouts defined in `core/network/NetworkConstants.kt`.

The app uses a custom network security config (`app/src/main/res/xml/network_security_config.xml`) that allows cleartext traffic to localhost (for development).

### Build Configuration

- **compileSdk**: 36
- **minSdk**: 24
- **targetSdk**: 36
- **Java Version**: 17
- **Kotlin JVM Target**: 17

### Logging

Timber is used for logging (only in debug builds). Use `Timber.d()`, `Timber.e()`, etc. instead of `Log`.

## Common Tasks

### Adding a New Feature Screen

1. Create feature package under `features/`
2. Create `domain/` (interfaces), `data/` (implementations, DTOs), and `presentation/` subdirectories
3. Define screen in `Screens.kt` as `@Serializable data object`
4. Create ViewModel with `@HiltViewModel`
5. Create Composable screen function
6. Add route to `AppNavigation.kt`
7. Add repository binding to `RepositoryModule.kt`

### Adding a New API Endpoint

1. Add DTOs in `features/[feature]/data/dto/`
2. Add endpoint to appropriate `*ApiService.kt` interface
3. Update repository interface and implementation
4. Handle new error cases in `ErrorMapper` if needed

### Adding New Error Types

1. Add string resource to `app/src/main/res/values/app_errors.xml`
2. Add sealed class case to `AppError.kt` with appropriate `UiText`
3. Update `ErrorMapper.kt` to map HTTP codes or exceptions to new error

### Modifying Token Storage

Token storage is centralized in `TokenManager` using DataStore. All token operations should go through this class (never directly access DataStore elsewhere).

## String Resources

User-facing strings use `UiText` abstraction:
- **UiText.StringResource**: For strings defined in XML
- **UiText.DynamicString**: For runtime strings

Error messages are in `app/src/main/res/values/app_errors.xml`, general strings in `values/strings.xml`.
