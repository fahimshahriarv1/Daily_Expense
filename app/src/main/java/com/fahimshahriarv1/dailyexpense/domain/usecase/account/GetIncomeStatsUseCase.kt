package com.fahimshahriarv1.dailyexpense.domain.usecase.account

import com.fahimshahriarv1.dailyexpense.domain.model.ExpenseStats
import com.fahimshahriarv1.dailyexpense.domain.repository.IncomeRepository
import com.fahimshahriarv1.dailyexpense.presentation.stats.StatsPeriod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class GetIncomeStatsUseCase @Inject constructor(
    private val repository: IncomeRepository
) {
    operator fun invoke(period: StatsPeriod): Flow<List<ExpenseStats>> {
        val entries = buildEntries(period)
        val start = entries.first().first
        val end = entries.last().second

        return repository.getIncomesBetween(start, end).map { incomes ->
            entries.map { (rangeStart, rangeEnd, label) ->
                val total = incomes
                    .filter { it.date in rangeStart until rangeEnd }
                    .sumOf { it.amount }
                ExpenseStats(label = label, totalAmount = total)
            }
        }
    }

    private fun buildEntries(period: StatsPeriod): List<Triple<Long, Long, String>> {
        return when (period) {
            StatsPeriod.WEEKLY -> buildWeeklyEntries()
            StatsPeriod.MONTHLY -> buildMonthlyEntries()
            StatsPeriod.YEARLY -> buildYearlyEntries()
        }
    }

    private fun buildWeeklyEntries(): List<Triple<Long, Long, String>> {
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        return (6 downTo 0).map { daysAgo ->
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -daysAgo)
            val dayStart = startOfDay(cal)
            val dayEnd = dayStart + 86_400_000L
            Triple(dayStart, dayEnd, dayFormat.format(cal.time))
        }
    }

    private fun buildMonthlyEntries(): List<Triple<Long, Long, String>> {
        val weekFormat = SimpleDateFormat("MMM d", Locale.getDefault())
        return (3 downTo 0).map { weeksAgo ->
            val calStart = Calendar.getInstance()
            calStart.add(Calendar.WEEK_OF_YEAR, -weeksAgo)
            calStart.set(Calendar.DAY_OF_WEEK, calStart.firstDayOfWeek)
            val weekStart = startOfDay(calStart)

            val calEnd = calStart.clone() as Calendar
            calEnd.add(Calendar.DAY_OF_YEAR, 7)
            val weekEnd = startOfDay(calEnd)

            Triple(weekStart, weekEnd, weekFormat.format(calStart.time))
        }
    }

    private fun buildYearlyEntries(): List<Triple<Long, Long, String>> {
        val monthFormat = SimpleDateFormat("MMM", Locale.getDefault())
        return (11 downTo 0).map { monthsAgo ->
            val calStart = Calendar.getInstance()
            calStart.add(Calendar.MONTH, -monthsAgo)
            calStart.set(Calendar.DAY_OF_MONTH, 1)
            val monthStart = startOfDay(calStart)

            val calEnd = calStart.clone() as Calendar
            calEnd.add(Calendar.MONTH, 1)
            val monthEnd = startOfDay(calEnd)

            Triple(monthStart, monthEnd, monthFormat.format(calStart.time))
        }
    }

    private fun startOfDay(cal: Calendar): Long {
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
