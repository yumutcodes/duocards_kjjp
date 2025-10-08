package com.example.duocardsapplication2.core.utiluties.error



import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.example.duocardsapplication2.R
import com.example.duocardsapplication2.core.utiluties.ui.UiText
import timber.log.Timber



/*
https://chatgpt.com/c/68e6649d-2038-8325-b2ec-d0f7379d69fe
buna bak
 */
sealed class AppError {
    abstract val message: UiText
    abstract val cause: Throwable?
    abstract val isRetryable: Boolean

    // ============================================
    // Network Errors
    // ============================================

    sealed class Network(
        override val message: UiText,
        override val cause: Throwable?,
        override val isRetryable: Boolean = true
    ) : AppError() {

        data class NoConnection(
            override val message: UiText = UiText.StringResource(R.string.error_no_connection),
            override val cause: Throwable?
        ) : Network(message, cause)

        data class Timeout(
            override val message: UiText = UiText.StringResource(R.string.error_timeout),
            override val cause: Throwable?
        ) : Network(message, cause)

        data class ServerError(
            override val message: UiText = UiText.StringResource(R.string.error_server),
            override val cause: Throwable?
        ) : Network(message, cause)

        data class SSL(
            override val message: UiText = UiText.StringResource(R.string.error_ssl),
            override val cause: Throwable?,
            override val isRetryable: Boolean = false
        ) : Network(message, cause, isRetryable)

        data class Unknown(
            override val message: UiText = UiText.StringResource(R.string.error_network),
            override val cause: Throwable?
        ) : Network(message, cause)
    }

    // ============================================
    // API Errors
    // ============================================

    sealed class Api(
        override val message: UiText,
        open val httpCode: Int,
        override val cause: Throwable?,
        override val isRetryable: Boolean = false
    ) : AppError() {

        // 4xx Client Errors
        data class BadRequest(
            override val message: UiText = UiText.StringResource(R.string.error_bad_request),
            override val cause: Throwable?
        ) : Api(message, 400, cause)

        data class Unauthorized(
            override val message: UiText = UiText.StringResource(R.string.error_unauthorized),
            override val cause: Throwable?
        ) : Api(message, 401, cause)

        data class Forbidden(
            override val message: UiText = UiText.StringResource(R.string.error_forbidden),
            override val cause: Throwable?
        ) : Api(message, 403, cause)

        data class NotFound(
            override val message: UiText = UiText.StringResource(R.string.error_not_found),
            override val cause: Throwable?
        ) : Api(message, 404, cause)

        data class Conflict(
            override val message: UiText = UiText.StringResource(R.string.error_conflict),
            override val cause: Throwable?
        ) : Api(message, 409, cause)

        data class UnprocessableEntity(
            override val message: UiText = UiText.StringResource(R.string.error_validation),
            override val cause: Throwable?
        ) : Api(message, 422, cause)

        data class RateLimited(
            override val message: UiText = UiText.StringResource(R.string.error_rate_limited),
            override val cause: Throwable?,
            val retryAfter: Long? = null
        ) : Api(message, 429, cause, isRetryable = true)

        // 5xx Server Errors
        data class InternalServerError(
            override val message: UiText = UiText.StringResource(R.string.error_server_internal),
            override val cause: Throwable?
        ) : Api(message, 500, cause, isRetryable = true)

        data class BadGateway(
            override val message: UiText = UiText.StringResource(R.string.error_server_gateway),
            override val cause: Throwable?
        ) : Api(message, 502, cause, isRetryable = true)

        data class ServiceUnavailable(
            override val message: UiText = UiText.StringResource(R.string.error_server_unavailable),
            override val cause: Throwable?
        ) : Api(message, 503, cause, isRetryable = true)

        data class GatewayTimeout(
            override val message: UiText = UiText.StringResource(R.string.error_server_timeout),
            override val cause: Throwable?
        ) : Api(message, 504, cause, isRetryable = true)

        // Business Logic Errors
        data class Business(
            val errorCode: String,
            override val message: UiText,
            override val cause: Throwable? = null
        ) : Api(message, 0, cause)

        // Unknown HTTP Error
        data class Unknown(
            override val httpCode: Int,
            override val message: UiText,
            override val cause: Throwable?
        ) : Api(message, httpCode, cause, isRetryable = httpCode >= 500)

        companion object {
            fun fromHttpCode(code: Int, cause: Throwable?, message: UiText? = null): Api {
                return when (code) {
                    400 -> BadRequest(message ?: UiText.StringResource(R.string.error_bad_request), cause)
                    401 -> Unauthorized(cause = cause)
                    403 -> Forbidden(cause = cause)
                    404 -> NotFound(cause = cause)
                    409 -> Conflict(cause = cause)
                    422 -> UnprocessableEntity(cause = cause)
                    429 -> RateLimited(cause = cause)
                    500 -> InternalServerError(cause = cause)
                    502 -> BadGateway(cause = cause)
                    503 -> ServiceUnavailable(cause = cause)
                    504 -> GatewayTimeout(cause = cause)
                    else -> Unknown(
                        httpCode = code,
                        message = message ?: UiText.DynamicString("HTTP Error $code"),
                        cause = cause
                    )
                }
            }
        }
    }

