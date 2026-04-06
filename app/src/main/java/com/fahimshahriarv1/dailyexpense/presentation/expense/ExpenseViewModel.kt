package com.fahimshahriarv1.dailyexpense.presentation.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fahimshahriarv1.dailyexpense.domain.model.Expense
import com.fahimshahriarv1.dailyexpense.domain.usecase.account.GetAccountsUseCase
import com.fahimshahriarv1.dailyexpense.domain.usecase.expense.AddExpenseUseCase
import com.fahimshahriarv1.dailyexpense.domain.usecase.expense.DeleteExpenseUseCase
import com.fahimshahriarv1.dailyexpense.domain.usecase.expense.GetExpensesUseCase
import com.fahimshahriarv1.dailyexpense.domain.usecase.expense.UpdateExpenseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val getExpensesUseCase: GetExpensesUseCase,
    private val getAccountsUseCase: GetAccountsUseCase,
    private val addExpenseUseCase: AddExpenseUseCase,
    private val deleteExpenseUseCase: DeleteExpenseUseCase,
    private val updateExpenseUseCase: UpdateExpenseUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ExpenseState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ExpenseEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        loadData()
    }

    fun onEvent(event: ExpenseEvent) {
        when (event) {
            is ExpenseEvent.AmountChanged -> _state.update { it.copy(amount = event.amount) }
            is ExpenseEvent.CategoryChanged -> _state.update { it.copy(category = event.category) }
            is ExpenseEvent.NoteChanged -> _state.update { it.copy(note = event.note) }
            is ExpenseEvent.AccountSelected -> _state.update { it.copy(selectedAccountId = event.accountId) }
            is ExpenseEvent.DateChanged -> _state.update { it.copy(selectedDate = event.date) }
            is ExpenseEvent.AddExpense -> addExpense()
            is ExpenseEvent.UpdateExpense -> updateExpense()
            is ExpenseEvent.DeleteExpense -> deleteExpense(event.expense)
            is ExpenseEvent.StartEdit -> startEdit(event.expense)
            is ExpenseEvent.ToggleAddSheet -> _state.update {
                if (it.showAddSheet) {
                    // Closing sheet — clear form
                    it.copy(showAddSheet = false, editingExpense = null, amount = "", category = "", note = "", selectedAccountId = -1L, selectedDate = System.currentTimeMillis())
                } else {
                    it.copy(showAddSheet = true)
                }
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                getExpensesUseCase(),
                getAccountsUseCase()
            ) { expenses, accounts ->
                val accountMap = accounts.associateBy { it.id }
                val enrichedExpenses = expenses.map { expense ->
                    expense.copy(accountName = accountMap[expense.accountId]?.name ?: "Unknown")
                }
                Pair(enrichedExpenses, accounts)
            }.collect { (expenses, accounts) ->
                _state.update {
                    it.copy(
                        expenses = expenses,
                        accounts = accounts,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun startEdit(expense: Expense) {
        _state.update {
            it.copy(
                editingExpense = expense,
                amount = expense.amount.toBigDecimal().stripTrailingZeros().toPlainString(),
                category = expense.category,
                note = expense.note,
                selectedAccountId = expense.accountId,
                selectedDate = expense.date,
                showAddSheet = true
            )
        }
    }

    private fun addExpense() {
        val currentState = _state.value
        val amount = currentState.amount.toDoubleOrNull()

        if (amount == null || amount <= 0) {
            viewModelScope.launch { _effect.send(ExpenseEffect.ShowSnackbar("Enter a valid amount")) }
            return
        }
        if (currentState.category.isEmpty()) {
            viewModelScope.launch { _effect.send(ExpenseEffect.ShowSnackbar("Select a category")) }
            return
        }
        if (currentState.selectedAccountId == -1L) {
            viewModelScope.launch { _effect.send(ExpenseEffect.ShowSnackbar("Select an account")) }
            return
        }

        viewModelScope.launch {
            try {
                addExpenseUseCase(
                    Expense(
                        amount = amount,
                        category = currentState.category,
                        note = currentState.note,
                        date = currentState.selectedDate,
                        accountId = currentState.selectedAccountId
                    )
                )
                _state.update {
                    it.copy(
                        amount = "",
                        category = "",
                        note = "",
                        selectedAccountId = -1L,
                        selectedDate = System.currentTimeMillis(),
                        showAddSheet = false
                    )
                }
                _effect.send(ExpenseEffect.ShowSnackbar("Expense added"))
            } catch (e: Exception) {
                _effect.send(ExpenseEffect.ShowSnackbar("Failed to add expense"))
            }
        }
    }

    private fun updateExpense() {
        val currentState = _state.value
        val oldExpense = currentState.editingExpense ?: return
        val amount = currentState.amount.toDoubleOrNull()

        if (amount == null || amount <= 0) {
            viewModelScope.launch { _effect.send(ExpenseEffect.ShowSnackbar("Enter a valid amount")) }
            return
        }
        if (currentState.category.isEmpty()) {
            viewModelScope.launch { _effect.send(ExpenseEffect.ShowSnackbar("Select a category")) }
            return
        }
        if (currentState.selectedAccountId == -1L) {
            viewModelScope.launch { _effect.send(ExpenseEffect.ShowSnackbar("Select an account")) }
            return
        }

        val newExpense = oldExpense.copy(
            amount = amount,
            category = currentState.category,
            note = currentState.note,
            date = currentState.selectedDate,
            accountId = currentState.selectedAccountId
        )

        viewModelScope.launch {
            try {
                updateExpenseUseCase(oldExpense, newExpense)
                _state.update {
                    it.copy(
                        amount = "",
                        category = "",
                        note = "",
                        selectedAccountId = -1L,
                        selectedDate = System.currentTimeMillis(),
                        showAddSheet = false,
                        editingExpense = null
                    )
                }
                _effect.send(ExpenseEffect.ShowSnackbar("Expense updated"))
            } catch (e: Exception) {
                _effect.send(ExpenseEffect.ShowSnackbar("Failed to update expense"))
            }
        }
    }

    private fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            try {
                deleteExpenseUseCase(expense)
                _effect.send(ExpenseEffect.ShowSnackbar("Expense deleted"))
            } catch (e: Exception) {
                _effect.send(ExpenseEffect.ShowSnackbar("Failed to delete expense"))
            }
        }
    }
}
