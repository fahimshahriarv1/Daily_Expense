package com.example.dailyexpense.domain.usecase.expense

import com.example.dailyexpense.domain.model.Expense
import com.example.dailyexpense.domain.repository.AccountRepository
import com.example.dailyexpense.domain.repository.ExpenseRepository
import javax.inject.Inject

class AddExpenseUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(expense: Expense) {
        expenseRepository.addExpense(expense)
        accountRepository.deductBalance(expense.accountId, expense.amount)
    }
}
