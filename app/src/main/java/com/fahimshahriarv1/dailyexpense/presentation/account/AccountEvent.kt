package com.fahimshahriarv1.dailyexpense.presentation.account

import com.fahimshahriarv1.dailyexpense.domain.model.Account

sealed class AccountEvent {
    data class NameChanged(val name: String) : AccountEvent()
    data class TypeChanged(val type: String) : AccountEvent()
    data class BalanceChanged(val balance: String) : AccountEvent()
    data object AddAccount : AccountEvent()
    data class DeleteAccount(val account: Account) : AccountEvent()
    data object ToggleAddDialog : AccountEvent()
    data class SignInWithToken(val idToken: String) : AccountEvent()
    data class SignInFailed(val message: String) : AccountEvent()
    data object SignOut : AccountEvent()
    data class StartAddIncome(val account: Account) : AccountEvent()
    data class IncomeAmountChanged(val amount: String) : AccountEvent()
    data object ConfirmAddIncome : AccountEvent()
    data object DismissIncomeDialog : AccountEvent()
}
