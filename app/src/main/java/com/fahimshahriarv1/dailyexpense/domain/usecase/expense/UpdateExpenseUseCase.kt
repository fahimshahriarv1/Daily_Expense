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
        val oldAccount = accountRepository.getAccountById(oldExpense.accountId)
        if (oldAccount != null) {
            accountRepository.updateAccount(oldAccount.copy(balance = oldAccount.balance + oldExpense.amount))
        }
        // Deduct new amount from new account
        val newAccount = accountRepository.getAccountById(newExpense.accountId)
        if (newAccount != null) {
            accountRepository.updateAccount(newAccount.copy(balance = newAccount.balance - newExpense.amount))
        }
        expenseRepository.updateExpense(newExpense)
    }
}
