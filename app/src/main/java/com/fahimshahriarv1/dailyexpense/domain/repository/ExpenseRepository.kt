package com.fahimshahriarv1.dailyexpense.domain.repository

import com.fahimshahriarv1.dailyexpense.domain.model.Expense
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun getAllExpenses(): Flow<List<Expense>>
    fun getExpensesBetween(start: Long, end: Long): Flow<List<Expense>>
    suspend fun addExpense(expense: Expense)
    suspend fun updateExpense(expense: Expense)
    suspend fun deleteExpense(expense: Expense)
    suspend fun syncFromRemote()
    suspend fun syncToRemote()
}
