package com.fahimshahriarv1.dailyexpense.domain.usecase.account

import com.fahimshahriarv1.dailyexpense.domain.model.Income
import com.fahimshahriarv1.dailyexpense.domain.repository.AccountRepository
import com.fahimshahriarv1.dailyexpense.domain.repository.IncomeRepository
import javax.inject.Inject

class UpdateIncomeUseCase @Inject constructor(
    private val incomeRepository: IncomeRepository,
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(oldIncome: Income, newIncome: Income) {
        // Reverse old income from old account
        accountRepository.deductBalance(oldIncome.accountId, oldIncome.amount)
        // Add new income to new account
        accountRepository.addBalance(newIncome.accountId, newIncome.amount)
        incomeRepository.updateIncome(newIncome)
    }
}
