package com.example.dailyexpense.presentation.account

import com.example.dailyexpense.domain.model.Account

data class AccountState(
    val accounts: List<Account> = emptyList(),
    val name: String = "",
    val type: String = "Cash",
    val balance: String = "",
    val isLoading: Boolean = true,
    val showAddDialog: Boolean = false,
    val accountTypes: List<String> = listOf("Cash", "Bank", "MFS", "Other")
)

sealed class AccountEffect {
    data class ShowSnackbar(val message: String) : AccountEffect()
}
