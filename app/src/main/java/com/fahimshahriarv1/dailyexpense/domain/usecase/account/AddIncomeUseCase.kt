package com.fahimshahriarv1.dailyexpense.domain.usecase.account

import com.fahimshahriarv1.dailyexpense.domain.model.Income
import com.fahimshahriarv1.dailyexpense.domain.repository.AccountRepository
import com.fahimshahriarv1.dailyexpense.domain.repository.IncomeRepository
import javax.inject.Inject

class AddIncomeUseCase @Inject constructor(
    private val incomeRepository: IncomeRepository,
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(income: Income) {
        incomeRepository.addIncome(income)
        val account = accountRepository.getAccountByUuid(income.accountUuid) ?: return
        accountRepository.updateAccount(account.copy(balance = account.balance + income.amount))
    }
}
