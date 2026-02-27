package com.hanhyo.commitlog.presentation.ui.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hanhyo.commitlog.domain.model.AnalysisStatus
import com.hanhyo.commitlog.presentation.R
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions

@Composable
fun EmptyAnalysisCard(
    status: AnalysisStatus,
    onRegenerate: () -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = CommitLogTheme.colors.surfaceVariant.copy(alpha = 0.3f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.SpacingLarge),
            verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = when (status) {
                    AnalysisStatus.PENDING -> stringResource(R.string.detail_analysis_pending)
                    AnalysisStatus.FAILED -> stringResource(R.string.detail_analysis_failed)
                    else -> stringResource(R.string.detail_analysis_none)
                },
                style = CommitLogTheme.typography.bodyMedium,
                color = CommitLogTheme.colors.textSecondary
            )

            if (status != AnalysisStatus.PENDING) {
                TextButton(onClick = onRegenerate) {
                    Text(
                        text = stringResource(R.string.detail_button_regenerate),
                        color = CommitLogTheme.colors.primary
                    )
                }
            } else {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = CommitLogTheme.colors.primary,
                    strokeWidth = 2.dp
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "EmptyAnalysisCard - Pending")
@Composable
private fun EmptyAnalysisCardPendingPreview() {
    CommitLogTheme {
        EmptyAnalysisCard(
            status = AnalysisStatus.PENDING,
            onRegenerate = {}
        )
    }
}

@Preview(showBackground = true, name = "EmptyAnalysisCard - Failed")
@Composable
private fun EmptyAnalysisCardFailedPreview() {
    CommitLogTheme {
        EmptyAnalysisCard(
            status = AnalysisStatus.FAILED,
            onRegenerate = {}
        )
    }
}

@Preview(showBackground = true, name = "EmptyAnalysisCard - None")
@Composable
private fun EmptyAnalysisCardNonePreview() {
    CommitLogTheme {
        EmptyAnalysisCard(
            status = AnalysisStatus.NONE,
            onRegenerate = {}
        )
    }
}
