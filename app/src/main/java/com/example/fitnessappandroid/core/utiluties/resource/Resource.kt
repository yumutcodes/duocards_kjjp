package com.example.fitnessappandroid.core.utiluties.result

import com.example.fitnessappandroid.core.utiluties.error.AppError

sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val error: AppError, val data: T? = null) : Resource<T>()
    data object Loading : Resource<Nothing>()

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isLoading: Boolean get() = this is Loading

    val dataOrNull: T? get() = when (this) {
        is Success -> data
        is Error -> data
        is Loading -> null
    }

    val errorOrNull: AppError? get() = when (this) {
        is Error -> error
        else -> null
    }

    inline fun <R> map(transform: (T) -> R): Resource<R> {
        return when (this) {
            is Success -> Success(transform(data))
            is Error -> Error(error, data?.let(transform))
            is Loading -> Loading
        }
    }

    inline fun onSuccess(action: (T) -> Unit): Resource<T> {
        if (this is Success) action(data)
        return this
    }

    inline fun onError(action: (AppError, T?) -> Unit): Resource<T> {
        if (this is Error) action(error, data)
        return this
    }

    inline fun onLoading(action: () -> Unit): Resource<T> {
        if (this is Loading) action()
        return this
    }
}