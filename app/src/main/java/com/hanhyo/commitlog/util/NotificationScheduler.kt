package com.hanhyo.commitlog.util

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.hanhyo.commitlog.data.worker.GenerateMonthlyReviewWorker
import java.time.Duration
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

/**
 * 앱의 알림 및 백그라운드 작업을 스케줄링하는 유틸리티 클래스
 * 
 * [Notification System Overview]
 * 1. 월간 회고(Monthly Review): 매월 1일 오전 9시에 정기적으로 실행됩니다.
 *    - 단순히 알림만 띄우는 것이 아니라, 백그라운드에서 AI 회고 리포트를 먼저 생성합니다.
 *    - 생성이 완료되면 알림을 통해 사용자에게 리포트 도착을 알립니다.
 * 2. 데일리 리마인더(작업 예정): 매일 정해진 시간에 기록 유무를 확인 후 알림을 발송합니다.
 * 3. AI 분석 완료: 기록(Commit) 시 백그라운드 분석이 끝난 후 알림을 발송합니다.
 */
object NotificationScheduler {

    private const val MONTHLY_REVIEW_WORK_NAME = "MonthlyReviewNotificationWork"

    /**
     * 월간 회고 알림 및 생성을 매월 1일 오전 9시에 반복적으로 스케줄링합니다.
     * 
     * - PeriodicWorkRequest를 사용하여 주기적으로 실행되도록 합니다.
     * - 초기 실행 지연 시간(initialDelay)을 계산하여 정확한 시간에 첫 실행되도록 맞춥니다.
     */
    fun scheduleMonthlyReviewNotification(context: Context) {
        val now = ZonedDateTime.now(ZoneId.systemDefault())
        val today = LocalDate.now()
        
        // 실행 대상이 되는 달 (지난 달의 회고를 1일에 생성)
        val lastMonth = today.minusMonths(1)
        
        var nextRun = now.withDayOfMonth(1).withHour(9).withMinute(0).withSecond(0).withNano(0)
        
        if (now.isAfter(nextRun) || now.isEqual(nextRun)) {
            // 이미 이번 달 1일이 지났다면, 다음 달 1일로 설정
            nextRun = nextRun.plusMonths(1)
        }

        val initialDelay = Duration.between(now, nextRun).toMillis()

        // 주기적 실행 시에는 Worker 내부에서 실행 시점 기준으로 대상 달을 계산하므로 InputData를 비웁니다.
        val workRequest = PeriodicWorkRequestBuilder<GenerateMonthlyReviewWorker>(
            30, TimeUnit.DAYS
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            MONTHLY_REVIEW_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }
}
