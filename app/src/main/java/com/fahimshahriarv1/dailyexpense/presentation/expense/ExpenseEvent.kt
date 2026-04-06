package com.fahimshahriarv1.dailyexpense.presentation.expense

import com.fahimshahriarv1.dailyexpense.domain.model.Expense

sealed class ExpenseEvent {
    data class AmountChanged(val amount: String) : ExpenseEvent()
    data class CategoryChanged(val category: String) : ExpenseEvent()
    data class NoteChanged(val note: String) : ExpenseEvent()
    data class AccountSelected(val accountId: Long) : ExpenseEvent()
    data class DateChanged(val date: Long) : ExpenseEvent()
    data object AddExpense : ExpenseEvent()
    data object UpdateExpense : ExpenseEvent()
    data class DeleteExpense(val expense: Expense) : ExpenseEvent()
    data class StartEdit(val expense: Expense) : ExpenseEvent()
    data object ToggleAddSheet : ExpenseEvent()
}
