package com.example.dailyexpense.domain.usecase.account

import com.example.dailyexpense.domain.model.Account
import com.example.dailyexpense.domain.repository.AccountRepository
import javax.inject.Inject

class AddAccountUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    suspend operator fun invoke(account: Account) {
        repository.addAccount(account)
    }
}
