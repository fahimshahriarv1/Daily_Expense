package com.fahimshahriarv1.dailyexpense.data.repository

import android.util.Log
import com.fahimshahriarv1.dailyexpense.data.local.dao.ExpenseDao
import com.fahimshahriarv1.dailyexpense.data.local.entity.toDomain
import com.fahimshahriarv1.dailyexpense.data.local.entity.toEntity
import com.fahimshahriarv1.dailyexpense.data.remote.FirestoreExpenseDataSource
import com.fahimshahriarv1.dailyexpense.domain.model.Expense
import com.fahimshahriarv1.dailyexpense.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val firestoreDataSource: FirestoreExpenseDataSource
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
        try {
            firestoreDataSource.upsert(expense)
        } catch (e: Exception) {
            Log.w("ExpenseRepo", "Firestore sync failed for add", e)
        }
    }

    override suspend fun updateExpense(expense: Expense) {
        expenseDao.update(expense.toEntity())
        try {
            firestoreDataSource.upsert(expense)
        } catch (e: Exception) {
            Log.w("ExpenseRepo", "Firestore sync failed for update", e)
        }
    }

    override suspend fun deleteExpense(expense: Expense) {
        expenseDao.delete(expense.toEntity())
        try {
            firestoreDataSource.delete(expense.uuid)
        } catch (e: Exception) {
            Log.w("ExpenseRepo", "Firestore sync failed for delete", e)
        }
    }

    override suspend fun syncFromRemote() {
        try {
            val remoteExpenses = firestoreDataSource.getAll()
            remoteExpenses.forEach { remote ->
                val existing = expenseDao.getByUuid(remote.uuid)
                if (existing == null) {
                    expenseDao.insert(remote.toEntity().copy(id = 0))
                }
            }
        } catch (e: Exception) {
            Log.w("ExpenseRepo", "Firestore pull failed", e)
        }
    }

    override suspend fun syncToRemote() {
        try {
            val localExpenses = expenseDao.getAllExpensesOnce()
            localExpenses.forEach { entity ->
                firestoreDataSource.upsert(entity.toDomain())
            }
        } catch (e: Exception) {
            Log.w("ExpenseRepo", "Firestore push failed", e)
        }
    }
}
