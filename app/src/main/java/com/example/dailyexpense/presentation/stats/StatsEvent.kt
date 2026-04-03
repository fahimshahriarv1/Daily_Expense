package com.example.dailyexpense.presentation.stats

sealed class StatsEvent {
    data class PeriodChanged(val period: StatsPeriod) : StatsEvent()
}

enum class StatsPeriod {
    WEEKLY, MONTHLY, YEARLY
}
