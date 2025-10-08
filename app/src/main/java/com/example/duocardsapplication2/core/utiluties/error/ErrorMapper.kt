package com.example.duocardsapplication2.core.utiluties.error

// ============================================
// data/mapper/ErrorMapper.kt
// ============================================

import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException
import javax.net.ssl.SSLHandshakeException
import retrofit2.HttpException
import com.google.gson.JsonSyntaxException
import kotlinx.serialization.SerializationException
import java.util.concurrent.TimeoutException

/**
 * Maps Throwable to AppError
 * Handles all common exception types in Android apps
 */
fun Throwable.toAppError(): AppError {
    return when (this) {
        // Network errors
        is IOException -> mapIOException(this)

        // HTTP errors
        is HttpException -> mapHttpException(this)

        // Parsing errors
        is JsonSyntaxException,
        is SerializationException -> mapParsingError(this)

        // Already an AppError (from domain layer)
        is AppError -> this

        // Timeout errors (Coroutines)
        is TimeoutException -> AppError.Network(
            message = UiText.StringResource(R.string.error_timeout),
            cause = this,
            type = AppError.Network.NetworkType.TIMEOUT
        )

        // Unknown
        else -> mapUnknownError(this)
    }
}

// ============================================
// IO Exception Mapping
// ============================================

private fun mapIOException(exception: IOException): AppError.Network {
    return when (exception) {
        // No internet connection
        is UnknownHostException -> AppError.Network(
            message = UiText.StringResource(R.string.error_no_connection),
            cause = exception,
            type = AppError.Network.NetworkType.NO_CONNECTION
        )

        // Request timeout
        is SocketTimeoutException -> AppError.Network(
            message = UiText.StringResource(R.string.error_timeout),
            cause = exception,
            type = AppError.Network.NetworkType.TIMEOUT
        )

        // SSL/TLS errors
        is SSLException,
        is SSLHandshakeException -> AppError.Network(
            message = UiText.StringResource(R.string.error_ssl),
            cause = exception,
            type = AppError.Network.NetworkType.SSL
        )

        // Connection lost/reset
        is SocketException -> {
            if (exception.message?.contains("reset", ignoreCase = true) == true) {
                AppError.Network(
                    message = UiText.StringResource(R.string.error_connection_reset),
                    cause = exception,
                    type = AppError.Network.NetworkType.GENERIC
                )
            } else {
                AppError.Network(
                    message = UiText.StringResource(R.string.error_network),
                    cause = exception,
                    type = AppError.Network.NetworkType.GENERIC
                )
            }
        }

        // Generic network error
        else -> AppError.Network(
            message = UiText.StringResource(R.string.error_network),
            cause = exception,
            type = AppError.Network.NetworkType.GENERIC
        )
    }
}

// ============================================
// HTTP Exception Mapping
// ============================================

private fun mapHttpException(exception: HttpException): AppError {
    val errorBody = exception.response()?.errorBody()?.string()

    return when (exception.code()) {
        // 400 - Bad Request
        400 -> parseServerError(errorBody, exception) ?: AppError.Unknown(
            message = UiText.StringResource(R.string.error_bad_request),
            cause = exception
        )

        // 401 - Unauthorized
        401 -> AppError.Auth(
            message = UiText.StringResource(R.string.error_unauthorized),
            cause = exception,
            type = AppError.Auth.AuthType.UNAUTHORIZED
        )

        // 403 - Forbidden
        403 -> AppError.Auth(
            message = UiText.StringResource(R.string.error_forbidden),
            cause = exception,
            type = AppError.Auth.AuthType.FORBIDDEN
        )

        // 404 - Not Found
        404 -> AppError.Unknown(
            message = UiText.StringResource(R.string.error_not_found),
            cause = exception
        )

        // 408 - Request Timeout
        408 -> AppError.Network(
            message = UiText.StringResource(R.string.error_timeout),
            cause = exception,
            type = AppError.Network.NetworkType.TIMEOUT
        )

        // 422 - Unprocessable Entity (Validation error)
        422 -> parseValidationError(errorBody, exception)

        // 429 - Too Many Requests
        429 -> AppError.Unknown(
            message = UiText.StringResource(R.string.error_rate_limit),
            cause = exception
        )

        // 500 - Internal Server Error
        500 -> AppError.Server(
            message = UiText.StringResource(R.string.error_server_internal),
            cause = exception,
            type = AppError.Server.ServerType.INTERNAL_ERROR
        )

        // 502 - Bad Gateway
        502 -> AppError.Server(
            message = UiText.StringResource(R.string.error_server_gateway),
            cause = exception,
            type = AppError.Server.ServerType.BAD_GATEWAY
        )

        // 503 - Service Unavailable
        503 -> AppError.Server(
            message = UiText.StringResource(R.string.error_server_unavailable),
            cause = exception,
            type = AppError.Server.ServerType.SERVICE_UNAVAILABLE
        )

        // 504 - Gateway Timeout
        504 -> AppError.Server(
            message = UiText.StringResource(R.string.error_server_timeout),
            cause = exception,
            type = AppError.Server.ServerType.GATEWAY_TIMEOUT
        )

        // Other 4xx client errors
        in 400..499 -> parseServerError(errorBody, exception) ?: AppError.Unknown(
            message = UiText.DynamicString("Client error: ${exception.code()}"),
            cause = exception
        )

        // Other 5xx server errors
        in 500..599 -> AppError.Server(
            message = UiText.StringResource(R.string.error_server),
            cause = exception,
            type = AppError.Server.ServerType.GENERIC
        )

        // Unknown HTTP error
        else -> AppError.Unknown(
            message = UiText.DynamicString("HTTP error: ${exception.code()}"),
            cause = exception
        )
    }
}

