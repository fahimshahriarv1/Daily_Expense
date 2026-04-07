package com.fahimshahriarv1.dailyexpense.presentation.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fahimshahriarv1.dailyexpense.domain.usecase.account.GetIncomeStatsUseCase
import com.fahimshahriarv1.dailyexpense.domain.usecase.expense.GetExpenseStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val getExpenseStatsUseCase: GetExpenseStatsUseCase,
    private val getIncomeStatsUseCase: GetIncomeStatsUseCase
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
            combine(
                getExpenseStatsUseCase(_state.value.period),
                getIncomeStatsUseCase(_state.value.period)
            ) { expenseStats, incomeStats ->
                Pair(expenseStats, incomeStats)
            }.collect { (expenseStats, incomeStats) ->
                _state.update {
                    it.copy(
                        expenseStats = expenseStats,
                        incomeStats = incomeStats,
                        totalExpense = expenseStats.sumOf { s -> s.totalAmount },
                        totalIncome = incomeStats.sumOf { s -> s.totalAmount },
                        isLoading = false
                    )
                }
            }
        }
    }
}
