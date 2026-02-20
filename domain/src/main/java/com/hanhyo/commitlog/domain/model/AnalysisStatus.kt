package com.hanhyo.commitlog.domain.model

enum class AnalysisStatus {
    NONE,       // 분석 전
    PENDING,    // 분석 중
    COMPLETED,  // 분석 완료
    FAILED      // 분석 실패
}
