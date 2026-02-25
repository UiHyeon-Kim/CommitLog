package com.hanhyo.commitlog.presentation.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.hanhyo.commitlog.domain.usecase.commit.GetStreakUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import androidx.core.net.toUri

/**
 * Glance 위젯에서 Hilt 의존성 주입을 사용하기 위한 EntryPoint 인터페이스.
 * Glance AppWidget은 직접적인 Hilt 주입을 지원하지 않으므로 이 방식을 사용합니다.
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    /** 스트릭 정보를 가져오는 UseCase */
    fun getStreakUseCase(): GetStreakUseCase
}

/**
 * 사용자의 연속 스트릭을 표시하는 Glance 기반 앱 위젯.
 */
class StreakWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context,
            WidgetEntryPoint::class.java
        )
        val getStreakUseCase = entryPoint.getStreakUseCase()
        val streakResult = getStreakUseCase()
        val streakData = streakResult.getOrNull()

        val currentStreak = streakData?.currentStreak ?: 0

        provideContent {
            GlanceTheme {
                StreakWidgetContent(currentStreak = currentStreak)
            }
        }
    }
}

/**
 * 위젯의 실제 UI를 구성하는 Composable.
 *
 * @param currentStreak 현재 사용자의 연속 스트릭 일수
 */
@Composable
fun StreakWidgetContent(currentStreak: Int) {
    Row(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.surface)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 스트릭 아이콘과 일수 표시
        Text(
            text = "${currentStreak}일째",
            style = TextStyle(
                color = GlanceTheme.colors.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = GlanceModifier.padding(end = 8.dp)
        )

        Spacer(modifier = GlanceModifier.defaultWeight())

        // "기록하기" 버튼: 클릭 시 앱의 작성 화면(WriteRoute)으로 이동
        Button(
            text = "기록하기",
            onClick = composeAction()
        )
    }
}

/**
 * "기록하기" 버튼 클릭 시 실행될 액션을 정의합니다.
 * MainActivity의 딥링크(app://commitlog/write)를 통해 특정 화면으로 이동합니다.
 */
private fun composeAction(): androidx.glance.action.Action {
    return actionStartActivity(
        Intent(Intent.ACTION_VIEW, "app://commitlog/write".toUri()).apply {
            setPackage("com.hanhyo.commitlog")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
    )
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 190, heightDp = 90)
@Composable
fun StreakWidgetContentPreview() {
    StreakWidgetContent(currentStreak = 1)
}
