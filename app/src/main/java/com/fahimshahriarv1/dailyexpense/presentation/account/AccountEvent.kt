package com.fahimshahriarv1.dailyexpense.presentation.account

import com.fahimshahriarv1.dailyexpense.domain.model.Account
import com.fahimshahriarv1.dailyexpense.domain.model.Income
import com.fahimshahriarv1.dailyexpense.domain.model.Transfer

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

    // Income events
    data class StartAddIncome(val account: Account) : AccountEvent()
    data class StartEditIncome(val income: Income) : AccountEvent()
    data class IncomeAmountChanged(val amount: String) : AccountEvent()
    data class IncomeSourceChanged(val source: String) : AccountEvent()
    data class IncomeNoteChanged(val note: String) : AccountEvent()
    data class IncomeDateChanged(val date: Long) : AccountEvent()
    data class IncomeAccountSelected(val accountUuid: String) : AccountEvent()
    data object ConfirmAddIncome : AccountEvent()
    data object ConfirmUpdateIncome : AccountEvent()
    data class DeleteIncome(val income: Income) : AccountEvent()
    data object DismissIncomeSheet : AccountEvent()

    // Transfer events
    data class StartTransfer(val account: Account) : AccountEvent()
    data class TransferAmountChanged(val amount: String) : AccountEvent()
    data class TransferToAccountSelected(val accountUuid: String) : AccountEvent()
    data class TransferNoteChanged(val note: String) : AccountEvent()
    data object ConfirmTransfer : AccountEvent()
    data object DismissTransferSheet : AccountEvent()
    data class DeleteTransfer(val transfer: Transfer) : AccountEvent()
}
