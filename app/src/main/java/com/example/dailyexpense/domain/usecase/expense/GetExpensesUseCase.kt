package com.example.dailyexpense.domain.usecase.expense

import com.example.dailyexpense.domain.model.Expense
import com.example.dailyexpense.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetExpensesUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    operator fun invoke(): Flow<List<Expense>> = repository.getAllExpenses()
}
