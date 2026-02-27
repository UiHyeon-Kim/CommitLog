package com.hanhyo.commitlog.presentation.ui.stats.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hanhyo.commitlog.presentation.R
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions
import com.hanhyo.commitlog.presentation.ui.stats.model.MonthlyStats
import com.hanhyo.commitlog.presentation.ui.stats.model.MoodDistribution
import com.hanhyo.commitlog.presentation.ui.stats.model.ProductiveDay
import com.hanhyo.commitlog.presentation.ui.stats.model.Skill
import com.hanhyo.commitlog.presentation.ui.stats.model.StatsPeriod
import com.hanhyo.commitlog.presentation.ui.stats.model.WeeklyStats
import com.hanhyo.commitlog.presentation.ui.stats.model.YearlyStats
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.Fill
import com.patrykandpatrick.vico.core.common.Insets
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun StatisticsCharts(
    period: StatsPeriod,
    weeklyStats: WeeklyStats,
    monthlyStats: MonthlyStats,
    yearlyStats: YearlyStats,
    yearlyHeatmap: List<Int>,
    weeklyChartModelProducer: CartesianChartModelProducer,
    monthlyChartModelProducer: CartesianChartModelProducer,
    yearlyChartModelProducer: CartesianChartModelProducer,
    onNavigateToWrite: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isEmpty = when (period) {
        StatsPeriod.WEEKLY -> weeklyStats.commitCount == 0
        StatsPeriod.MONTHLY -> monthlyStats.commitFrequency == 0
        StatsPeriod.YEARLY -> yearlyStats.totalRecords == 0
    }

    if (isEmpty) {
        StatisticsEmptyView(
            onNavigateToWrite = onNavigateToWrite,
            modifier = modifier
        )
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingLarge)
    ) {
        when (period) {
            StatsPeriod.WEEKLY -> WeeklyStatsView(weeklyStats, chartProducer = weeklyChartModelProducer)
            StatsPeriod.MONTHLY -> MonthlyStatsView(monthlyStats, chartProducer = monthlyChartModelProducer)
            StatsPeriod.YEARLY -> YearlyStatsView(
                yearlyStats,
                heatmap = yearlyHeatmap,
                chartProducer = yearlyChartModelProducer
            )
        }

        Spacer(modifier = Modifier.height(Dimensions.SpacingLarge))
    }
}

/**
 * 주간 통계 데이터를 화면에 표시하는 컴포저블입니다.
 * 총 집중 시간, 주간 커밋 횟수 요약과 일별 커밋 활동 차트, 그리고 AI 인사이트를 제공합니다.
 */
@Composable
private fun WeeklyStatsView(stats: WeeklyStats, chartProducer: CartesianChartModelProducer) {
    Column(
        modifier = Modifier.padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingLarge)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = stringResource(R.string.stats_report_weekly),
                style = CommitLogTheme.typography.headlineMedium,
                color = CommitLogTheme.colors.textPrimary
            )
            Text(
                text = stringResource(R.string.stats_report_weekly_desc),
                style = CommitLogTheme.typography.bodyMedium,
                color = CommitLogTheme.colors.textSecondary
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium)
        ) {
            StatsSummaryCard(
                title = stringResource(R.string.stats_summary_focus_time),
                value = stringResource(R.string.common_unit_hours, stats.focusTime),
                icon = "⏱️",
                modifier = Modifier.weight(1f)
            )
            StatsSummaryCard(
                title = stringResource(R.string.stats_summary_commit_count),
                value = stringResource(R.string.common_unit_count, stats.commitCount),
                icon = "commit",
                modifier = Modifier.weight(1f)
            )
        }

        StatsCard(title = stringResource(R.string.stats_chart_daily_activity)) {
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
                                shape = CorneredShape.rounded(
                                    topLeftPercent = 50,
                                    topRightPercent = 50
                                ),
                            )
                        )
                    ),
                    startAxis = null,
                    bottomAxis = HorizontalAxis.rememberBottom(
                        valueFormatter = bottomAxisValueFormatter,
                        guideline = null
                    ),
                    marker = rememberCartesianMarker(),
                ),
                modelProducer = chartProducer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

        if (stats.productiveDays.isNotEmpty()) {
            StatsCard(title = stringResource(R.string.stats_card_productive_day)) {
                Column(verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium)) {
                    stats.productiveDays.forEachIndexed { index, day ->
                        ProductiveDayItem(day = day, rank = index + 1)
                    }
                }
            }
        }
    }
}

/**
 * 월간 통계 데이터를 화면에 표시하는 컴포저블입니다.
 * 커밋 빈도, 연속 기록 요약과 월간 활동 히트맵, AI Mood 분포를 제공합니다.
 */