    // ============================================
    // Database Errors
    // ============================================

    sealed class Database(
        override val message: UiText,
        override val cause: Throwable?,
        override val isRetryable: Boolean = false
    ) : AppError() {

        data class ReadFailed(
            override val message: UiText = UiText.StringResource(R.string.error_db_read),
            override val cause: Throwable?
        ) : Database(message, cause)

        data class WriteFailed(
            override val message: UiText = UiText.StringResource(R.string.error_db_write),
            override val cause: Throwable?,
            override val isRetryable: Boolean = true
        ) : Database(message, cause, isRetryable)

        data class DeleteFailed(
            override val message: UiText = UiText.StringResource(R.string.error_db_delete),
            override val cause: Throwable?
        ) : Database(message, cause)

        data class NotFound(
            override val message: UiText = UiText.StringResource(R.string.error_db_not_found),
            override val cause: Throwable?
        ) : Database(message, cause)

        data class ConstraintViolation(
            override val message: UiText = UiText.StringResource(R.string.error_db_constraint),
            override val cause: Throwable?
        ) : Database(message, cause)

        data class Unknown(
            override val message: UiText = UiText.StringResource(R.string.error_db_unknown),
            override val cause: Throwable?
        ) : Database(message, cause)
    }

    // ============================================
    // Validation Errors
    // ============================================

    sealed class Validation(
        override val message: UiText,
        override val isRetryable: Boolean = false
    ) : AppError() {
        override val cause: Throwable? = null

        data class InvalidInput(
            val field: String,
            val reason: String,
            override val message: UiText = UiText.DynamicString("$field: $reason")
        ) : Validation(message)

        data class MissingRequired(
            val field: String,
            override val message: UiText = UiText.StringResource(
                R.string.error_field_required,
                listOf(field)
            )
        ) : Validation(message)

        data class InvalidFormat(
            val field: String,
            val expectedFormat: String,
            override val message: UiText = UiText.StringResource(
                R.string.error_invalid_format,
                listOf(field, expectedFormat)
            )
        ) : Validation(message)

        data class OutOfRange(
            val field: String,
            val min: Any?,
            val max: Any?,
            override val message: UiText = UiText.DynamicString(
                "$field must be between $min and $max"
            )
        ) : Validation(message)

        data class TooShort(
            val field: String,
            val minLength: Int,
            override val message: UiText = UiText.StringResource(
                R.string.error_too_short,
                listOf(field, minLength)
            )
        ) : Validation(message)

        data class TooLong(
            val field: String,
            val maxLength: Int,
            override val message: UiText = UiText.StringResource(
                R.string.error_too_long,
                listOf(field, maxLength)
            )
        ) : Validation(message)
    }

    // ============================================
    // Authentication Errors
    // ============================================

