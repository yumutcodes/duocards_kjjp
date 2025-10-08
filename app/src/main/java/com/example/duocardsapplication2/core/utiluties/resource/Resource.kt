package com.example.duocardsapplication2.core.utiluties.result

import com.example.duocardsapplication2.core.utiluties.error.AppError

/**
 * Represents the state of an async operation with data
 *
 * States:
 * - Idle: Initial state, no operation started
 * - Loading: Operation in progress (can have cached data)
 * - Success: Operation succeeded with data
 * - Error: Operation failed without data
 * - ErrorWithData: Operation failed but cached data available
 */
sealed class Resource<out T> {

    /**
     * Initial state - no operation started yet
     */
    data object Idle : Resource<Nothing>()

    /**
     * Loading state with optional cached data
     * @param data Cached/stale data to show during loading
     * @param isRefreshing True if refreshing existing data (pull-to-refresh)
     */
    data class Loading<T>(
        val data: T? = null,
        override val isRefreshing: Boolean = false
    ) : Resource<T>()

    /**
     * Success state with data
     */
    data class Success<T>(val data: T) : Resource<T>()

    /**
     * Error state WITHOUT data
     * Use when no cached/stale data is available
     */
    data class Error<T>(
        val error: AppError
    ) : Resource<T>()  // ✅ Fixed: Generic T instead of Nothing

    /**
     * Error state WITH cached data
     * Use when cached/stale data should be shown alongside error
     * Common in offline-first apps
     * @param data Non-nullable cached data to display
     */
    data class ErrorWithData<T>(
        val error: AppError,
        val data: T
    ) : Resource<T>()

    // ============================================
    // State Check Properties
    // ============================================

    val isIdle: Boolean get() = this is Idle
    val isLoading: Boolean get() = this is Loading
    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error<*> || this is ErrorWithData<*>

    open val isRefreshing: Boolean get() = false

    /**
     * Check if is initial loading (no cached data)
     */
    fun isInitialLoading(): Boolean = this is Loading && data == null

    /**
     * Check if has any data (in any state)
     */
    fun hasData(): Boolean = dataOrNull() != null

    // ============================================
    // Data Access Methods
    // ============================================

    /**
     * Returns data if available in any state
     */
    fun dataOrNull(): T? = when (this) {
        is Idle -> null
        is Loading -> data
        is Success -> data
        is Error -> null
        is ErrorWithData -> data
    }

    /**
     * Returns data or throws exception
     */
    fun getOrThrow(): T = when (this) {
        is Idle -> throw IllegalStateException("Resource is in Idle state")
        is Loading -> data ?: throw IllegalStateException("No data in Loading state")
        is Success -> data
        is Error -> throw IllegalStateException("Error: ${error.message}")
        is ErrorWithData -> data
    }

    /**
     * Returns data or default value
     */
    fun getOrDefault(default: T): T = dataOrNull() ?: default

    /**
     * Returns data only if Success, null otherwise
     */
    fun getOrNull(): T? = if (this is Success) data else null

    // ============================================
    // Error Access Methods
    // ============================================

    /**
     * Returns error if in error state
     */
    fun errorOrNull(): AppError? = when (this) {
        is Error -> error
        is ErrorWithData -> error
        else -> null
    }

    // ============================================
    // Transformation Methods
    // ============================================

    /**
     * Transform data if available
     */
    inline fun <R> map(transform: (T) -> R): Resource<R> = when (this) {
        is Idle -> Idle
        is Loading -> Loading(data?.let(transform), isRefreshing)
        is Success -> Success(transform(data))
        is Error -> Error(error)  // ✅ Fixed: Create new Error<R>
        is ErrorWithData -> ErrorWithData(error, transform(data))
    }

    /**
     * Suspend version for async transformations
     */
    suspend inline fun <R> mapSuspend(crossinline transform: suspend (T) -> R): Resource<R> = when (this) {
        is Idle -> Idle
        is Loading -> Loading(data?.let { transform(it) }, isRefreshing)
        is Success -> Success(transform(data))
        is Error -> Error(error)  // ✅ Fixed: Create new Error<R>
        is ErrorWithData -> ErrorWithData(error, transform(data))
    }

    /**
     * Flat map for nested Resources
     */
    inline fun <R> flatMap(transform: (T) -> Resource<R>): Resource<R> = when (this) {
        is Idle -> Idle
        is Loading -> if (data != null) transform(data) else Loading()
        is Success -> transform(data)
        is Error -> Error(error)  // ✅ Fixed: Create new Error<R>
        is ErrorWithData -> transform(data)
    }

    // ============================================
    // Callback Methods (Fluent API)
    // ============================================

    /**
     * Execute action on success
     */
    inline fun onSuccess(action: (T) -> Unit): Resource<T> {
        if (this is Success) action(data)
        return this
    }

    /**
     * Execute action on error (any error type)
     */
    inline fun onError(action: (AppError) -> Unit): Resource<T> {
        when (this) {
            is Error -> action(error)
            is ErrorWithData -> action(error)
            else -> {}
        }
        return this
    }

    /**
     * Execute action on loading
     */
    inline fun onLoading(action: (T?) -> Unit): Resource<T> {
        if (this is Loading) action(data)
        return this
    }

    /**
     * Execute action on idle
     */
    inline fun onIdle(action: () -> Unit): Resource<T> {
        if (this is Idle) action()
        return this
    }

    /**
     * Handle all states in one call
     */
    inline fun handle(
        onIdle: () -> Unit = {},
        onLoading: (T?) -> Unit = {},
        onSuccess: (T) -> Unit = {},
        onError: (AppError) -> Unit = {},
        onErrorWithData: (AppError, T) -> Unit = { error, _ -> onError(error) }
    ) {
        when (this) {
            is Idle -> onIdle()
            is Loading -> onLoading(data)
            is Success -> onSuccess(data)
            is Error -> onError(error)
            is ErrorWithData -> onErrorWithData(error, data)
        }
    }

    // ============================================
    // Companion Object - Factory Methods
    // ============================================

    companion object {
        /**
         * Create idle state
         */
        fun <T> idle(): Resource<T> = Idle

        /**
         * Create loading state
         */
        fun <T> loading(cachedData: T? = null, isRefreshing: Boolean = false): Resource<T> =
            Loading(cachedData, isRefreshing)

        /**
         * Create success state
         */
        fun <T> success(data: T): Resource<T> = Success(data)

        /**
         * Create error state (no data)
         */
        fun <T> error(error: AppError): Resource<T> = Error(error)  // ✅ Fixed: Generic T

        /**
         * Create error state with cached data
         */
        fun <T> errorWithData(error: AppError, data: T): Resource<T> =
            ErrorWithData(error, data)
    }
}

// ============================================
// Extension Functions for Flow/LiveData
// ============================================

/**
 * Convert to Resource.Success
 */
fun <T> T.toSuccessResource(): Resource<T> = Resource.Success(this)

/**
 * Convert to Resource.Error
 */
fun <T> AppError.toErrorResource(): Resource<T> = Resource.Error(this)  // ✅ Fixed: Generic T

/**
 * Convert to Resource.ErrorWithData
 */
fun <T> AppError.toErrorResourceWithData(data: T): Resource<T> =
    Resource.ErrorWithData(this, data)