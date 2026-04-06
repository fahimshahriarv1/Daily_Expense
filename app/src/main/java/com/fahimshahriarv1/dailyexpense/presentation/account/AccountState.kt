package com.fahimshahriarv1.dailyexpense.presentation.account

import com.fahimshahriarv1.dailyexpense.domain.model.Account
import com.fahimshahriarv1.dailyexpense.domain.model.User

data class AccountState(
    val accounts: List<Account> = emptyList(),
    val name: String = "",
    val type: String = "Cash",
    val balance: String = "",
    val isLoading: Boolean = true,
    val showAddDialog: Boolean = false,
    val accountTypes: List<String> = listOf("Cash", "Bank", "MFS", "Other"),
    val user: User? = null,
    val isSignedIn: Boolean = false,
    val isAuthLoading: Boolean = false,
    val authError: String? = null,
    val showIncomeDialog: Boolean = false,
    val incomeAmount: String = "",
    val incomeAccount: Account? = null
)

sealed class AccountEffect {
    data class ShowSnackbar(val message: String) : AccountEffect()
}
