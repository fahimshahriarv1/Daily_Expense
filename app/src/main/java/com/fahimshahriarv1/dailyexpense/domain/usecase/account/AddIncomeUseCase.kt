package com.fahimshahriarv1.dailyexpense.domain.usecase.account

import com.fahimshahriarv1.dailyexpense.domain.repository.AccountRepository
import javax.inject.Inject

class AddIncomeUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(accountId: Long, amount: Double) {
        accountRepository.addBalance(accountId, amount)
    }
}
