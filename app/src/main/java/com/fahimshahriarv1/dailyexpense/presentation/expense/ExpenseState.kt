package com.fahimshahriarv1.dailyexpense.presentation.expense

import com.fahimshahriarv1.dailyexpense.domain.model.Account
import com.fahimshahriarv1.dailyexpense.domain.model.Expense

data class ExpenseState(
    val expenses: List<Expense> = emptyList(),
    val accounts: List<Account> = emptyList(),
    val amount: String = "",
    val category: String = "",
    val note: String = "",
    val selectedAccountId: Long = -1L,
    val selectedDate: Long = System.currentTimeMillis(),
    val isLoading: Boolean = true,
    val isActionLoading: Boolean = false,
    val showAddSheet: Boolean = false,
    val editingExpense: Expense? = null,
    val categories: List<String> = listOf(
        "Food", "Transport", "Shopping", "Bills",
        "Entertainment", "Health", "Education", "Other"
    ),

    // Account filter (empty = show all)
    val selectedFilterAccountIds: Set<Long> = emptySet()
)

sealed class ExpenseEffect {
    data class ShowSnackbar(val message: String) : ExpenseEffect()
}
