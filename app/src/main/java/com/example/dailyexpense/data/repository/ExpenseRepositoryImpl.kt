package com.example.dailyexpense.data.repository

import com.example.dailyexpense.data.local.dao.ExpenseDao
import com.example.dailyexpense.data.local.entity.toDomain
import com.example.dailyexpense.data.local.entity.toEntity
import com.example.dailyexpense.domain.model.Expense
import com.example.dailyexpense.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao
) : ExpenseRepository {

    override fun getAllExpenses(): Flow<List<Expense>> {
        return expenseDao.getAllExpenses().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getExpensesBetween(start: Long, end: Long): Flow<List<Expense>> {
        return expenseDao.getExpensesBetween(start, end).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addExpense(expense: Expense) {
        expenseDao.insert(expense.toEntity())
    }

    override suspend fun deleteExpense(expense: Expense) {
        expenseDao.delete(expense.toEntity())
    }
}
