package com.hanhyo.commitlog.data.scheduler

import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.hanhyo.commitlog.data.worker.GenerateMonthlyReviewWorker
import com.hanhyo.commitlog.domain.scheduler.ReviewScheduler
import timber.log.Timber
import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewSchedulerImpl @Inject constructor(
    private val workManager: WorkManager
) : ReviewScheduler {

    override fun scheduleRegularMonthlyReview() {
        val now = ZonedDateTime.now(ZoneId.systemDefault())
        
        // 다음 실행 시간 계산: 다음 달 1일 오전 9시
        // 만약 현재가 1일 오전 9시 이전이라면 이번 달 1일 오전 9시로 설정
        var nextRun = now.withDayOfMonth(1).withHour(9).withMinute(0).withSecond(0).withNano(0)
        
        if (now.isAfter(nextRun) || now.isEqual(nextRun)) {
            nextRun = nextRun.plusMonths(1)
        }

        val delay = Duration.between(now, nextRun).toMillis()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<GenerateMonthlyReviewWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .addTag(TAG_MONTHLY_REVIEW_REGULAR)
            .build()

        workManager.enqueueUniqueWork(
            WORK_NAME_MONTHLY_REVIEW_REGULAR,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )

        Timber.d("Scheduled regular monthly review. Next run: $nextRun (delay: ${delay}ms)")
    }

    companion object {
        const val TAG_MONTHLY_REVIEW_REGULAR = "monthly_review_regular"
        const val WORK_NAME_MONTHLY_REVIEW_REGULAR = "MonthlyReviewRegularWork"
    }
}