    sealed class Authentication(
        override val message: UiText,
        override val cause: Throwable?,
        override val isRetryable: Boolean = false
    ) : AppError() {

        data class TokenExpired(
            override val message: UiText = UiText.StringResource(R.string.error_token_expired),
            override val cause: Throwable?
        ) : Authentication(message, cause, isRetryable = true)

        data class InvalidToken(
            override val message: UiText = UiText.StringResource(R.string.error_token_invalid),
            override val cause: Throwable?
        ) : Authentication(message, cause)

        data class InvalidCredentials(
            override val message: UiText = UiText.StringResource(R.string.error_invalid_credentials),
            override val cause: Throwable?
        ) : Authentication(message, cause)

        data class SessionExpired(
            override val message: UiText = UiText.StringResource(R.string.error_session_expired),
            override val cause: Throwable?
        ) : Authentication(message, cause)

        data class TwoFactorRequired(
            override val message: UiText = UiText.StringResource(R.string.error_2fa_required),
            override val cause: Throwable?
        ) : Authentication(message, cause)

        data class AccountLocked(
            override val message: UiText = UiText.StringResource(R.string.error_account_locked),
            override val cause: Throwable?
        ) : Authentication(message, cause)
    }

    // ============================================
    // Unknown Error
    // ============================================

    data class Unknown(
        override val message: UiText = UiText.StringResource(R.string.error_unknown),
        override val cause: Throwable?,
        val context: String? = null
    ) : AppError() {
        override val isRetryable = false
    }
}

// ============================================
// Extension Functions
// ============================================

/**
 * Get retry delay in milliseconds
 * fun AppError.getRetryDelay(): Long = when (this) {
 *     is AppError.Network.Timeout -> 5000L
 *     is AppError.Network.NoConnection -> 3000L
 *     is AppError.Network.ServerError -> 3000L
 *     is AppError.Api.RateLimited -> retryAfter ?: 10000L
 *     is AppError.Api.ServiceUnavailable -> 10000L
 *     is AppError.Api.GatewayTimeout -> 5000L
 *     is AppError.Api.BadGateway -> 3000L
 *     is AppError.Api.InternalServerError -> 3000L
 *     is AppError.Database.WriteFailed -> 2000L
 *     is AppError.Authentication.TokenExpired -> 1000L
 *     else -> if (isRetryable) 2000L else 0L
 * }
 */


/**
 * Check if requires user action
 */
fun AppError.requiresUserAction(): Boolean = when (this) {
    is AppError.Authentication -> when (this) {
        is AppError.Authentication.TokenExpired -> false
        else -> true
    }
    is AppError.Validation -> true
    is AppError.Api.Forbidden -> true
    is AppError.Api.Unauthorized -> true
    else -> false
}

/**
 * Get error severity
 */
enum class ErrorSeverity { LOW, MEDIUM, HIGH, CRITICAL }

fun AppError.getSeverity(): ErrorSeverity = when (this) {
    is AppError.Network -> ErrorSeverity.MEDIUM
    is AppError.Api.InternalServerError -> ErrorSeverity.HIGH
    is AppError.Api.ServiceUnavailable -> ErrorSeverity.HIGH
    is AppError.Database -> ErrorSeverity.HIGH
    is AppError.Authentication.AccountLocked -> ErrorSeverity.CRITICAL
    is AppError.Authentication -> ErrorSeverity.MEDIUM
    is AppError.Validation -> ErrorSeverity.LOW
    is AppError.Unknown -> ErrorSeverity.HIGH
    else -> ErrorSeverity.MEDIUM
}

/**
 * Get appropriate icon
 */
@Composable
fun AppError.getIcon(): ImageVector = when (this) {
    is AppError.Network.NoConnection -> Icons.Default.WifiOff
    is AppError.Network.Timeout -> Icons.Default.Timer
    is AppError.Network.SSL -> Icons.Default.Lock
    is AppError.Network -> Icons.Default.SignalWifiOff
    is AppError.Api.ServiceUnavailable -> Icons.Default.Construction
    is AppError.Api.RateLimited -> Icons.Default.Timer
    is AppError.Api -> Icons.Default.CloudOff
    is AppError.Database -> Icons.Default.Storage
    is AppError.Validation -> Icons.Default.ErrorOutline
    is AppError.Authentication -> Icons.Default.Lock
    is AppError.Unknown -> Icons.Default.Error
}

/**
 * Get error title
 */
@Composable
fun AppError.getTitle(): String = when (this) {
    is AppError.Network -> stringResource(R.string.error_title_network)
    is AppError.Api -> stringResource(R.string.error_title_server)
    is AppError.Database -> stringResource(R.string.error_title_database)
    is AppError.Validation -> stringResource(R.string.error_title_validation)
    is AppError.Authentication -> stringResource(R.string.error_title_auth)
    is AppError.Unknown -> stringResource(R.string.error_title_unknown)
}