// ============================================
// Parse Server Error Response
// ============================================

/**
 * Parse error from server response body
 * Expected JSON format: {"message": "Error message"}
 */
private fun parseServerError(errorBody: String?, exception: HttpException): AppError? {
    if (errorBody.isNullOrBlank()) return null

    return try {
        // Parse JSON error response
        val json = JSONObject(errorBody)
        val message = json.optString("message")
            ?: json.optString("error")
            ?: json.optString("detail")
            ?: return null

        AppError.Unknown(
            message = UiText.DynamicString(message),
            cause = exception
        )
    } catch (e: Exception) {
        null
    }
}

/**
 * Parse validation error from server
 * Expected format: {"errors": {"field": ["error1", "error2"]}}
 */
private fun parseValidationError(errorBody: String?, exception: HttpException): AppError {
    if (errorBody.isNullOrBlank()) {
        return AppError.Unknown(
            message = UiText.StringResource(R.string.error_validation),
            cause = exception
        )
    }

    return try {
        val json = JSONObject(errorBody)
        val errors = json.optJSONObject("errors")

        if (errors != null && errors.length() > 0) {
            val firstField = errors.keys().next()
            val firstError = errors.getJSONArray(firstField).getString(0)

            AppError.Unknown(
                message = UiText.DynamicString("$firstField: $firstError"),
                cause = exception
            )
        } else {
            AppError.Unknown(
                message = UiText.StringResource(R.string.error_validation),
                cause = exception
            )
        }
    } catch (e: Exception) {
        AppError.Unknown(
            message = UiText.StringResource(R.string.error_validation),
            cause = exception
        )
    }
}

// ============================================
// Parsing Error Mapping
// ============================================

private fun mapParsingError(exception: Throwable): AppError {
    return AppError.Unknown(
        message = UiText.StringResource(R.string.error_parsing),
        cause = exception
    )
}

// ============================================
// Unknown Error Mapping
// ============================================

private fun mapUnknownError(exception: Throwable): AppError {
    // Log to Crashlytics
    FirebaseCrashlytics.getInstance().recordException(exception)

    return AppError.Unknown(
        message = exception.message?.let {
            UiText.DynamicString(it)
        } ?: UiText.StringResource(R.string.error_unknown),
        cause = exception
    )
}

// ============================================
// Domain-Specific Error Extensions
// ============================================

/**
 * Map domain-specific exceptions to AppError
 */
fun Throwable.toAppErrorWithContext(context: String): AppError {
    val baseError = this.toAppError()

    // Add context to error message
    return when (baseError) {
        is AppError.Network -> baseError.copy(
            message = UiText.DynamicString("$context: ${baseError.message}")
        )
        is AppError.Server -> baseError.copy(
            message = UiText.DynamicString("$context: ${baseError.message}")
        )
        is AppError.Auth -> baseError.copy(
            message = UiText.DynamicString("$context: ${baseError.message}")
        )
        is AppError.Unknown -> baseError.copy(
            message = UiText.DynamicString("$context: ${baseError.message}")
        )
    }
}

/**
 * Check if error is retryable
 */
fun AppError.isRetryable(): Boolean = when (this) {
    is AppError.Network -> type != AppError.Network.NetworkType.SSL
    is AppError.Server -> type in listOf(
        AppError.Server.ServerType.SERVICE_UNAVAILABLE,
        AppError.Server.ServerType.GATEWAY_TIMEOUT
    )
    is AppError.Auth -> false
    is AppError.Unknown -> false
}

/**
 * Get retry delay in milliseconds
 */
fun AppError.getRetryDelay(): Long = when (this) {
    is AppError.Network -> when (type) {
        AppError.Network.NetworkType.TIMEOUT -> 5000L
        AppError.Network.NetworkType.NO_CONNECTION -> 3000L
        else -> 2000L
    }
    is AppError.Server -> when (type) {
        AppError.Server.ServerType.SERVICE_UNAVAILABLE -> 10000L
        AppError.Server.ServerType.GATEWAY_TIMEOUT -> 5000L
        else -> 3000L
    }
    else -> 0L
}

// ============================================
// Retrofit Error Body Parser (Optional)
// ============================================

/**
 * Extension for Retrofit error body parsing
 */
fun HttpException.getErrorMessage(): String? {
    return try {
        response()?.errorBody()?.string()?.let { body ->
            JSONObject(body).optString("message")
                ?: JSONObject(body).optString("error")
                ?: JSONObject(body).optString("detail")
        }
    } catch (e: Exception) {
        null
    }
}

