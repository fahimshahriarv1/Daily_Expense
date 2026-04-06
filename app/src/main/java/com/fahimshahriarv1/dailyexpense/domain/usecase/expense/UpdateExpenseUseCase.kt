package com.fahimshahriarv1.dailyexpense.domain.usecase.expense

import com.fahimshahriarv1.dailyexpense.domain.model.Expense
import com.fahimshahriarv1.dailyexpense.domain.repository.AccountRepository
import com.fahimshahriarv1.dailyexpense.domain.repository.ExpenseRepository
import javax.inject.Inject

class UpdateExpenseUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(oldExpense: Expense, newExpense: Expense) {
        // Restore old amount to old account
        accountRepository.deductBalance(oldExpense.accountId, -oldExpense.amount)
        // Deduct new amount from new account
        accountRepository.deductBalance(newExpense.accountId, newExpense.amount)
        expenseRepository.updateExpense(newExpense)
    }
}
