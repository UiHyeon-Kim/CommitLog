package com.hanhyo.commitlog.presentation.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import dagger.hilt.android.EntryPointAccessors


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
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.surface)
            .clickable(composeAction())
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🔥",
            style = TextStyle(fontSize = 30.sp)
        )
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
@Preview(widthDp = 90, heightDp = 90)
@Composable
fun StreakWidgetContentPreview() {
    StreakWidgetContent(currentStreak = 1)
}
