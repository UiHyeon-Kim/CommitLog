package com.hanhyo.commitlog.data.scheduler

import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.hanhyo.commitlog.data.worker.GenerateMonthlyReviewWorker
import com.hanhyo.commitlog.data.worker.WorkerConstants
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
        var nextRun = now.withDayOfMonth(1).withHour(9).withMinute(0).withSecond(0).withNano(0)

        if (now.isAfter(nextRun) || now.isEqual(nextRun)) {
            nextRun = nextRun.plusMonths(1)
        }

        val delay = Duration.between(now, nextRun).toMillis()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val targetYear = now.year
        val targetMonth = now.monthValue

        val workRequest = OneTimeWorkRequestBuilder<GenerateMonthlyReviewWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .addTag(WorkerConstants.TAG_MONTHLY_REVIEW_REGULAR)
            .setInputData(GenerateMonthlyReviewWorker.createInputData(targetYear, targetMonth))
            .build()

        workManager.enqueueUniqueWork(
            WorkerConstants.WORK_NAME_MONTHLY_REVIEW_REGULAR,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )

        Timber.d("정기 월간 검토 예정. 다음 실행: $nextRun (delay: ${delay}ms)")
    }
}
