package com.hanhyo.commitlog.data.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.hanhyo.commitlog.domain.scheduler.ReviewScheduler
import com.hanhyo.commitlog.domain.usecase.aianalysis.GenerateMonthlyReviewUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import timber.log.Timber
import java.time.LocalDate

/**
 * 월간 회고 생성 및 알림을 담당하는 백그라운드 워커
 *
 * [Notification System Overview - Monthly Review]
 * 1. 발생 시점: 매월 1일 오전 9시 (NotificationScheduler에 의해 스케줄링됨)
 * 2. 동작:
 *    - 사용자가 직접 앱에서 '생성'을 누르지 않아도 백그라운드에서 지난 달의 데이터를 분석합니다.
 *    - AI 분석이 완료되면 DB에 저장하고, 시스템 알림(Status Bar)을 통해 완료를 알립니다.
 *    - 사용자가 알림을 클릭하면 앱의 '회고' 화면으로 딥링크 이동합니다.
 */
@HiltWorker
class GenerateMonthlyReviewWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val generateMonthlyReviewUseCase: GenerateMonthlyReviewUseCase,
    private val reviewScheduler: ReviewScheduler
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val today = LocalDate.now()
        val year = inputData.getInt(WorkerConstants.KEY_YEAR, today.minusMonths(1).year)
        val month = inputData.getInt(WorkerConstants.KEY_MONTH, today.minusMonths(1).monthValue)

        Timber.d("Starting Monthly Review Generation for $year-$month")

        return try {
            val result = generateMonthlyReviewUseCase(year, month)

            // 정기 스케줄링 태그가 포함되어 있다면 다음 달 작업을 위해 재귀적으로 예약
            if (tags.contains(WorkerConstants.TAG_MONTHLY_REVIEW_REGULAR)) {
                reviewScheduler.scheduleRegularMonthlyReview()
            }

            if (result.isSuccess) {
                showNotification(year, month)
                Result.success()
            } else {
                val exception = result.exceptionOrNull()
                Timber.e(exception, "Failed to generate monthly review")
                Result.failure(
                    Data.Builder()
                        .putString(WorkerConstants.KEY_ERROR_MESSAGE, exception?.message ?: "Unknown error")
                        .build()
                )
            }
        } catch (e: CancellationException) {
            Timber.i("GenerateMonthlyReviewWorker cancelled")
            throw e
        } catch (e: Exception) {
            Timber.e(e, "Unexpected error in GenerateMonthlyReviewWorker")
            Result.failure(
                Data.Builder()
                    .putString(WorkerConstants.KEY_ERROR_MESSAGE, e.message ?: "Unexpected error")
                    .build()
            )
        }
    }

    private fun showNotification(year: Int, month: Int) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "monthly_review_channel"
        val channel = NotificationChannel(
            channelId,
            "월간 회고 알림",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "월간 회고 생성 완료 및 리포트 도착 알림"
        }
        notificationManager.createNotificationChannel(channel)

        // 클릭 시 ReviewScreen으로 이동하는 Deep Link 설정
        val intent = Intent(Intent.ACTION_VIEW, "app://commitlog/review".toUri()).apply {
            // 패키지 명을 명시하여 외부 앱에서 낚아채지 못하게 함
            setPackage(context.packageName)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            1001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val iconResId = context.resources.getIdentifier(
            "ic_notification",
            "drawable",
            context.packageName
        )
        val finalIcon = if (iconResId != 0) iconResId else android.R.drawable.ic_dialog_info

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(finalIcon)
            .setContentTitle("${year}년 ${month}월 회고 리포트 도착 📊")
            .setContentText("AI가 분석한 한 달간의 성장을 확인해보세요!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val NOTIFICATION_ID = 2001

        /**
         * Worker에 전달할 데이터를 생성합니다.
         */
        fun createInputData(year: Int, month: Int): Data {
            return Data.Builder()
                .putInt(WorkerConstants.KEY_YEAR, year)
                .putInt(WorkerConstants.KEY_MONTH, month)
                .build()
        }
    }
}
