package com.example.dailyexpense.domain.usecase.account

import com.example.dailyexpense.domain.model.Account
import com.example.dailyexpense.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAccountsUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    operator fun invoke(): Flow<List<Account>> = repository.getAllAccounts()
}
