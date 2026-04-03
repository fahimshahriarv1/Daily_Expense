package com.example.dailyexpense.domain.usecase.expense

import com.example.dailyexpense.domain.model.Expense
import com.example.dailyexpense.domain.repository.AccountRepository
import com.example.dailyexpense.domain.repository.ExpenseRepository
import javax.inject.Inject

class DeleteExpenseUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(expense: Expense) {
        expenseRepository.deleteExpense(expense)
        accountRepository.deductBalance(expense.accountId, -expense.amount)
    }
}
