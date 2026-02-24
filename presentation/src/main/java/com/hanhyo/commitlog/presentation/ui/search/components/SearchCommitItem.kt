package com.hanhyo.commitlog.presentation.ui.search.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hanhyo.commitlog.domain.model.AnalysisStatus
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.model.CommitId
import com.hanhyo.commitlog.domain.model.CommitTitle
import com.hanhyo.commitlog.domain.model.LearnedContent
import com.hanhyo.commitlog.domain.model.LearningTag
import com.hanhyo.commitlog.presentation.common.extension.toKoreanFormat
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions
import java.time.LocalDate

@Composable
fun SearchCommitItem(
    commit: Commit,
    onClick: () -> Unit,
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = CommitLogTheme.colors.surface,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.SpacingMedium),
            verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingSmall)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(
                    text = commit.date.toKoreanFormat(),
                    style = CommitLogTheme.typography.labelSmall,
                    color = CommitLogTheme.colors.textTertiary
                )
                
                // 태그 표시
                if (commit.tags.isNotEmpty()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        commit.tags.take(2).forEach { tag ->
                            Text(
                                text = "#${tag.value}",
                                style = CommitLogTheme.typography.labelSmall,
                                color = CommitLogTheme.colors.primary
                            )
                        }
                    }
                }
            }

            Text(
                text = commit.title.value,
                style = CommitLogTheme.typography.titleMedium,
                color = CommitLogTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = commit.learnedToday.value,
                style = CommitLogTheme.typography.bodyMedium,
                color = CommitLogTheme.colors.textSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true, name = "SearchCommitItem - With Tags")
@Composable
private fun SearchCommitItemWithTagsPreview() {
    val sampleCommitWithTags = Commit(
        id = CommitId(1),
        date = LocalDate.now(),
        title = CommitTitle("Jetpack Compose Deep Dive"),
        learnedToday = LearnedContent("State management with ViewModel."),
        tags = setOf(LearningTag("compose"), LearningTag("android")),
        difficulties = null,
        tomorrowPlan = null,
        analysis = null,
        analysisStatus = AnalysisStatus.NONE,
        isDraft = false,
        createdAt = 0L,
        updatedAt = null
    )
    
    CommitLogTheme {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(8.dp)) {
            SearchCommitItem(commit = sampleCommitWithTags, onClick = {})
        }
    }
}

@Preview(showBackground = true, name = "SearchCommitItem - Without Tags")
@Composable
private fun SearchCommitItemWithoutTagsPreview() {
    val sampleCommitWithoutTags = Commit(
        id = CommitId(2),
        date = LocalDate.now().minusDays(1),
        title = CommitTitle("Algorithm Problem Solving"),
        learnedToday = LearnedContent("Solved a BFS problem."),
        tags = emptySet(),
        difficulties = null,
        tomorrowPlan = null,
        analysis = null,
        analysisStatus = AnalysisStatus.NONE,
        isDraft = false,
        createdAt = 0L,
        updatedAt = null
    )

    CommitLogTheme {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(8.dp)) {
            SearchCommitItem(commit = sampleCommitWithoutTags, onClick = {})
        }
    }
}