@Composable
private fun MonthlyStatsView(stats: MonthlyStats, chartProducer: CartesianChartModelProducer) {
    Column(
        modifier = Modifier.padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingLarge)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = stringResource(R.string.stats_report_monthly),
                style = CommitLogTheme.typography.headlineMedium,
                color = CommitLogTheme.colors.textPrimary
            )
            Text(
                text = stringResource(R.string.stats_report_monthly_desc),
                style = CommitLogTheme.typography.bodyMedium,
                color = CommitLogTheme.colors.textSecondary
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium)
        ) {
            StatsSummaryCard(
                title = stringResource(R.string.stats_summary_frequency),
                value = stringResource(R.string.common_unit_count, stats.commitFrequency),
                icon = "commit",
                modifier = Modifier.weight(1f)
            )
            StatsSummaryCard(
                title = stringResource(R.string.stats_summary_streak),
                value = stringResource(R.string.common_unit_days, stats.streak),
                icon = "🔥",
                modifier = Modifier.weight(1f)
            )
        }

        StatsCard(title = stringResource(R.string.stats_chart_mood_flow)) {
            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberLineCartesianLayer(
                        lineProvider = LineCartesianLayer.LineProvider.series(
                            LineCartesianLayer.Line(
                                fill = LineCartesianLayer.LineFill.single(Fill(CommitLogTheme.colors.primary.toArgb())),
                                areaFill = LineCartesianLayer.AreaFill.single(
                                    Fill(CommitLogTheme.colors.primary.copy(alpha = 0.3f).toArgb())
                                ),
                                pointConnector = LineCartesianLayer.PointConnector.cubic()
                            )
                        )
                    ),
                    startAxis = null,
                    bottomAxis = HorizontalAxis.rememberBottom(
                        valueFormatter = { _, x, _ -> "${(x.toInt() * 3) + 1}일" },
                        guideline = null
                    ),
                    marker = rememberCartesianMarker(),
                ),
                modelProducer = chartProducer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

        if (stats.moods.isNotEmpty()) {
            StatsCard(title = stringResource(R.string.stats_chart_mood_dist)) {
                Column(verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium)) {
                    stats.moods.forEach { mood ->
                        MoodProgressBar(label = mood.label, percentage = mood.percentage, color = mood.color)
                    }
                }
            }
        }

        if (stats.keywords.isNotEmpty()) {
            StatsCard(title = stringResource(R.string.stats_chart_keywords)) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    stats.keywords.forEach { keyword ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(CommitLogTheme.colors.surfaceVariant)
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = keyword,
                                style = CommitLogTheme.typography.bodySmall,
                                color = CommitLogTheme.colors.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun rememberCartesianMarker(): CartesianMarker {
    val labelBackgroundShape = CorneredShape.rounded(allPercent = 25)
    val labelBackground = rememberShapeComponent(
        fill = Fill(CommitLogTheme.colors.surfaceVariant.toArgb()),
        shape = labelBackgroundShape,
        margins = Insets(4f)
    )
    val label = rememberTextComponent(
        color = CommitLogTheme.colors.textPrimary,
        background = labelBackground
    )
    val indicatorComponent = rememberShapeComponent(
        fill = Fill(CommitLogTheme.colors.primary.toArgb()),
        shape = CorneredShape.Pill
    )
    return rememberDefaultCartesianMarker(
        label = label,
        labelPosition = DefaultCartesianMarker.LabelPosition.Top,
        indicator = { indicatorComponent }
    )
}

@Composable
private fun YearlyStatsView(
    stats: YearlyStats,
    heatmap: List<Int>,
    chartProducer: CartesianChartModelProducer
) {
    Column(
        modifier = Modifier.padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingLarge)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(R.string.stats_report_yearly),
                    style = CommitLogTheme.typography.headlineMedium,
                    color = CommitLogTheme.colors.textPrimary
                )
                Text(
                    text = stringResource(R.string.stats_report_yearly_desc),
                    style = CommitLogTheme.typography.bodyMedium,
                    color = CommitLogTheme.colors.textSecondary
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium)
        ) {
            StatsSummaryCard(
                title = stringResource(R.string.stats_summary_total_records),
                value = "${stats.totalRecords}",
                icon = "📊",
                modifier = Modifier.weight(1f)
            )
            StatsSummaryCard(
                title = stringResource(R.string.stats_summary_busiest_month),
                value = "${stats.busiestMonth} 🔥",
                icon = "📅",
                modifier = Modifier.weight(1f)
            )
        }

        StatsCard(title = stringResource(R.string.stats_chart_contribution)) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                    Text(
                        text = "Total ${stats.totalRecords} Commits",
                        style = CommitLogTheme.typography.bodySmall,
                        color = CommitLogTheme.colors.textSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "Less",
                        style = CommitLogTheme.typography.labelSmall,
                        color = CommitLogTheme.colors.textSecondary
                    )
                    Spacer(Modifier.width(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        (0..4).forEach { level ->
                            Box(
                                modifier = Modifier
                                    .size(11.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(heatmapColor(level))
                            )
                        }
                    }
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "More",
                        style = CommitLogTheme.typography.labelSmall,
                        color = CommitLogTheme.colors.textSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        val weeks = heatmap.chunked(7)
                        val currentYear = java.time.LocalDate.now().year
                        val startDate = java.time.LocalDate.of(currentYear, 1, 1)
                        val firstDayOffset = startDate.dayOfWeek.value % 7

                        weeks.forEachIndexed { weekIndex, days ->
                            val currentMonth = run {
                                val firstRealDayIndex = days.indexOfFirst { it >= -1 }
                                if (firstRealDayIndex != -1) {
                                    val dayOfYear = (weekIndex * 7 + firstRealDayIndex - firstDayOffset) + 1
                                    if (dayOfYear >= 1 && dayOfYear <= startDate.lengthOfYear()) {
                                        startDate.withDayOfYear(dayOfYear).monthValue
                                    } else -1
                                } else -1
                            }

                            val prevMonth = run {
                                if (weekIndex > 0) {
                                    val prevWeekDays = weeks[weekIndex - 1]
                                    val firstRealDayIndex = prevWeekDays.indexOfFirst { it >= -1 }
                                    if (firstRealDayIndex != -1) {
                                        val dayOfYear = ((weekIndex - 1) * 7 + firstRealDayIndex - firstDayOffset) + 1
                                        if (dayOfYear >= 1 && dayOfYear <= startDate.lengthOfYear()) {
                                            startDate.withDayOfYear(dayOfYear).monthValue
                                        } else -1
                                    } else -1
                                } else -1
                            }

                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Month Label (월이 바뀔 때만 표시)
                                val labelText =
                                    if (currentMonth != -1 && currentMonth != prevMonth) "${currentMonth}월" else ""

                                Box(modifier = Modifier.height(16.dp)) {
                                    if (labelText.isNotEmpty()) {
                                        Text(
                                            text = labelText,
                                            style = CommitLogTheme.typography.labelSmall,
                                            color = CommitLogTheme.colors.textSecondary,
                                            maxLines = 1,
                                            modifier = Modifier.align(Alignment.CenterStart)
                                        )
                                    }
                                }

                                days.forEach { level ->
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(heatmapColor(level))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        val skillSize = stats.skillGrowth.size
        if (skillSize >= 3) {
            StatsCard(title = stringResource(R.string.stats_chart_radar)) {
                RadarChart(
                    skills = stats.skillGrowth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(if (skillSize == 3) Modifier.height(280.dp) else Modifier.height(300.dp))
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun RadarChart(
    skills: List<Skill>,
    modifier: Modifier = Modifier,
    lineColor: Color = CommitLogTheme.colors.surfaceVariant,
    fillColor: Color = CommitLogTheme.colors.primary,
    textColor: Color = CommitLogTheme.colors.textPrimary
) {
    val numSides = skills.size.coerceAtLeast(3)
    val textMeasurer = rememberTextMeasurer()
    val textStyle = CommitLogTheme.typography.labelSmall.copy(color = textColor)

    Canvas(modifier = modifier) {
        val radius = size.minDimension / 2 * 0.8f // 크기 확장
        val center = Offset(size.width / 2, size.height / 2)
        val angleStep = (2 * Math.PI / numSides).toFloat()

        // 스코어 정규화 (가장 높은 점수가 1.0이 되도록 하여 그래프가 펼쳐지게 함)
        val maxScore = skills.maxOfOrNull { it.score }?.coerceAtLeast(0.1f) ?: 1f

        // 1. 방사형 웹 구조 그리기 (배경 다각형)
        for (step in 1..4) {
            val stepRadius = radius * (step / 4f)
            val path = Path()
            for (i in 0 until numSides) {
                val angle = angleStep * i - (Math.PI / 2).toFloat()
                val x = center.x + stepRadius * cos(angle)
                val y = center.y + stepRadius * sin(angle)
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()
            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(
                    width = 2f,
                    pathEffect = if (step == 4) null else PathEffect.dashPathEffect(
                        floatArrayOf(10f, 10f)
                    ) // 최외곽 라인은 실선, 내부는 점선 처리
                )
            )
        }

        // 2. 방사형 가이드라인 그리기
        for (i in 0 until numSides) {
            val angle = angleStep * i - (Math.PI / 2).toFloat()
            val endX = center.x + radius * cos(angle)
            val endY = center.y + radius * sin(angle)
            drawLine(
                color = lineColor,
                start = center,
                end = Offset(endX, endY),
                strokeWidth = 2f
            )
        }

        // 3. 최외곽에 라벨 그리기
        for (i in 0 until numSides) {
            val angle = angleStep * i - (Math.PI / 2).toFloat()
            val labelRadius = radius * 1.2f // 라벨이 바깥에 위치하도록 반경 조정
            val x = center.x + labelRadius * cos(angle)
            val y = center.y + labelRadius * sin(angle)

            val skill = skills.getOrNull(i)
            if (skill != null) {
                val measuredText = textMeasurer.measure(skill.name, textStyle)
                drawText(
                    textMeasurer = textMeasurer,
                    text = skill.name,
                    style = textStyle.copy(fontSize = 10.sp),
                    topLeft = Offset(
                        x = x - measuredText.size.width / 2,
                        y = y - measuredText.size.height / 2
                    )
                )
            }
        }

        // 4. 데이터 다각형 그리기
        val dataPath = Path()
        for (i in 0 until numSides) {
            val skill = skills.getOrNull(i)
            // 개별 점수를 전체 값 중 최대값으로 나누어 정규화 (최소 0.1f 보장)
            val normalizedRatio = (skill?.score ?: 0f) / maxScore
            val finalRatio = normalizedRatio.coerceAtLeast(0.1f)

            val angle = angleStep * i - (Math.PI / 2).toFloat()
            val dataRadius = radius * finalRatio
            val x = center.x + dataRadius * cos(angle)
            val y = center.y + dataRadius * sin(angle)

            if (i == 0) dataPath.moveTo(x, y) else dataPath.lineTo(x, y)
        }
        dataPath.close()

        drawPath(
            path = dataPath,
            color = fillColor.copy(alpha = 0.4f),
            style = androidx.compose.ui.graphics.drawscope.Fill
        )
        drawPath(
            path = dataPath,
            color = fillColor,
            style = Stroke(width = 8f)
        )
    }
}

@Composable
private fun heatmapColor(level: Int): Color = when (level) {
    -2 -> Color.Transparent // 오프셋
    -1 -> CommitLogTheme.colors.surfaceVariant // 데이터 없음 (기존 0)
    0 -> CommitLogTheme.colors.surfaceVariant
    1 -> CommitLogTheme.colors.primary.copy(alpha = 0.3f)
    2 -> CommitLogTheme.colors.primary.copy(alpha = 0.5f)
    3 -> CommitLogTheme.colors.primary.copy(alpha = 0.7f)
    else -> CommitLogTheme.colors.primary
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun WeeklyStatsViewPreview() {
    CommitLogTheme {
        val dummyWeeklyProducer = CartesianChartModelProducer()

        Column(modifier = Modifier.padding(16.dp)) {
            WeeklyStatsView(
                stats = WeeklyStats(
                    focusTime = 14,
                    commitCount = 5,
                    productiveDays = listOf(
                        ProductiveDay(
                            day = "수요일",
                            count = 3,
                            description = "커밋 3개 작성",
                            isTop = true
                        ),
                        ProductiveDay(
                            day = "월요일",
                            count = 2,
                            description = "커밋 2개 작성",
                            isTop = false
                        )
                    )
                ),
                chartProducer = dummyWeeklyProducer
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun MonthlyStatsViewPreview() {
    CommitLogTheme {
        val dummyMonthlyProducer = CartesianChartModelProducer()
        Column(modifier = Modifier.padding(16.dp)) {
            MonthlyStatsView(
                stats = MonthlyStats(
                    commitFrequency = 12,
                    streak = 3,
                    moods = listOf(
                        MoodDistribution(
                            label = "집중함",
                            percentage = 0.6f,
                            color = Color(0xFF2196F3)
                        )
                    ),
                    keywords = listOf("#프로젝트_완료", "#새벽운동", "#독서습관")
                ),
                chartProducer = dummyMonthlyProducer
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun YearlyStatsViewPreview() {
    CommitLogTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            YearlyStatsView(
                stats = YearlyStats(
                    totalRecords = 120,
                    busiestMonth = "10월",
                    skillGrowth = listOf(
                        Skill("Jetpack Compose", 0.8f),
                        Skill("Kotlin Coroutines", 0.6f),
                        Skill("Android Architecture", 0.4f)
                    )
                ),
                heatmap = List(365) { (0..4).random() },
                chartProducer = CartesianChartModelProducer()
            )
        }
    }
}
