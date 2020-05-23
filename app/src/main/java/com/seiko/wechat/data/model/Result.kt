package com.seiko.wechat.data.model

sealed class Result<out T> {
    data class Success<T>(val data: T): Result<T>()
    data class Error(val throwable: Throwable): Result<Nothing>()
}