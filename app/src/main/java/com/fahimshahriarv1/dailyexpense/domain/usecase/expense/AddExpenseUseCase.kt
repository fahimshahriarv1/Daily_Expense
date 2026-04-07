package com.fahimshahriarv1.dailyexpense.domain.usecase.expense

import com.fahimshahriarv1.dailyexpense.domain.model.Expense
import com.fahimshahriarv1.dailyexpense.domain.repository.AccountRepository
import com.fahimshahriarv1.dailyexpense.domain.repository.ExpenseRepository
import javax.inject.Inject

class AddExpenseUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(expense: Expense) {
        expenseRepository.addExpense(expense)
        val account = accountRepository.getAccountById(expense.accountId) ?: return
        accountRepository.updateAccount(account.copy(balance = account.balance - expense.amount))
    }
}
