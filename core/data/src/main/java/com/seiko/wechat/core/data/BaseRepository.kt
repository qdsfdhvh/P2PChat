package com.seiko.wechat.core.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.IOException

abstract class BaseRepository {
    suspend fun <T>callApi(
        context: CoroutineDispatcher,
        apiCall: suspend () -> T
    ): Result<T> {
        return withContext(context) {
            try {
                Result.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                Result.Error(throwable)
            }
        }
    }
}