/**
 * Log error with appropriate level
 */
fun AppError.log(tag: String = "AppError") {
    val errorType = this::class.simpleName ?: "Unknown"

    when (getSeverity()) {
        ErrorSeverity.LOW -> Timber.tag(tag).d(cause, "[$errorType] $message")
        ErrorSeverity.MEDIUM -> Timber.tag(tag).w(cause, "[$errorType] $message")
        ErrorSeverity.HIGH -> Timber.tag(tag).e(cause, "[$errorType] $message")
        ErrorSeverity.CRITICAL -> {
            Timber.tag(tag).e(cause, "[$errorType] $message")
            cause?.let {
               // FirebaseCrashlytics.getInstance().recordException(it)
            }
        }
    }

    // Log additional context
    when (this) {
        is AppError.Api -> Timber.tag(tag).d("HTTP Code: $httpCode")
        is AppError.Validation -> when (this) {
            is AppError.Validation.InvalidInput -> Timber.tag(tag).d("Field: $field, Reason: $reason")
            is AppError.Validation.MissingRequired -> Timber.tag(tag).d("Missing field: $field")
            else -> {}
        }
        is AppError.Unknown -> context?.let { Timber.tag(tag).d("Context: $it") }
        else -> {}
    }
}

/**
 * Track in analytics
 * fun AppError.trackAnalytics(analytics: FirebaseAnalytics) {
 *     val category = when (this) {
 *         is AppError.Network -> "network"
 *         is AppError.Api -> "api"
 *         is AppError.Database -> "database"
 *         is AppError.Validation -> "validation"
 *         is AppError.Authentication -> "authentication"
 *         is AppError.Unknown -> "unknown"
 *     }
 *
 *     analytics.logEvent("error_$category") {
 *         param("error_type", this@trackAnalytics::class.simpleName ?: "unknown")
 *         param("is_retryable", isRetryable.toString())
 *         param("severity", getSeverity().name.lowercase())
 *
 *         when (this@trackAnalytics) {
 *             is AppError.Api -> {
 *                 param("http_code", httpCode.toLong())
 *                 if (this@trackAnalytics is AppError.Api.Business) {
 *                     param("business_code", errorCode)
 *                 }
 *             }
 *             is AppError.Validation -> when (this@trackAnalytics) {
 *                 is AppError.Validation.InvalidInput -> param("field", field)
 *                 is AppError.Validation.MissingRequired -> param("field", field)
 *                 else -> {}
 *             }
 *             is AppError.Unknown -> context?.let { param("context", it) }
 *             else -> {}
 *         }
 *     }
 * }
 */


/**
 * Handle error with type-safe callbacks
 */
inline fun AppError.handle(
    onNetwork: (AppError.Network) -> Unit = {},
    onApi: (AppError.Api) -> Unit = {},
    onDatabase: (AppError.Database) -> Unit = {},
    onValidation: (AppError.Validation) -> Unit = {},
    onAuthentication: (AppError.Authentication) -> Unit = {},
    onUnknown: (AppError.Unknown) -> Unit = {}
) {
    when (this) {
        is AppError.Network -> onNetwork(this)
        is AppError.Api -> onApi(this)
        is AppError.Database -> onDatabase(this)
        is AppError.Validation -> onValidation(this)
        is AppError.Authentication -> onAuthentication(this)
        is AppError.Unknown -> onUnknown(this)
    }
}

// ============================================
// Error Mapper
// ============================================

/*
fun Throwable.toAppError(): AppError = when (this) {
    // Network errors
    is UnknownHostException -> AppError.Network.NoConnection(cause = this)
    is SocketTimeoutException -> AppError.Network.Timeout(cause = this)
    is SSLException,
    is SSLHandshakeException -> AppError.Network.SSL(cause = this)
    is SocketException -> AppError.Network.ServerError(cause = this)
    is IOException -> AppError.Network.Unknown(cause = this)

    // HTTP errors
    is HttpException -> AppError.Api.fromHttpCode(code(), this)

    // Database errors (Room, SQLite)
    is SQLException -> AppError.Database.Unknown(cause = this)

    // Already AppError
    is AppError -> this

    // Unknown
    else -> AppError.Unknown(cause = this)
}

 */
