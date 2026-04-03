package com.example.dailyexpense.data.repository

import com.example.dailyexpense.data.local.dao.AccountDao
import com.example.dailyexpense.data.local.entity.toDomain
import com.example.dailyexpense.data.local.entity.toEntity
import com.example.dailyexpense.domain.model.Account
import com.example.dailyexpense.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val accountDao: AccountDao
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
    }

    override suspend fun updateAccount(account: Account) {
        accountDao.update(account.toEntity())
    }

    override suspend fun deleteAccount(account: Account) {
        accountDao.delete(account.toEntity())
    }

    override suspend fun deductBalance(id: Long, amount: Double) {
        accountDao.deductBalance(id, amount)
    }
}
