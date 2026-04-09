package com.fahimshahriarv1.dailyexpense.presentation.account

import com.fahimshahriarv1.dailyexpense.domain.model.Account
import com.fahimshahriarv1.dailyexpense.domain.model.Income
import com.fahimshahriarv1.dailyexpense.domain.model.Transfer
import com.fahimshahriarv1.dailyexpense.domain.model.User

data class AccountState(
    val accounts: List<Account> = emptyList(),
    val incomes: List<Income> = emptyList(),
    val transfers: List<Transfer> = emptyList(),
    val name: String = "",
    val type: String = "Cash",
    val balance: String = "",
    val isLoading: Boolean = true,
    val isActionLoading: Boolean = false,
    val showAddDialog: Boolean = false,
    val accountTypes: List<String> = listOf("Cash", "Bank", "MFS", "Other"),
    val user: User? = null,
    val isSignedIn: Boolean = false,
    val isAuthLoading: Boolean = false,
    val authError: String? = null,

    // Income form
    val showIncomeSheet: Boolean = false,
    val editingIncome: Income? = null,
    val incomeAmount: String = "",
    val incomeSource: String = "",
    val incomeNote: String = "",
    val incomeDate: Long = System.currentTimeMillis(),
    val incomeAccountUuid: String = "",
    val incomeSources: List<String> = listOf(
        "Salary", "Freelance", "Business", "Investment", "Gift", "Other"
    ),

    // Transfer form
    val showTransferSheet: Boolean = false,
    val transferAmount: String = "",
    val transferFromAccountUuid: String = "",
    val transferToAccountUuid: String = "",
    val transferNote: String = ""
)

sealed class AccountEffect {
    data class ShowSnackbar(val message: String) : AccountEffect()
}
