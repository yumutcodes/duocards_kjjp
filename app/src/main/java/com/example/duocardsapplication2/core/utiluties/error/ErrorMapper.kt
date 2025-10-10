package com.example.duocardsapplication2.core.utiluties.error

import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ErrorMapper @Inject constructor() {

    fun mapToAppError(throwable: Throwable): AppError {
        return when (throwable) {
            is HttpException -> mapHttpException(throwable)
            is UnknownHostException -> AppError.NoConnection(throwable)
            is ConnectException -> AppError.NoConnection(throwable)
            is SocketTimeoutException -> AppError.Timeout(throwable)
            else -> AppError.Unknown(message = throwable.message, throwable = throwable)
        }
    }

    fun mapHttpCode(code: Int, cause: Throwable? = null): AppError {
        return when (code) {
            400 -> AppError.BadRequest(cause)
            401 -> AppError.Unauthorized(cause)
            403 -> AppError.Forbidden(cause)
            404 -> AppError.NotFound(cause)
            408 -> AppError.Timeout(cause)
            429 -> AppError.ServerError(cause)
            500, 502, 503, 504 -> AppError.ServerError(cause)
            else -> AppError.Unknown("HTTP $code", cause)
        }
    }

    private fun mapHttpException(httpException: HttpException): AppError {
        return mapHttpCode(httpException.code(), httpException)
    }
}


