package com.hanhyo.commitlog.domain.common

sealed class Result<out T> {

    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val error: DomainError) : Result<Nothing>()
    object Loading : Result<Nothing>()

    fun isSuccess(): Boolean = this is Success
    fun isError(): Boolean = this is Error
    fun getOrNull(): T? = if (this is Success) data else null

    companion object {
        fun <T> success(data: T): Result<T> = Success(data)
        fun error(error: DomainError): Result<Nothing> = Error(error)
        fun error(message: String): Result<Nothing> = Error(DomainError.Unknown(message))
        fun loading(): Result<Nothing> = Loading
    }
}


