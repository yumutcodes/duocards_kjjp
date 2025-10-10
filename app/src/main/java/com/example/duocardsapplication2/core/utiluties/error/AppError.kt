package com.example.duocardsapplication2.core.utiluties.error

import com.example.duocardsapplication2.core.utiluties.ui.UiText
import com.example.duocardsapplication2.R

sealed class AppError(
    val uiText: UiText,
    val title: UiText? = null,
    val isRetryable: Boolean = false,
    val cause: Throwable? = null
) {
    // Network Errors
    data class NoConnection(
        val throwable: Throwable? = null
    ) : AppError(
        uiText = UiText.StringResource(R.string.error_no_connection),
        title = UiText.StringResource(R.string.error_title_network),
        isRetryable = true,
        cause = throwable
    )

    data class Timeout(
        val throwable: Throwable? = null
    ) : AppError(
        uiText = UiText.StringResource(R.string.error_timeout),
        title = UiText.StringResource(R.string.error_title_network),
        isRetryable = true,
        cause = throwable
    )

    data class ServerError(
        val throwable: Throwable? = null
    ) : AppError(
        uiText = UiText.StringResource(R.string.error_server),
        title = UiText.StringResource(R.string.error_title_server),
        isRetryable = true,
        cause = throwable
    )

    // API Errors
    data class BadRequest(
        val throwable: Throwable? = null
    ) : AppError(
        uiText = UiText.StringResource(R.string.error_bad_request),
        title = UiText.StringResource(R.string.error_title_server),
        cause = throwable
    )

    data class Unauthorized(
        val throwable: Throwable? = null
    ) : AppError(
        uiText = UiText.StringResource(R.string.error_unauthorized),
        title = UiText.StringResource(R.string.error_title_auth),
        cause = throwable
    )

    data class Forbidden(
        val throwable: Throwable? = null
    ) : AppError(
        uiText = UiText.StringResource(R.string.error_forbidden),
        title = UiText.StringResource(R.string.error_title_auth),
        cause = throwable
    )

    data class NotFound(
        val throwable: Throwable? = null
    ) : AppError(
        uiText = UiText.StringResource(R.string.error_not_found),
        title = UiText.StringResource(R.string.error_title_server),
        cause = throwable
    )

    // Auth Errors
    data class InvalidCredentials(
        val throwable: Throwable? = null
    ) : AppError(
        uiText = UiText.StringResource(R.string.error_invalid_credentials),
        title = UiText.StringResource(R.string.error_title_auth),
        cause = throwable
    )

    data class TokenExpired(
        val throwable: Throwable? = null
    ) : AppError(
        uiText = UiText.StringResource(R.string.error_token_expired),
        title = UiText.StringResource(R.string.error_title_auth),
        cause = throwable
    )

    data class AccountLocked(
        val throwable: Throwable? = null
    ) : AppError(
        uiText = UiText.StringResource(R.string.error_account_locked),
        title = UiText.StringResource(R.string.error_title_auth),
        cause = throwable
    )

    // Database Errors
    data class DatabaseReadError(
        val throwable: Throwable? = null
    ) : AppError(
        uiText = UiText.StringResource(R.string.error_db_read),
        title = UiText.StringResource(R.string.error_title_database),
        cause = throwable
    )

    data class DatabaseWriteError(
        val throwable: Throwable? = null
    ) : AppError(
        uiText = UiText.StringResource(R.string.error_db_write),
        title = UiText.StringResource(R.string.error_title_database),
        cause = throwable
    )

    // Validation Errors
    data class FieldRequired(val fieldName: String) : AppError(
        uiText = UiText.StringResource(R.string.error_field_required, fieldName),
        title = UiText.StringResource(R.string.error_title_validation)
    )

    data class InvalidFormat(val fieldName: String, val expectedFormat: String) : AppError(
        uiText = UiText.StringResource(R.string.error_invalid_format, fieldName, expectedFormat),
        title = UiText.StringResource(R.string.error_title_validation)
    )

    // Unknown Error
    data class Unknown(val message: String? = null, val throwable: Throwable? = null) : AppError(
        uiText = message?.let { UiText.DynamicString(it) }
            ?: UiText.StringResource(R.string.error_unknown),
        title = UiText.StringResource(R.string.error_title_unknown),
        cause = throwable
    )
}


