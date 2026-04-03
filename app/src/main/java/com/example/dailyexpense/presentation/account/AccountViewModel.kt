package com.example.dailyexpense.presentation.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyexpense.domain.model.Account
import com.example.dailyexpense.domain.usecase.account.AddAccountUseCase
import com.example.dailyexpense.domain.usecase.account.DeleteAccountUseCase
import com.example.dailyexpense.domain.usecase.account.GetAccountsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val getAccountsUseCase: GetAccountsUseCase,
    private val addAccountUseCase: AddAccountUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AccountState())
    val state = _state.asStateFlow()

    private val _effect = Channel<AccountEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        loadAccounts()
    }

    fun onEvent(event: AccountEvent) {
        when (event) {
            is AccountEvent.NameChanged -> _state.update { it.copy(name = event.name) }
            is AccountEvent.TypeChanged -> _state.update { it.copy(type = event.type) }
            is AccountEvent.BalanceChanged -> _state.update { it.copy(balance = event.balance) }
            is AccountEvent.AddAccount -> addAccount()
            is AccountEvent.DeleteAccount -> deleteAccount(event.account)
            is AccountEvent.ToggleAddDialog -> _state.update { it.copy(showAddDialog = !it.showAddDialog) }
        }
    }

    private fun loadAccounts() {
        viewModelScope.launch {
            getAccountsUseCase().collect { accounts ->
                _state.update { it.copy(accounts = accounts, isLoading = false) }
            }
        }
    }

    private fun addAccount() {
        val currentState = _state.value
        if (currentState.name.isBlank()) {
            viewModelScope.launch { _effect.send(AccountEffect.ShowSnackbar("Enter account name")) }
            return
        }

        val balance = currentState.balance.toDoubleOrNull() ?: 0.0

        viewModelScope.launch {
            try {
                addAccountUseCase(
                    Account(
                        name = currentState.name,
                        type = currentState.type,
                        balance = balance
                    )
                )
                _state.update {
                    it.copy(
                        name = "",
                        type = "Cash",
                        balance = "",
                        showAddDialog = false
                    )
                }
                _effect.send(AccountEffect.ShowSnackbar("Account added"))
            } catch (e: Exception) {
                _effect.send(AccountEffect.ShowSnackbar("Failed to add account"))
            }
        }
    }

    private fun deleteAccount(account: Account) {
        viewModelScope.launch {
            try {
                deleteAccountUseCase(account)
                _effect.send(AccountEffect.ShowSnackbar("Account deleted"))
            } catch (e: Exception) {
                _effect.send(AccountEffect.ShowSnackbar("Failed to delete account"))
            }
        }
    }
}
