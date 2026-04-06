package com.fahimshahriarv1.dailyexpense.presentation.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fahimshahriarv1.dailyexpense.domain.usecase.expense.GetExpenseStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val getExpenseStatsUseCase: GetExpenseStatsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(StatsState())
    val state = _state.asStateFlow()

    private var statsJob: Job? = null

    init {
        loadStats()
    }

    fun onEvent(event: StatsEvent) {
        when (event) {
            is StatsEvent.PeriodChanged -> {
                _state.update { it.copy(period = event.period) }
                loadStats()
            }
        }
    }

    private fun loadStats() {
        statsJob?.cancel()
        statsJob = viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getExpenseStatsUseCase(_state.value.period).collect { stats ->
                _state.update {
                    it.copy(
                        stats = stats,
                        totalExpense = stats.sumOf { s -> s.totalAmount },
                        isLoading = false
                    )
                }
            }
        }
    }
}
