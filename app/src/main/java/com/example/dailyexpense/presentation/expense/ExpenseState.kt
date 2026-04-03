package com.example.dailyexpense.presentation.expense

import com.example.dailyexpense.domain.model.Account
import com.example.dailyexpense.domain.model.Expense

data class ExpenseState(
    val expenses: List<Expense> = emptyList(),
    val accounts: List<Account> = emptyList(),
    val amount: String = "",
    val category: String = "",
    val note: String = "",
    val selectedAccountId: Long = -1L,
    val selectedDate: Long = System.currentTimeMillis(),
    val isLoading: Boolean = true,
    val showAddSheet: Boolean = false,
    val categories: List<String> = listOf(
        "Food", "Transport", "Shopping", "Bills",
        "Entertainment", "Health", "Education", "Other"
    )
)

sealed class ExpenseEffect {
    data class ShowSnackbar(val message: String) : ExpenseEffect()
}
