
sealed class Resource<out T> {

    /**
     * Success state with data
     */
    data class Success<T>(val data: T) : Resource<T>()

    /**
     * Loading state with optional cached data
     */
    data class Loading<T>(
        val data: T? = null,
        override val isRefreshing: Boolean = false
    ) : Resource<T>()

    /**
     * Error state WITHOUT data
     * Use when no cached/stale data is available
     */
    data class Error<T>(
        val error: AppError
    ) : Resource<T>()

    /**
     * Error state WITH data
     * Use when cached/stale data should be shown alongside error
     * @param data Non-nullable cached data to display
     */
    data class ErrorWithData<T>(
        val error: AppError,
        val data: T  // ✅ Non-nullable!
    ) : Resource<T>()

    // ============================================
    // Properties
    // ============================================

    val isSuccess: Boolean get() = this is Success
    val isLoading: Boolean get() = this is Loading
    val isError: Boolean get() = this is Error || this is ErrorWithData
    open val isRefreshing: Boolean get() = false

    /**
     * Returns data if available in any state
     */
    fun dataOrNull(): T? = when (this) {
        is Success -> data
        is Loading -> data
        is Error -> null  // ✅ No data
        is ErrorWithData -> data  // ✅ Has data
    }

    /**
     * Returns error if in error state
     */
    fun errorOrNull(): AppError? = when (this) {
        is Error -> error
        is ErrorWithData -> error
        else -> null
    }

    /**
     * Check if has any data (in any state)
     */
    fun hasData(): Boolean = dataOrNull() != null

    /**
     * Check if is initial loading (no cached data)
     */
    fun isInitialLoading(): Boolean = this is Loading && data == null
}