package com.fahimshahriarv1.dailyexpense.domain.usecase.account

import com.fahimshahriarv1.dailyexpense.domain.model.Income
import com.fahimshahriarv1.dailyexpense.domain.repository.AccountRepository
import com.fahimshahriarv1.dailyexpense.domain.repository.IncomeRepository
import javax.inject.Inject

class DeleteIncomeUseCase @Inject constructor(
    private val incomeRepository: IncomeRepository,
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(income: Income) {
        incomeRepository.deleteIncome(income)
        accountRepository.deductBalance(income.accountId, income.amount)
    }
}
