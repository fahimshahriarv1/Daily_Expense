package com.fahimshahriarv1.dailyexpense.domain.repository

import com.fahimshahriarv1.dailyexpense.domain.model.Account
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun getAllAccounts(): Flow<List<Account>>
    suspend fun getAccountById(id: Long): Account?
    suspend fun addAccount(account: Account)
    suspend fun updateAccount(account: Account)
    suspend fun deleteAccount(account: Account)
    suspend fun deductBalance(id: Long, amount: Double)
    suspend fun syncFromRemote()
    suspend fun syncToRemote()
}
