package com.hanhyo.commitlog.presentation.designsystem.components.chip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hanhyo.commitlog.domain.model.AIMood
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmotionChip(
    mood: AIMood,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val containerColor = if (selected) {
        CommitLogTheme.colors.primary
    } else {
        CommitLogTheme.colors.surfaceVariant
    }

    val contentColor = if (selected) {
        CommitLogTheme.colors.surface
    } else {
        CommitLogTheme.colors.textPrimary
    }

    Surface(
        onClick = onClick ?: {},
        modifier = modifier.height(Dimensions.EmotionChipHeight),
        enabled = onClick != null,
        shape = RoundedCornerShape(16.dp),
        color = containerColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = mood.emoji,
                fontSize = Dimensions.EmotionChipIconSize.value.sp
            )

            Text(
                text = mood.displayNameKo,
                style = CommitLogTheme.typography.titleSmall,
                color = contentColor
            )
        }
    }
}

@Preview
@Composable
private fun EmotionChipPreview() {
    CommitLogTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AIMood.entries.take(4).forEach { mood ->
                EmotionChip(
                    mood = mood,
                    selected = mood == AIMood.FOCUSED
                )
            }
        }
    }
}
