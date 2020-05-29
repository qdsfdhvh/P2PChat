package com.seiko.wechat.core.data

sealed class Result<out T> {
    data class Success<out T>(val data: T): Result<T>()
    data class Error(val error: Throwable?): Result<Nothing>()
}