package com.hanhyo.commitlog.domain.common

sealed class DomainError {
    abstract val message: String
    open val cause: Throwable? = null

    /** 유효성 검증 에러 (잘못된 입력, 비즈니스 규칙 위반 등) */
    data class ValidationError(override val message: String) : DomainError()

    /** 네트워크 에러 (연결 실패, 타임아웃 등) */
    data class NetworkError(
        override val message: String,
        override val cause: Throwable?
    ) : DomainError()

    /** 데이터베이스 에러 (저장/조회 실패 등) */
    data class DatabaseError(
        override val message: String,
        override val cause: Throwable? = null
    ) : DomainError()

    /** API 에러 (HTTP 에러, API 키 문제 등) */
    data class ApiError(
        val code: Int,
        override val message: String
    ) : DomainError()

    /** AI 분석 에러 (분석 실패, 응답 파싱 실패 등) */
    data class AiAnalysisError(
        override val message: String,
        override val cause: Throwable?
    ) : DomainError()

    /** 찾을 수 없음 에러 (존재하지 않는 데이터 조회 시) */
    data class NotFoundError(override val message: String) : DomainError()

    /** 알 수 없는 에러 */
    data class Unknown(override val message: String) : DomainError()
}
