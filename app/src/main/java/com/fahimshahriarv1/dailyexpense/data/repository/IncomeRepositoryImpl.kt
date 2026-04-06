package com.fahimshahriarv1.dailyexpense.data.repository

import android.util.Log
import com.fahimshahriarv1.dailyexpense.data.local.dao.IncomeDao
import com.fahimshahriarv1.dailyexpense.data.local.entity.toDomain
import com.fahimshahriarv1.dailyexpense.data.local.entity.toEntity
import com.fahimshahriarv1.dailyexpense.data.remote.FirestoreIncomeDataSource
import com.fahimshahriarv1.dailyexpense.domain.model.Income
import com.fahimshahriarv1.dailyexpense.domain.repository.IncomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class IncomeRepositoryImpl @Inject constructor(
    private val incomeDao: IncomeDao,
    private val firestoreDataSource: FirestoreIncomeDataSource
) : IncomeRepository {

    override fun getAllIncomes(): Flow<List<Income>> {
        return incomeDao.getAllIncomes().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getIncomesBetween(start: Long, end: Long): Flow<List<Income>> {
        return incomeDao.getIncomesBetween(start, end).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addIncome(income: Income) {
        incomeDao.insert(income.toEntity())
        try {
            firestoreDataSource.upsert(income)
        } catch (e: Exception) {
            Log.w("IncomeRepo", "Firestore sync failed for add", e)
        }
    }

    override suspend fun updateIncome(income: Income) {
        incomeDao.update(income.toEntity())
        try {
            firestoreDataSource.upsert(income)
        } catch (e: Exception) {
            Log.w("IncomeRepo", "Firestore sync failed for update", e)
        }
    }

    override suspend fun deleteIncome(income: Income) {
        incomeDao.delete(income.toEntity())
        try {
            firestoreDataSource.delete(income.uuid)
        } catch (e: Exception) {
            Log.w("IncomeRepo", "Firestore sync failed for delete", e)
        }
    }

    override suspend fun syncFromRemote() {
        try {
            val remoteIncomes = firestoreDataSource.getAll()
            remoteIncomes.forEach { remote ->
                val existing = incomeDao.getByUuid(remote.uuid)
                if (existing == null) {
                    incomeDao.insert(remote.toEntity().copy(id = 0))
                }
            }
        } catch (e: Exception) {
            Log.w("IncomeRepo", "Firestore pull failed", e)
        }
    }

    override suspend fun syncToRemote() {
        try {
            val localIncomes = incomeDao.getAllIncomesOnce()
            localIncomes.forEach { entity ->
                firestoreDataSource.upsert(entity.toDomain())
            }
        } catch (e: Exception) {
            Log.w("IncomeRepo", "Firestore push failed", e)
        }
    }
}
