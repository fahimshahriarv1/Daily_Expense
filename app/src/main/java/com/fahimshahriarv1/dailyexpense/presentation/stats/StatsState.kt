package com.fahimshahriarv1.dailyexpense.presentation.stats

import com.fahimshahriarv1.dailyexpense.domain.model.ExpenseStats

data class StatsState(
    val period: StatsPeriod = StatsPeriod.WEEKLY,
    val expenseStats: List<ExpenseStats> = emptyList(),
    val incomeStats: List<ExpenseStats> = emptyList(),
    val totalExpense: Double = 0.0,
    val totalIncome: Double = 0.0,
    val isLoading: Boolean = true
)
