package com.hanhyo.commitlog.domain.common

import kotlin.coroutines.cancellation.CancellationException

/**
 * suspend 함수에서 안전하게 Result를 생성하는 유틸리티
 * [CancellationException]은 rethrow해 코루틴 취소를 보장
 */
inline fun <T> runSuspendCatching(block: () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
}
