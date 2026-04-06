package com.fahimshahriarv1.dailyexpense.domain.repository

import com.fahimshahriarv1.dailyexpense.domain.model.Income
import kotlinx.coroutines.flow.Flow

interface IncomeRepository {
    fun getAllIncomes(): Flow<List<Income>>
    fun getIncomesBetween(start: Long, end: Long): Flow<List<Income>>
    suspend fun addIncome(income: Income)
    suspend fun updateIncome(income: Income)
    suspend fun deleteIncome(income: Income)
    suspend fun syncFromRemote()
    suspend fun syncToRemote()
}
