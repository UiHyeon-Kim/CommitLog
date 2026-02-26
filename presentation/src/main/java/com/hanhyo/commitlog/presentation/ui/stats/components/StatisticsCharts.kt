package com.hanhyo.commitlog.presentation.ui.stats.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions
import com.hanhyo.commitlog.presentation.ui.stats.model.MonthlyStats
import com.hanhyo.commitlog.presentation.ui.stats.model.MoodDistribution
import com.hanhyo.commitlog.presentation.ui.stats.model.ProductiveDay
import com.hanhyo.commitlog.presentation.ui.stats.model.Skill
import com.hanhyo.commitlog.presentation.ui.stats.model.StatsPeriod
import com.hanhyo.commitlog.presentation.ui.stats.model.WeeklyStats
import com.hanhyo.commitlog.presentation.ui.stats.model.YearlyHeatmapData
import com.hanhyo.commitlog.presentation.ui.stats.model.YearlyStats
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.common.component.LineComponent
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.Fill
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
                text = "이번 주 리포트",
                style = CommitLogTheme.typography.headlineMedium,
                color = CommitLogTheme.colors.textPrimary
            )
            Text(
                text = "최근 7일간의 기록입니다.",
                style = CommitLogTheme.typography.bodyMedium,
                color = CommitLogTheme.colors.textSecondary
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium)
        ) {
            StatsSummaryCard(
                title = "총 집중 시간",
                value = "${stats.focusTime} 시간",
                icon = "⏱️",
                modifier = Modifier.weight(1f)
            )
            StatsSummaryCard(
                title = "주간 커밋",
                value = "${stats.commitCount} 회",
                icon = "commit",
                modifier = Modifier.weight(1f)
            )
        }

        StatsCard(title = "일별 커밋 활동") {
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
            StatsCard(title = "가장 생산적이었던 날") {
                Column(verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium)) {
                    stats.productiveDays.forEachIndexed { index, day ->
                        ProductiveDayItem(day = day, rank = index + 1)
                    }
                }
            }
        }

        AiInsightCard(
            insight = "꾸준한 기록이 성장의 밑거름이 됩니다. 오늘도 화이팅하세요!"
        )
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
                text = "월간 리포트",
                style = CommitLogTheme.typography.headlineMedium,
                color = CommitLogTheme.colors.textPrimary
            )
            Text(
                text = "이번 달의 기록입니다.",
                style = CommitLogTheme.typography.bodyMedium,
                color = CommitLogTheme.colors.textSecondary
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium)
        ) {
            StatsSummaryCard(
                title = "Commit 빈도",
                value = "${stats.commitFrequency} 회",
                icon = "commit",
                modifier = Modifier.weight(1f)
            )
            StatsSummaryCard(
                title = "연속 기록",
                value = "${stats.streak} 일",
                icon = "🔥",
                modifier = Modifier.weight(1f)
            )
        }

        StatsCard(title = "기분 흐름 (Mood Flow)") {
            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberLineCartesianLayer(
                        lineProvider = LineCartesianLayer.LineProvider.series(
                            LineCartesianLayer.Line(
                                fill = LineCartesianLayer.LineFill.single(Fill(CommitLogTheme.colors.primary.toArgb())),
                                areaFill = LineCartesianLayer.AreaFill.single(
                                    Fill(CommitLogTheme.colors.primary.copy(alpha = 0.3f).toArgb())
                                )
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
            StatsCard(title = "AI Mood 분포") {
                Column(verticalArrangement = Arrangement.spacedBy(Dimensions.SpacingMedium)) {
                    stats.moods.forEach { mood ->
                        MoodProgressBar(label = mood.label, percentage = mood.percentage, color = mood.color)
                    }
                }
            }
        }

        if (stats.keywords.isNotEmpty()) {
            StatsCard(title = "이달의 키워드 ✨") {
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
    )
    val label = rememberTextComponent(
        color = CommitLogTheme.colors.textPrimary,
        background = labelBackground
    )
    return rememberDefaultCartesianMarker(
        label = label,
        labelPosition = DefaultCartesianMarker.LabelPosition.Top,
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
                    text = "연간 리포트",
                    style = CommitLogTheme.typography.headlineMedium,
                    color = CommitLogTheme.colors.textPrimary
                )
                Text(
                    text = "올해의 기여도와 성장 기록입니다.",
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
                title = "총 커밋 기록",
                value = "${stats.totalRecords}",
                icon = "📊",
                modifier = Modifier.weight(1f)
            )
            StatsSummaryCard(
                title = "가장 바빴던 달",
                value = "${stats.busiestMonth} 🔥",
                icon = "📅",
                modifier = Modifier.weight(1f)
            )
        }

        StatsCard(title = "연간 기여도") {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                    Text(
                        text = "Total ${stats.totalRecords} Commits",
                        style = CommitLogTheme.typography.bodySmall,
                        color = CommitLogTheme.colors.textSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    Text("Less", style = CommitLogTheme.typography.labelSmall, color = CommitLogTheme.colors.textSecondary)
                    Spacer(Modifier.width(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        (0..4).forEach { level ->
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(heatmapColor(level))
                            )
                        }
                    }
                    Spacer(Modifier.width(4.dp))
                    Text("More", style = CommitLogTheme.typography.labelSmall, color = CommitLogTheme.colors.textSecondary)
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
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Month Label
                                val label = remember(weekIndex) {
                                    val firstRealDayIndex = days.indexOfFirst { it >= -1 }
                                    if (firstRealDayIndex != -1) {
                                        val dayOfYear = (weekIndex * 7 + firstRealDayIndex - firstDayOffset) + 1
                                        if (dayOfYear >= 1 && dayOfYear <= startDate.lengthOfYear()) {
                                            val date = startDate.withDayOfYear(dayOfYear.toInt())
                                            if (date.dayOfMonth <= 7) "${date.monthValue}월" else ""
                                        } else ""
                                    } else ""
                                }

                                Box(modifier = Modifier.height(16.dp)) {
                                    if (label.isNotEmpty()) {
                                        Text(
                                            text = label,
                                            style = CommitLogTheme.typography.labelSmall,
                                            color = CommitLogTheme.colors.textSecondary,
                                            maxLines = 1
                                        )
                                    }
                                }

                                days.forEach { level ->
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(heatmapColor(level))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (stats.skillGrowth.size >= 3) {
            StatsCard(title = "스킬 성장 (Skill Radar)") {
                RadarChart(
                    skills = stats.skillGrowth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
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
        val radius = size.minDimension / 2 * 0.7f
        val center = Offset(size.width / 2, size.height / 2)
        val angleStep = (2 * Math.PI / numSides).toFloat()

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
                    style = textStyle,
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
            val scoreRatio = skill?.score?.coerceIn(0f, 1f) ?: 0f
            // 점수가 매우 낮더라도 시각적으로 구분되도록 최소 비율 보장
            val finalRatio = scoreRatio.coerceAtLeast(0.1f)
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

// MonthLabels 제거됨 (동적으로 YearlyStatsView 내부에 통합)

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
