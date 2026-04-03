package com.example.dailyexpense.presentation.account

import com.example.dailyexpense.domain.model.Account

sealed class AccountEvent {
    data class NameChanged(val name: String) : AccountEvent()
    data class TypeChanged(val type: String) : AccountEvent()
    data class BalanceChanged(val balance: String) : AccountEvent()
    data object AddAccount : AccountEvent()
    data class DeleteAccount(val account: Account) : AccountEvent()
    data object ToggleAddDialog : AccountEvent()
}
