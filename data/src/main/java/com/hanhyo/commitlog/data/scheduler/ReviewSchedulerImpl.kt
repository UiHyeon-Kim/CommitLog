package com.hanhyo.commitlog.data.scheduler

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.hanhyo.commitlog.data.worker.GenerateMonthlyReviewWorker
import com.hanhyo.commitlog.data.worker.WorkerConstants
import com.hanhyo.commitlog.domain.scheduler.ReviewScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val workManager: WorkManager
) : ReviewScheduler {

    private val prefs = context.getSharedPreferences("review_scheduler_prefs", Context.MODE_PRIVATE)

    override fun scheduleRegularMonthlyReview() {
        val now = ZonedDateTime.now(ZoneId.systemDefault())
        
        // 다음 실행 시간 계산: 다음 달 1일 오전 9시
        var nextRun = now.withDayOfMonth(1).withHour(9).withMinute(0).withSecond(0).withNano(0)
        
        if (now.isAfter(nextRun) || now.isEqual(nextRun)) {
            nextRun = nextRun.plusMonths(1)
        }

        // 영속화: 앱이 재시작되거나 문제가 생겼을 때 확인용
        prefs.edit().putLong(PREF_KEY_NEXT_RUN, nextRun.toInstant().toEpochMilli()).apply()

        val delay = Duration.between(now, nextRun).toMillis()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<GenerateMonthlyReviewWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .addTag(WorkerConstants.TAG_MONTHLY_REVIEW_REGULAR)
            .build()

        workManager.enqueueUniqueWork(
            WorkerConstants.WORK_NAME_MONTHLY_REVIEW_REGULAR,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )

        Timber.d("Scheduled regular monthly review. Next run: $nextRun (delay: ${delay}ms)")
    }

    companion object {
        private const val PREF_KEY_NEXT_RUN = "next_scheduled_run_time"
    }
}
