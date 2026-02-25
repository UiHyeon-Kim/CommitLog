package com.hanhyo.commitlog.domain.exception

/**
 * AI 분석 관련 도메인 예외
 */
class AiAnalysisException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)
