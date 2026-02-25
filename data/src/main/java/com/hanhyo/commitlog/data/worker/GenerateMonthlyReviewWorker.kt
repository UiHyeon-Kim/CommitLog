package com.hanhyo.commitlog.data.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.hanhyo.commitlog.domain.usecase.aianalysis.GenerateMonthlyReviewUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
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
    private val generateMonthlyReviewUseCase: GenerateMonthlyReviewUseCase
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val today = LocalDate.now()
        // InputData로 전달된 정보가 있으면 사용하고, 없으면 실행 시점 기준 지난 달을 기본값으로 사용합니다.
        // 이는 주기적(Periodic) 실행 시 매번 적절한 달을 계산하기 위함입니다.
        val year = inputData.getInt(KEY_YEAR, today.minusMonths(1).year)
        val month = inputData.getInt(KEY_MONTH, today.minusMonths(1).monthValue)

        Timber.d("Starting Monthly Review Generation for $year-$month")

        return try {
            val result = generateMonthlyReviewUseCase(year, month)
            
            if (result.isSuccess) {
                showNotification(year, month)
                Result.success()
            } else {
                Timber.e(result.exceptionOrNull(), "Failed to generate monthly review")
                Result.failure()
            }
        } catch (e: Exception) {
            Timber.e(e, "Unexpected error in GenerateMonthlyReviewWorker")
            Result.failure()
        }
    }

    private fun showNotification(year: Int, month: Int) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "monthly_review_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "월간 회고 알림",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "월간 회고 생성 완료 및 리포트 도착 알림"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // 클릭 시 ReviewScreen으로 이동하는 Deep Link 설정
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("app://commitlog/review")).apply {
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

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.sym_def_app_icon) // TODO: 앱 전용 아이콘으로 교체 필요
            .setContentTitle("${year}년 ${month}월 회고 리포트 도착 \uD83D\uDCCA")
            .setContentText("AI가 분석한 한 달간의 성장을 확인해보세요!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val KEY_YEAR = "year"
        const val KEY_MONTH = "month"
        private const val NOTIFICATION_ID = 2001

        /**
         * Worker에 전달할 데이터를 생성합니다.
         */
        fun createInputData(year: Int, month: Int): Data {
            return Data.Builder()
                .putInt(KEY_YEAR, year)
                .putInt(KEY_MONTH, month)
                .build()
        }
    }
}
