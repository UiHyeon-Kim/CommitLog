package com.hanhyo.commitlog.presentation.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hanhyo.commitlog.domain.model.Commit
import com.hanhyo.commitlog.domain.model.CommitId
import com.hanhyo.commitlog.domain.model.CommitTitle
import com.hanhyo.commitlog.domain.model.LearnedContent
import com.hanhyo.commitlog.domain.model.LearningTag
import com.hanhyo.commitlog.presentation.designsystem.components.card.CommitCard
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions
import java.time.LocalDate

@Composable
fun SwipeToDeleteCard(
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState()

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            onDelete()
            dismissState.snapTo(SwipeToDismissBoxValue.Settled)
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            SwipeDeleteBackground(dismissState)
        },
        content = { content() }
    )
}

@Composable
private fun SwipeDeleteBackground(
    dismissState: SwipeToDismissBoxState
) {
    val color = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
        MaterialTheme.colorScheme.errorContainer
    } else {
        Color.Transparent
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color,
                RoundedCornerShape(Dimensions.CardRadius)
            )
            .padding(end = 16.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "삭제",
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Preview(showBackground = true, name = "SwipeToDeleteCard")
@Composable
private fun SwipeToDeleteCardPreview() {
    CommitLogTheme {
        val sampleCommit = Commit(
            id = CommitId(1L),
            date = LocalDate.now(),
            title = CommitTitle("Swipe To Delete"),
            learnedToday = LearnedContent("This is a preview of the swipe to delete functionality."),
            tags = setOf(LearningTag("preview")),
            createdAt = System.currentTimeMillis(),
            isDraft = false,
            difficulties = null,
            tomorrowPlan = null,
            analysis = null,
            analysisStatus = com.hanhyo.commitlog.domain.model.AnalysisStatus.NONE,
            updatedAt = null
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Swipe left on the card to see the delete action.")
            SwipeToDeleteCard(
                onDelete = {}
            ) {
                CommitCard(
                    commit = sampleCommit,
                    onClick = {}
                )
            }
        }
    }
}
