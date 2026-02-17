package com.hanhyo.commitlog.presentation.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hanhyo.commitlog.presentation.designsystem.components.bar.CommitLogTopAppBar
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import androidx.compose.ui.graphics.toArgb
import com.patrykandpatrick.vico.core.common.Fill
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.shape.Shape
@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CommitLogTopAppBar(
                title = "학습 통계",
            )
        },
        containerColor = CommitLogTheme.colors.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            StatsPeriodTabs(
                selectedPeriod = uiState.selectedPeriod,
                onPeriodSelected = viewModel::updatePeriod
            )

            Spacer(modifier = Modifier.height(Dimensions.SpacingMedium))

            StatisticsContent(
                period = uiState.selectedPeriod,
                viewModel = viewModel,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = Dimensions.SpacingLarge)
            )
        }
    }
}

@Composable
private fun StatsPeriodTabs(
    selectedPeriod: StatsPeriod,
    onPeriodSelected: (StatsPeriod) -> Unit,
) {
    TabRow(
        selectedTabIndex = selectedPeriod.ordinal,
        containerColor = Color.Transparent,
        contentColor = CommitLogTheme.colors.primary,
        indicator = { tabPositions ->
            if (selectedPeriod.ordinal < tabPositions.size) {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedPeriod.ordinal]),
                    color = CommitLogTheme.colors.primary,
                    height = 3.dp
                )
            }
        },
        divider = {
            HorizontalDivider(color = CommitLogTheme.colors.border)
        }
    ) {
        StatsPeriod.entries.forEach { period ->
            val selected = selectedPeriod == period
            Tab(
                selected = selected,
                onClick = { onPeriodSelected(period) },
                text = {
                    Text(
                        text = period.title,
                        style = if (selected) {
                            CommitLogTheme.typography.titleSmall
                        } else {
                            CommitLogTheme.typography.bodyMedium
                        },
                        color = if (selected) {
                            CommitLogTheme.colors.primary
                        } else {
                            CommitLogTheme.colors.textTertiary
                        }
                    )
                }
            )
        }
    }
}

@Composable
private fun StatisticsContent(
    period: StatsPeriod,
    viewModel: StatisticsViewModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingLarge)
    ) {
        when (period) {
            StatsPeriod.WEEKLY -> WeeklyStatsBox(viewModel.weeklyChartModelProducer)
            StatsPeriod.MONTHLY -> MonthlyStatsBox(viewModel.monthlyHeatmapData)
            StatsPeriod.YEARLY -> YearlyStatsBox(viewModel.yearlyChartModelProducer)
        }

        // 공통 섹션: 감정 분포 (Mood Distribution)
        MoodDistributionCard()

        Spacer(modifier = Modifier.height(Dimensions.SpacingLarge))
    }
}

@Composable
fun WeeklyStatsBox(modelProducer: CartesianChartModelProducer) {
    StatsCard(title = "주간 학습 흐름") {
        val daysOfWeek = listOf("월", "화", "수", "목", "금", "토", "일")
        val bottomAxisValueFormatter = CartesianValueFormatter { _, x, _ ->
            daysOfWeek.getOrNull(x.toInt()) ?: ""
        }

        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberColumnCartesianLayer(
                    columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                        rememberLineComponent(
                            Fill(CommitLogTheme.colors.primary.toArgb()),
                            thickness = 12.dp,
                            shape = Shape.Rectangle, // Rounded corners if verified
                        )
                    )
                ),
                startAxis = VerticalAxis.rememberStart(
                     itemPlacer = VerticalAxis.ItemPlacer.step(step = { 1.0 })
                ),
                bottomAxis = HorizontalAxis.rememberBottom(valueFormatter = bottomAxisValueFormatter),
            ),
            modelProducer = modelProducer,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium)
    ) {
        StatsSummaryCard(
            title = "총 Commit",
            value = "18회",
            icon = "🔥",
            modifier = Modifier.weight(1f)
        )
        StatsSummaryCard(
            title = "성공률",
            value = "85%",
            icon = "🎯",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun MonthlyStatsBox(heatmapData: List<Int>) {
    StatsCard(title = "월간 히트맵") {
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        ) {
            items(heatmapData) { level ->
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(MaterialTheme.shapes.small)
                        .background(
                            when (level) {
                                0 -> CommitLogTheme.colors.surfaceVariant
                                1 -> CommitLogTheme.colors.primary.copy(alpha = 0.3f)
                                2 -> CommitLogTheme.colors.primary.copy(alpha = 0.5f)
                                3 -> CommitLogTheme.colors.primary.copy(alpha = 0.7f)
                                else -> CommitLogTheme.colors.primary
                            }
                        )
                )
            }
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium)
    ) {
        StatsSummaryCard(
            title = "이번 달 기록",
            value = "70회",
            icon = "📅",
            modifier = Modifier.weight(1f)
        )
        StatsSummaryCard(
            title = "최장 연속",
            value = "12일",
            icon = "⚡",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun YearlyStatsBox(modelProducer: CartesianChartModelProducer) {
    StatsCard(title = "연간 추이") {
        val months = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")
        val bottomAxisValueFormatter = CartesianValueFormatter { _, x, _ ->
            months.getOrNull(x.toInt()) ?: ""
        }

        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(),
                startAxis = VerticalAxis.rememberStart(),
                bottomAxis = HorizontalAxis.rememberBottom(valueFormatter = bottomAxisValueFormatter),
            ),
            modelProducer = modelProducer,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
    }
}

@Composable
fun MoodDistributionCard() {
    StatsCard(title = "감정 분포") {
        Column(verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium)) {
            // Mock Data
            MoodProgressBar(label = "집중함 (Focused)", percentage = 0.45f, color = CommitLogTheme.colors.primary)
            MoodProgressBar(label = "성취감 (Productive)", percentage = 0.30f, color = CommitLogTheme.colors.accentPurple)
            MoodProgressBar(label = "힘듦 (Tired)", percentage = 0.15f, color = CommitLogTheme.colors.error)
            MoodProgressBar(label = "보통 (Normal)", percentage = 0.10f, color = CommitLogTheme.colors.textTertiary)
        }
    }
}

@Composable
fun MoodProgressBar(
    label: String,
    percentage: Float,
    color: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = CommitLogTheme.typography.bodySmall,
                color = CommitLogTheme.colors.textSecondary
            )
            Text(
                text = "${(percentage * 100).toInt()}%",
                style = CommitLogTheme.typography.labelSmall,
                color = CommitLogTheme.colors.textPrimary
            )
        }
        
        // Custom Progress Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(MaterialTheme.shapes.small)
                .background(CommitLogTheme.colors.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(percentage)
                    .fillMaxHeight()
                    .clip(MaterialTheme.shapes.small)
                    .background(color)
            )
        }
    }
}

@Composable
fun StatsCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = CommitLogTheme.colors.surface
        ),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.CardPadding),
            verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium)
        ) {
            Text(
                text = title,
                style = CommitLogTheme.typography.titleMedium,
                color = CommitLogTheme.colors.textPrimary
            )
            content()
        }
    }
}

@Composable
fun StatsSummaryCard(
    title: String,
    value: String,
    icon: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = CommitLogTheme.colors.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.CardPadding),
            verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingSmall)
        ) {
            Text(
                text = icon,
                style = CommitLogTheme.typography.titleMedium
            )
            Text(
                text = title,
                style = CommitLogTheme.typography.bodySmall,
                color = CommitLogTheme.colors.textSecondary
            )
            Text(
                text = value,
                style = CommitLogTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = CommitLogTheme.colors.textPrimary
            )
        }
    }
}