// ============================================
// Custom Exception Types (Optional)
// ============================================

/**
 * Domain-specific exceptions that map to AppError
 */
class UserNotFoundException(message: String) : Exception(message)
class InvalidTokenException(message: String) : Exception(message)
class NetworkUnavailableException(message: String) : Exception(message)

/**
 * Map custom exceptions
 */
fun Throwable.toAppErrorCustom(): AppError = when (this) {
    is UserNotFoundException -> AppError.Unknown(
        message = UiText.StringResource(R.string.error_user_not_found),
        cause = this
    )
    is InvalidTokenException -> AppError.Auth(
        message = UiText.StringResource(R.string.error_token_invalid),
        cause = this,
        type = AppError.Auth.AuthType.TOKEN_EXPIRED
    )
    is NetworkUnavailableException -> AppError.Network(
        message = UiText.StringResource(R.string.error_no_connection),
        cause = this,
        type = AppError.Network.NetworkType.NO_CONNECTION
    )
    else -> this.toAppError()
}

// ============================================
// res/values/strings.xml
// ============================================

/*
<resources>
    <!-- Network Errors -->
    <string name="error_network">Network error occurred</string>
    <string name="error_no_connection">No internet connection</string>
    <string name="error_timeout">Request timeout. Please try again</string>
    <string name="error_ssl">Secure connection failed</string>
    <string name="error_connection_reset">Connection was reset</string>

    <!-- HTTP Errors -->
    <string name="error_bad_request">Invalid request</string>
    <string name="error_not_found">Resource not found</string>
    <string name="error_rate_limit">Too many requests. Please try again later</string>

    <!-- Server Errors -->
    <string name="error_server">Server error occurred</string>
    <string name="error_server_internal">Internal server error</string>
    <string name="error_server_gateway">Bad gateway</string>
    <string name="error_server_unavailable">Service temporarily unavailable</string>
    <string name="error_server_timeout">Server timeout</string>

    <!-- Auth Errors -->
    <string name="error_auth">Authentication failed</string>
    <string name="error_unauthorized">Please login again</string>
    <string name="error_forbidden">Access denied</string>
    <string name="error_token_invalid">Session expired</string>

    <!-- Validation Errors -->
    <string name="error_validation">Validation failed</string>

    <!-- Parsing Errors -->
    <string name="error_parsing">Data format error</string>

    <!-- Unknown Errors -->
    <string name="error_unknown">Something went wrong</string>
    <string name="error_user_not_found">User not found</string>
</resources>
*/

// ============================================
// Testing
// ============================================

class ErrorMapperTest {

    @Test
    fun `unknown host exception maps to no connection error`() {
        val exception = UnknownHostException("api.example.com")
        val error = exception.toAppError()

        assertThat(error).isInstanceOf<AppError.Network>()
        assertThat((error as AppError.Network).type)
            .isEqualTo(AppError.Network.NetworkType.NO_CONNECTION)
    }

    @Test
    fun `socket timeout maps to timeout error`() {
        val exception = SocketTimeoutException("timeout")
        val error = exception.toAppError()

        assertThat(error).isInstanceOf<AppError.Network>()
        assertThat((error as AppError.Network).type)
            .isEqualTo(AppError.Network.NetworkType.TIMEOUT)
    }

    @Test
    fun `http 401 maps to auth error`() {
        val response = Response.error<Any>(
            401,
            "Unauthorized".toResponseBody()
        )
        val exception = HttpException(response)
        val error = exception.toAppError()

        assertThat(error).isInstanceOf<AppError.Auth>()
        assertThat((error as AppError.Auth).type)
            .isEqualTo(AppError.Auth.AuthType.UNAUTHORIZED)
    }

    @Test
    fun `http 503 maps to service unavailable`() {
        val response = Response.error<Any>(
            503,
            "Service Unavailable".toResponseBody()
        )
        val exception = HttpException(response)
        val error = exception.toAppError()

        assertThat(error).isInstanceOf<AppError.Server>()
        assertThat((error as AppError.Server).type)
            .isEqualTo(AppError.Server.ServerType.SERVICE_UNAVAILABLE)
    }

    @Test
    fun `server error with message in body is parsed`() {
        val errorBody = """{"message": "Custom error message"}"""
        val response = Response.error<Any>(
            400,
            errorBody.toResponseBody("application/json".toMediaType())
        )
        val exception = HttpException(response)
        val error = exception.toAppError()

        assertThat(error.message).isInstanceOf<UiText.DynamicString>()
        assertThat((error.message as UiText.DynamicString).value)
            .isEqualTo("Custom error message")
    }

    @Test
    fun `network error is retryable`() {
        val error = AppError.Network(
            message = UiText.DynamicString("test"),
            type = AppError.Network.NetworkType.TIMEOUT
        )

        assertThat(error.isRetryable()).isTrue()
    }

    @Test
    fun `auth error is not retryable`() {
        val error = AppError.Auth(
            message = UiText.DynamicString("test"),
            type = AppError.Auth.AuthType.UNAUTHORIZED
        )

        assertThat(error.isRetryable()).isFalse()
    }
}