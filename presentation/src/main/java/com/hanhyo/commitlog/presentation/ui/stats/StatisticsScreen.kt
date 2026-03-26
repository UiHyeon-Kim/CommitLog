package com.hanhyo.commitlog.presentation.ui.stats

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.hanhyo.commitlog.presentation.R
import com.hanhyo.commitlog.presentation.designsystem.components.bar.CommitLogTopAppBar
import com.hanhyo.commitlog.presentation.designsystem.theme.CommitLogTheme
import com.hanhyo.commitlog.presentation.designsystem.theme.dimension.Dimensions
import com.hanhyo.commitlog.presentation.ui.stats.components.StatisticsCharts
import com.hanhyo.commitlog.presentation.ui.stats.components.StatisticsSkeleton
import com.hanhyo.commitlog.presentation.ui.stats.components.StatsPeriodTabs
import com.hanhyo.commitlog.presentation.ui.stats.model.MonthlyStats
import com.hanhyo.commitlog.presentation.ui.stats.model.MoodDistribution
import com.hanhyo.commitlog.presentation.ui.stats.model.ProductiveDay
import com.hanhyo.commitlog.presentation.ui.stats.model.Skill
import com.hanhyo.commitlog.presentation.ui.stats.model.StatsPeriod
import com.hanhyo.commitlog.presentation.ui.stats.model.WeeklyStats
import com.hanhyo.commitlog.presentation.ui.stats.model.YearlyStats
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel(),
    onNavigateToWrite: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val lifecycleOwner = LocalLifecycleOwner.current

    val pagerState = rememberPagerState(
        initialPage = uiState.selectedPeriod.ordinal,
        pageCount = { StatsPeriod.entries.size }
    )

    val onPeriodSelected = remember(viewModel) {
        { period: StatsPeriod -> viewModel.updatePeriod(period) }
    }

    val onNavigateToWriteFixed = remember(onNavigateToWrite) {
        onNavigateToWrite
    }

    val currentOnPeriodSelected by rememberUpdatedState(onPeriodSelected)

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { page ->
                val selectedPeriod = StatsPeriod.entries[page]
                currentOnPeriodSelected(selectedPeriod)
            }
    }

    // 탭 클릭 시 애니메이션 이동 (상태 변경에 반응)
    LaunchedEffect(uiState.selectedPeriod) {
        if (pagerState.currentPage != uiState.selectedPeriod.ordinal) {
            pagerState.animateScrollToPage(uiState.selectedPeriod.ordinal)
        }
    }

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is StatisticsEffect.ShowSnackbar -> {
                        snackbarHostState.showSnackbar(effect.message)
                    }
                }
            }
        }
    }

    StatisticsContent(
        uiState = uiState,
        pagerState = pagerState,
        weeklyChartModelProducer = viewModel.weeklyChartModelProducer,
        monthlyChartModelProducer = viewModel.monthlyChartModelProducer,
        yearlyChartModelProducer = viewModel.yearlyChartModelProducer,
        onPeriodSelected = onPeriodSelected,
        onNavigateToWrite = onNavigateToWriteFixed,
        snackbarHostState = snackbarHostState
    )
}

@Composable
private fun StatisticsContent(
    uiState: StatisticsUiState,
    pagerState: PagerState,
    weeklyChartModelProducer: CartesianChartModelProducer,
    monthlyChartModelProducer: CartesianChartModelProducer,
    yearlyChartModelProducer: CartesianChartModelProducer,
    onPeriodSelected: (StatsPeriod) -> Unit,
    onNavigateToWrite: () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    Scaffold(
        topBar = {
            CommitLogTopAppBar(
                title = stringResource(R.string.stats_title),
            )
        },
        containerColor = CommitLogTheme.colors.background,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = CommitLogTheme.colors.surface,
                    contentColor = CommitLogTheme.colors.textPrimary,
                    actionColor = CommitLogTheme.colors.primary
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            StatsPeriodTabs(
                selectedPeriod = uiState.selectedPeriod,
                onPeriodSelected = onPeriodSelected
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
            ) { page ->
                val periodForPage = StatsPeriod.entries[page]
                val isCurrentPageLoading = periodForPage in uiState.loadingPeriods
                val isNotLoadedYet = periodForPage !in uiState.loadedPeriods

                if (isCurrentPageLoading || isNotLoadedYet) {
                    StatisticsSkeleton(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = Dimensions.SpacingLarge)
                    )
                } else {
                    StatisticsCharts(
                        period = periodForPage,
                        weeklyStats = uiState.weeklyStats,
                        monthlyStats = uiState.monthlyStats,
                        yearlyStats = uiState.yearlyStats,
                        yearlyHeatmap = uiState.yearlyHeatmapData,
                        weeklyChartModelProducer = weeklyChartModelProducer,
                        monthlyChartModelProducer = monthlyChartModelProducer,
                        yearlyChartModelProducer = yearlyChartModelProducer,
                        onNavigateToWrite = onNavigateToWrite,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = Dimensions.SpacingLarge)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StatisticsScreenPreview() {
    CommitLogTheme {
        val dummyWeeklyProducer = CartesianChartModelProducer()
        val dummyMonthlyProducer = CartesianChartModelProducer()
        val dummyYearlyProducer = CartesianChartModelProducer()
        val dummyPagerState = rememberPagerState(pageCount = { StatsPeriod.entries.size })

        StatisticsContent(
            uiState = StatisticsUiState(
                selectedPeriod = StatsPeriod.WEEKLY,
                weeklyStats = WeeklyStats(
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
                monthlyStats = MonthlyStats(
                    commitFrequency = 12,
                    streak = 3,
                    moods = listOf(
                        MoodDistribution(
                            label = "집중함",
                            percentage = 0.6f,
                            color = Color(0xFF2196F3)
                        ),
                        MoodDistribution(
                            label = "뿌듯함",
                            percentage = 0.4f,
                            color = Color(0xFF4CAF50)
                        )
                    )
                ),
                yearlyStats = YearlyStats(
                    totalRecords = 120,
                    busiestMonth = "10월",
                    skillGrowth = listOf(
                        Skill("Jetpack Compose", 0.8f),
                        Skill("Kotlin Coroutines", 0.6f)
                    )
                ),
                yearlyHeatmapData = List(365) { (0..4).random() }
            ),
            pagerState = dummyPagerState,
            weeklyChartModelProducer = dummyWeeklyProducer,
            monthlyChartModelProducer = dummyMonthlyProducer,
            yearlyChartModelProducer = dummyYearlyProducer,
            onPeriodSelected = {},
            onNavigateToWrite = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}
