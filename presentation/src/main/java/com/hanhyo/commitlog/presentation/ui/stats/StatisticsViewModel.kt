package com.hanhyo.commitlog.presentation.ui.stats

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hanhyo.commitlog.domain.usecase.commit.GetCommitsByDateRangeUseCase
import com.hanhyo.commitlog.presentation.ui.stats.model.MonthlyStats
import com.hanhyo.commitlog.presentation.ui.stats.model.MoodDistribution
import com.hanhyo.commitlog.presentation.ui.stats.model.ProductiveDay
import com.hanhyo.commitlog.presentation.ui.stats.model.Skill
import com.hanhyo.commitlog.presentation.ui.stats.model.StatsPeriod
import com.hanhyo.commitlog.presentation.ui.stats.model.WeeklyStats
import com.hanhyo.commitlog.presentation.ui.stats.model.YearlyStats
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getCommitsByDateRangeUseCase: GetCommitsByDateRangeUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<StatisticsEffect>(replay = 0)
    val effect: SharedFlow<StatisticsEffect> = _effect.asSharedFlow()

    val weeklyChartModelProducer = CartesianChartModelProducer()
    val monthlyChartModelProducer = CartesianChartModelProducer()
    val yearlyChartModelProducer = CartesianChartModelProducer()

    init {
        loadStatistics(StatsPeriod.WEEKLY)
    }

    fun updatePeriod(period: StatsPeriod) {
        _uiState.update { it.copy(selectedPeriod = period) }
        loadStatistics(period)
    }

    private fun loadStatistics(period: StatsPeriod) {
        viewModelScope.launch {
            val isFirstLoad = period !in _uiState.value.loadedPeriods
            if (isFirstLoad) {
                _uiState.update { it.copy(loadingPeriods = it.loadingPeriods + period) }
            }
            val now = LocalDate.now()

            when (period) {
                StatsPeriod.WEEKLY -> loadWeeklyStats(now)
                StatsPeriod.MONTHLY -> loadMonthlyStats(now)
                StatsPeriod.YEARLY -> loadYearlyStats(now)
            }
            _uiState.update {
                it.copy(
                    loadingPeriods = it.loadingPeriods - period,
                    loadedPeriods = it.loadedPeriods + period
                )
            }
        }
    }

    private suspend fun loadWeeklyStats(now: LocalDate) {
        // 오늘을 포함한 최근 7일
        val startDate = now.minusDays(6)
        getCommitsByDateRangeUseCase(startDate, now).onSuccess { commits ->
            val dailyCounts = (0..6).map { offset ->
                val date = startDate.plusDays(offset.toLong())
                commits.count { it.date == date }
            }

            val productiveDays = (0..6).mapNotNull { offset ->
                val date = startDate.plusDays(offset.toLong())
                val count = commits.count { it.date == date }
                if (count > 0) {
                    ProductiveDay(
                        day = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.KOREAN),
                        count = count,
                        description = "커밋 ${count}개 작성",
                        isTop = false
                    )
                } else null
            }.sortedByDescending { it.count }.take(3).mapIndexed { index, day ->
                day.copy(isTop = index == 0)
            }

            val newWeeklyStats = WeeklyStats(
                commitCount = commits.size,
                focusTime = 0,
                productiveDays = productiveDays
            )

            if (_uiState.value.weeklyStats != newWeeklyStats) {
                weeklyChartModelProducer.runTransaction {
                    columnSeries {
                        series(dailyCounts)
                    }
                }
                _uiState.update { state ->
                    state.copy(weeklyStats = newWeeklyStats)
                }
            }
        }.onFailure {
            _effect.emit(StatisticsEffect.ShowSnackbar("데이터를 불러오지 못했습니다."))
        }
    }

    private suspend fun loadMonthlyStats(now: LocalDate) {
        val startDate = now.withDayOfMonth(1)
        val endDate = now.withDayOfMonth(now.lengthOfMonth())

        getCommitsByDateRangeUseCase(startDate, endDate).onSuccess { commits ->
            val dailyMoodScores = (1..now.lengthOfMonth()).map { day ->
                val date = now.withDayOfMonth(day)
                val dailyCommits = commits.filter { it.date == date }
                if (dailyCommits.isNotEmpty()) {
                    val moodTotal = dailyCommits.mapNotNull { it.analysis?.mood?.name?.length }.sum()
                    (moodTotal + dailyCommits.size * 2).coerceIn(1..10)
                } else {
                    0
                }
            }

            val tagCounts = commits.flatMap { it.tags }.groupingBy { it.value }.eachCount()
            val keywords = tagCounts.entries.sortedByDescending { it.value }.take(5).map { "#${it.key}" }

            val moodCounts = commits.mapNotNull { it.analysis?.mood }.groupingBy { it }.eachCount()
            val totalMoods = moodCounts.values.sum()
            val moodDist = moodCounts.entries.map { (mood, count) ->
                MoodDistribution(
                    label = mood.displayNameKo,
                    percentage = if (totalMoods > 0) count.toFloat() / totalMoods else 0f,
                    color = Color(0xFF2196F3)
                )
            }.sortedByDescending { it.percentage }

            val newMonthlyStats = MonthlyStats(
                commitFrequency = commits.size,
                streak = 0,
                moods = moodDist,
                keywords = keywords
            )

            if (_uiState.value.monthlyStats != newMonthlyStats) {
                monthlyChartModelProducer.runTransaction {
                    lineSeries {
                        series(dailyMoodScores)
                    }
                }
                _uiState.update { state ->
                    state.copy(monthlyStats = newMonthlyStats)
                }
            }
        }.onFailure {
            _effect.emit(StatisticsEffect.ShowSnackbar("데이터를 불러오지 못했습니다."))
        }
    }

    private suspend fun loadYearlyStats(now: LocalDate) {
        val startDate = now.withDayOfYear(1)
        val endDate = now.withDayOfYear(now.lengthOfYear())

        getCommitsByDateRangeUseCase(startDate, endDate).onSuccess { commits ->
            // 연간 히트맵 (365일)
            val heatmap = (1..now.lengthOfYear()).map { day ->
                val date = now.withDayOfYear(day)
                val count = commits.count { it.date == date }
                count.coerceAtMost(4)
            }

            // 차트 데이터 (월별 커밋 수)
            val monthlyCounts = (1..12).map { month ->
                commits.count { it.date.monthValue == month }
            }

            yearlyChartModelProducer.runTransaction {
                lineSeries {
                    series(monthlyCounts)
                }
            }

            // 태그 기반 스킬 성장 데이터
            val tagCounts = commits.flatMap { it.tags }.groupingBy { it.value }.eachCount()
            val totalTags = tagCounts.values.sum()
            // 레이더 차트를 위해 3~8개의 스킬 속성 제공
            val skills = tagCounts.entries.map { (tag, count) ->
                Skill(
                    name = tag,
                    score = if (totalTags > 0) count.toFloat() / totalTags else 0f
                )
            }.sortedByDescending { it.score }.take(8).let {
                // 다각형을 올바르게 그리기 위해 최소 3개의 요소가 필요, 부족하면 더미 추가
                val padded = it.toMutableList()
                while (padded.size < 3) {
                    padded.add(Skill("Skill ${padded.size + 1}", 0f))
                }
                padded
            }

            // 가장 바빴던 달 계산
            val busiestMonthVal = monthlyCounts.indexOfMax()?.plus(1) ?: 0

            _uiState.update { state ->
                state.copy(
                    yearlyHeatmapData = heatmap,
                    yearlyStats = state.yearlyStats.copy(
                        totalRecords = commits.size,
                        busiestMonth = "${busiestMonthVal}월",
                        skillGrowth = skills
                    )
                )
            }
        }.onFailure {
            _effect.emit(StatisticsEffect.ShowSnackbar("데이터를 불러오지 못했습니다."))
        }
    }

    // 확장 함수
    private fun List<Int>.indexOfMax(): Int? {
        if (isEmpty()) return null
        var maxIndex = 0
        for (i in 1 until size) {
            if (this[i] > this[maxIndex]) maxIndex = i
        }
        return maxIndex
    }
}


data class StatisticsUiState(
    val loadingPeriods: Set<StatsPeriod> = emptySet(),
    val loadedPeriods: Set<StatsPeriod> = emptySet(),
    val selectedPeriod: StatsPeriod = StatsPeriod.WEEKLY,
    val weeklyStats: WeeklyStats = WeeklyStats(),
    val monthlyStats: MonthlyStats = MonthlyStats(),
    val yearlyStats: YearlyStats = YearlyStats(),
    val yearlyHeatmapData: List<Int> = emptyList()
) {
    val isLoading: Boolean get() = loadingPeriods.isNotEmpty()
}

sealed interface StatisticsEffect {
    data class ShowSnackbar(val message: String) : StatisticsEffect
}
