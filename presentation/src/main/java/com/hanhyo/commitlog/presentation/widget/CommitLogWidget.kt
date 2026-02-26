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
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.hanhyo.commitlog.domain.model.AnalysisStatus
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.model.CommitId
import com.hanhyo.commitlog.domain.model.CommitTitle
import com.hanhyo.commitlog.domain.model.LearnedContent
import dagger.hilt.android.EntryPointAccessors
import java.time.LocalDate

class CommitLogWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context,
            WidgetEntryPoint::class.java
        )
        val getTodayCommitUseCase = entryPoint.getTodayCommitUseCase()
        val commit = getTodayCommitUseCase().getOrNull()
        val writeIntent = getWriteIntent(context.packageName)

        provideContent {
            GlanceTheme {
                CommitLogWidgetContent(
                    commit = commit,
                    onWriteClick = actionStartActivity(writeIntent)
                )
            }
        }
    }
}

@Composable
private fun CommitLogWidgetContent(
    commit: Commit?,
    onWriteClick: androidx.glance.action.Action
) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.surface)
            .clickable(onWriteClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (commit != null) {
            // AI 분석 감정 이모지 표시 (없으면 기본값)
            Text(
                text = commit.analysis?.mood?.emoji ?: "📝",
                style = TextStyle(fontSize = 28.sp)
            )
            Spacer(modifier = GlanceModifier.height(8.dp))
            Text(
                text = commit.title.value,
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                maxLines = 2,
            )
            Spacer(modifier = GlanceModifier.height(4.dp))
            Text(
                text = "오늘의 커밋",
                style = TextStyle(
                    fontSize = 12.sp,
                    color = GlanceTheme.colors.onSurfaceVariant
                )
            )
        } else {
            // 커밋이 없는 경우
            Text(text = "🔥", style = TextStyle(fontSize = 32.sp))
            Spacer(modifier = GlanceModifier.height(8.dp))
            Text(
                text = "오늘의 한 걸음을 기록하세요",
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

private fun getWriteIntent(packageName: String): Intent {
    return Intent(Intent.ACTION_VIEW, "app://commitlog/write".toUri()).apply {
        setPackage(packageName)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 190, heightDp = 90)
@Composable
private fun CommitLogWidgetContentPreview() {
    val commit = Commit(
        id = CommitId(1L),
        date = LocalDate.now(),
        title = CommitTitle("오늘의 커밋"),
        learnedToday = LearnedContent("많은 것을 배웠습니다."),
        tags = emptySet(),
        analysisStatus = AnalysisStatus.COMPLETED,
        isDraft = false,
        createdAt = System.currentTimeMillis(),
        difficulties = null,
        tomorrowPlan = null,
        analysis = null,
        updatedAt = null,
    )
    CommitLogWidgetContent(
        commit = commit,
        onWriteClick = actionStartActivity(getWriteIntent("com.hanhyo.commitlog"))
    )
}
