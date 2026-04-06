package com.fahimshahriarv1.dailyexpense.data.repository

import android.util.Log
import com.fahimshahriarv1.dailyexpense.data.local.dao.AccountDao
import com.fahimshahriarv1.dailyexpense.data.local.entity.toDomain
import com.fahimshahriarv1.dailyexpense.data.local.entity.toEntity
import com.fahimshahriarv1.dailyexpense.data.remote.FirestoreAccountDataSource
import com.fahimshahriarv1.dailyexpense.domain.model.Account
import com.fahimshahriarv1.dailyexpense.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val accountDao: AccountDao,
    private val firestoreDataSource: FirestoreAccountDataSource
) : AccountRepository {

    override fun getAllAccounts(): Flow<List<Account>> {
        return accountDao.getAllAccounts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getAccountById(id: Long): Account? {
        return accountDao.getById(id)?.toDomain()
    }

    override suspend fun addAccount(account: Account) {
        accountDao.insert(account.toEntity())
        try {
            firestoreDataSource.upsert(account)
        } catch (e: Exception) {
            Log.w("AccountRepo", "Firestore sync failed for add", e)
        }
    }

    override suspend fun updateAccount(account: Account) {
        accountDao.update(account.toEntity())
        try {
            firestoreDataSource.upsert(account)
        } catch (e: Exception) {
            Log.w("AccountRepo", "Firestore sync failed for update", e)
        }
    }

    override suspend fun deleteAccount(account: Account) {
        accountDao.delete(account.toEntity())
        try {
            firestoreDataSource.delete(account.uuid)
        } catch (e: Exception) {
            Log.w("AccountRepo", "Firestore sync failed for delete", e)
        }
    }

    override suspend fun deductBalance(id: Long, amount: Double) {
        accountDao.deductBalance(id, amount)
        try {
            val account = accountDao.getById(id)
            if (account != null) {
                firestoreDataSource.updateBalance(account.uuid, account.balance)
            }
        } catch (e: Exception) {
            Log.w("AccountRepo", "Firestore balance sync failed", e)
        }
    }

    override suspend fun syncFromRemote() {
        try {
            val remoteAccounts = firestoreDataSource.getAll()
            remoteAccounts.forEach { remote ->
                val existing = accountDao.getByUuid(remote.uuid)
                if (existing == null) {
                    accountDao.insert(remote.toEntity().copy(id = 0))
                }
            }
        } catch (e: Exception) {
            Log.w("AccountRepo", "Firestore pull failed", e)
        }
    }

    override suspend fun syncToRemote() {
        try {
            val localAccounts = accountDao.getAllAccountsOnce()
            localAccounts.forEach { entity ->
                firestoreDataSource.upsert(entity.toDomain())
            }
        } catch (e: Exception) {
            Log.w("AccountRepo", "Firestore push failed", e)
        }
    }
}
