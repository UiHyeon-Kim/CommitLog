package com.hanhyo.commitlog.presentation.ui.review.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions
import java.time.YearMonth

@Composable
fun ReviewMonthTabs(
    availableMonths: List<YearMonth>,
    selectedYear: Int,
    selectedMonth: Int,
    onMonthSelected: (Int, Int) -> Unit
) {
    if (availableMonths.isEmpty()) return

    val listState = rememberLazyListState()

    LaunchedEffect(selectedYear, selectedMonth) {
        val index = availableMonths.indexOfFirst { it.year == selectedYear && it.monthValue == selectedMonth }
        if (index >= 0) {
            listState.animateScrollToItem(index)
        }
    }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        state = listState,
        contentPadding = PaddingValues(horizontal = Dimensions.SpacingLarge),
        horizontalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(availableMonths) { month ->
            val selected = month.year == selectedYear && month.monthValue == selectedMonth

            Surface(
                onClick = { onMonthSelected(month.year, month.monthValue) },
                shape = RoundedCornerShape(100.dp),
                color = if (selected) CommitLogTheme.colors.primary else CommitLogTheme.colors.background,
                contentColor = if (selected) Color.White else CommitLogTheme.colors.textSecondary,
                border = if (selected) null else BorderStroke(1.dp, CommitLogTheme.colors.border),
            ) {
                Text(
                    text = "${month.year}년 ${month.monthValue}월",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = CommitLogTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReviewMonthTabsPreview() {
    val availableMonths = listOf(
        YearMonth.of(2024, 3),
        YearMonth.of(2024, 4),
        YearMonth.of(2024, 5),
    )
    var selectedYear by remember { mutableStateOf(2024) }
    var selectedMonth by remember { mutableStateOf(5) }

    CommitLogTheme {
        ReviewMonthTabs(
            availableMonths = availableMonths,
            selectedYear = selectedYear,
            selectedMonth = selectedMonth,
            onMonthSelected = { year, month ->
                selectedYear = year
                selectedMonth = month
            }
        )
    }
}
