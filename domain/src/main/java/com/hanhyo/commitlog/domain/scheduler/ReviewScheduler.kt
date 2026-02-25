package com.hanhyo.commitlog.domain.scheduler

/**
 * 월간 회고 통계 및 알림을 위한 스케줄러 인터페이스
 */
interface ReviewScheduler {
    /**
     * 정기적인 월간 회고 알림 및 분석 작업을 스케줄링합니다.
     * 매월 1일 오전 9시에 실행되도록 예약합니다.
     */
    fun scheduleRegularMonthlyReview()
}
