package com.fahimshahriarv1.dailyexpense.presentation.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fahimshahriarv1.dailyexpense.domain.model.Account
import com.fahimshahriarv1.dailyexpense.domain.usecase.account.AddAccountUseCase
import com.fahimshahriarv1.dailyexpense.domain.usecase.account.DeleteAccountUseCase
import com.fahimshahriarv1.dailyexpense.domain.usecase.account.GetAccountsUseCase
import com.fahimshahriarv1.dailyexpense.domain.usecase.auth.GetCurrentUserUseCase
import com.fahimshahriarv1.dailyexpense.domain.usecase.auth.SignInWithGoogleUseCase
import com.fahimshahriarv1.dailyexpense.domain.usecase.auth.SignOutUseCase
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
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AccountState())
    val state = _state.asStateFlow()

    private val _effect = Channel<AccountEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        _state.update {
            it.copy(
                isSignedIn = getCurrentUserUseCase.isSignedIn(),
                user = getCurrentUserUseCase.getUser()
            )
        }
        loadAccounts()
        observeAuthState()
    }

    fun onEvent(event: AccountEvent) {
        when (event) {
            is AccountEvent.NameChanged -> _state.update { it.copy(name = event.name) }
            is AccountEvent.TypeChanged -> _state.update { it.copy(type = event.type) }
            is AccountEvent.BalanceChanged -> _state.update { it.copy(balance = event.balance) }
            is AccountEvent.AddAccount -> addAccount()
            is AccountEvent.DeleteAccount -> deleteAccount(event.account)
            is AccountEvent.ToggleAddDialog -> _state.update { it.copy(showAddDialog = !it.showAddDialog) }
            is AccountEvent.SignInWithToken -> signIn(event.idToken)
            is AccountEvent.SignInFailed -> {
                _state.update { it.copy(isAuthLoading = false, authError = event.message) }
            }
            is AccountEvent.SignOut -> signOut()
        }
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            getCurrentUserUseCase().collect { user ->
                _state.update {
                    it.copy(isSignedIn = user != null, user = user)
                }
            }
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

    private fun signIn(idToken: String) {
        viewModelScope.launch {
            _state.update { it.copy(isAuthLoading = true, authError = null) }
            try {
                val user = signInWithGoogleUseCase(idToken)
                _state.update { it.copy(isSignedIn = true, user = user, isAuthLoading = false) }
                _effect.send(AccountEffect.ShowSnackbar("Signed in & synced"))
            } catch (e: Exception) {
                _state.update {
                    it.copy(isAuthLoading = false, authError = e.message ?: "Sign-in failed")
                }
            }
        }
    }

    private fun signOut() {
        viewModelScope.launch {
            signOutUseCase()
            _state.update { it.copy(isSignedIn = false, user = null) }
            _effect.send(AccountEffect.ShowSnackbar("Signed out"))
        }
    }
}
