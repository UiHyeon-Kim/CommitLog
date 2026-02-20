package com.hanhyo.commitlog.presentation.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class StatisticsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    // Vico 2.x Producer - use constructor
    val weeklyChartModelProducer = CartesianChartModelProducer()
    val yearlyChartModelProducer = CartesianChartModelProducer()

    // Monthly Heatmap Data (Day 1-31, 0=None, 1-4=Level)
    val monthlyHeatmapData = List(31) { Random.nextInt(0, 5) }

    init {
        // Initialize Dummy Data
        viewModelScope.launch {
            weeklyChartModelProducer.runTransaction {
                columnSeries {
                    series(3, 5, 2, 0, 4, 1, 3)
                }
            }

            yearlyChartModelProducer.runTransaction {
                lineSeries {
                    series(15, 22, 35, 18, 48, 31, 12, 25, 39, 44, 28, 15)
                }
            }
        }
    }

    fun updatePeriod(period: StatsPeriod) {
        _uiState.update { it.copy(selectedPeriod = period) }
    }
}

data class StatisticsUiState(
    val isLoading: Boolean = false,
    val selectedPeriod: StatsPeriod = StatsPeriod.WEEKLY,
)

enum class StatsPeriod(val title: String) {
    WEEKLY("주간"),
    MONTHLY("월간"),
    YEARLY("연간")
}
