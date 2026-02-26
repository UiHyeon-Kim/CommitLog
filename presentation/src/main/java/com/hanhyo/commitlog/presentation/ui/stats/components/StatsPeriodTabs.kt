package com.hanhyo.commitlog.presentation.ui.stats.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.ui.stats.model.StatsPeriod

@Composable
fun StatsPeriodTabs(
    selectedPeriod: StatsPeriod,
    onPeriodSelected: (StatsPeriod) -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .height(48.dp)
            .background(
                color = CommitLogTheme.colors.surfaceVariant.copy(alpha = 0.5f),
                shape = CircleShape
            )
            .padding(4.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        val tabWidth = maxWidth / StatsPeriod.entries.size
        val indicatorOffset by animateDpAsState(
            targetValue = tabWidth * selectedPeriod.ordinal,
            animationSpec = spring(stiffness = Spring.StiffnessLow),
            label = "tabIndicatorOffset"
        )

        // 슬라이딩 인디케이터 배경
        Box(
            modifier = Modifier
                .graphicsLayer {
                    translationX = indicatorOffset.toPx()
                }
                .width(tabWidth)
                .fillMaxHeight()
                .background(color = CommitLogTheme.colors.primary, shape = CircleShape)
        )

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            StatsPeriod.entries.forEach { period ->
                val selected = selectedPeriod == period
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onPeriodSelected(period) }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = period.title,
                        style = CommitLogTheme.typography.titleMedium,
                        color = if (selected) {
                            Color.White
                        } else {
                            CommitLogTheme.colors.textTertiary
                        },
                        modifier = Modifier.zIndex(1f)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StatsPeriodTabsPreview() {
    var selectedPeriod by remember { mutableStateOf(StatsPeriod.WEEKLY) }
    CommitLogTheme {
        StatsPeriodTabs(
            selectedPeriod = selectedPeriod,
            onPeriodSelected = { selectedPeriod = it }
        )
    }
}
