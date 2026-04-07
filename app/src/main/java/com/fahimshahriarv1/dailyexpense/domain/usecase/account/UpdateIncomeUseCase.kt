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
        val oldAccount = accountRepository.getAccountByUuid(oldIncome.accountUuid)
        if (oldAccount != null) {
            accountRepository.updateAccount(oldAccount.copy(balance = oldAccount.balance - oldIncome.amount))
        }
        // Add new income to new account
        val newAccount = accountRepository.getAccountByUuid(newIncome.accountUuid)
        if (newAccount != null) {
            accountRepository.updateAccount(newAccount.copy(balance = newAccount.balance + newIncome.amount))
        }
        incomeRepository.updateIncome(newIncome)
    }
}
