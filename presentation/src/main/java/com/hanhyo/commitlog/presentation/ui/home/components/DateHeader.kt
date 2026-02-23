package com.hanhyo.commitlog.presentation.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DateHeader(
    date: LocalDate,
    totalCommitCount: Int? = null
) {
    val today = LocalDate.now()
    val isToday = date.isEqual(today)
    val isYesterday = date.isEqual(today.minusDays(1))

    val dateFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 EEEE", Locale.KOREAN)
    val dateString = date.format(dateFormatter)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CommitLogTheme.colors.background)
            .padding(vertical = Dimensions.SpacingSmall)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                if (isToday) {
                    Text(
                        text = "오늘의 기록",
                        style = CommitLogTheme.typography.headlineMedium,
                        color = CommitLogTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                } else if (isYesterday) {
                    Text(
                        text = "어제 기록",
                        style = CommitLogTheme.typography.headlineMedium,
                        color = CommitLogTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isToday || isYesterday) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = CommitLogTheme.colors.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = dateString,
                        style = CommitLogTheme.typography.bodyMedium,
                        color = if (isToday || isYesterday) CommitLogTheme.colors.primary else CommitLogTheme.colors.textTertiary
                    )
                }
            }

            if (totalCommitCount != null) {
                Text(
                    text = "총 ${totalCommitCount}개",
                    style = CommitLogTheme.typography.bodyMedium,
                    color = CommitLogTheme.colors.textTertiary,
                    modifier = Modifier
                        .background(
                            color = CommitLogTheme.colors.surface,
                            shape = RoundedCornerShape(Dimensions.ButtonRadius)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "DateHeader - Today")
@Composable
fun DateHeaderTodayPreview() {
    CommitLogTheme {
        DateHeader(date = LocalDate.now())
    }
}

@Preview(showBackground = true, name = "DateHeader - Yesterday")
@Composable
fun DateHeaderYesterdayPreview() {
    CommitLogTheme {
        DateHeader(date = LocalDate.now().minusDays(1))
    }
}

@Preview(showBackground = true, name = "DateHeader - Past")
@Composable
fun DateHeaderPastPreview() {
    CommitLogTheme {
        DateHeader(date = LocalDate.now().minusDays(7))
    }
}
