package com.fahimshahriarv1.dailyexpense.presentation.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fahimshahriarv1.dailyexpense.domain.model.Account
import com.fahimshahriarv1.dailyexpense.domain.model.Income
import com.fahimshahriarv1.dailyexpense.domain.usecase.account.AddAccountUseCase
import com.fahimshahriarv1.dailyexpense.domain.usecase.account.AddIncomeUseCase
import com.fahimshahriarv1.dailyexpense.domain.usecase.account.DeleteAccountUseCase
import com.fahimshahriarv1.dailyexpense.domain.usecase.account.DeleteIncomeUseCase
import com.fahimshahriarv1.dailyexpense.domain.usecase.account.GetAccountsUseCase
import com.fahimshahriarv1.dailyexpense.domain.usecase.account.GetIncomesUseCase
import com.fahimshahriarv1.dailyexpense.domain.usecase.account.UpdateIncomeUseCase
import com.fahimshahriarv1.dailyexpense.domain.usecase.auth.GetCurrentUserUseCase
import com.fahimshahriarv1.dailyexpense.domain.usecase.auth.SignInWithGoogleUseCase
import com.fahimshahriarv1.dailyexpense.domain.usecase.auth.SignOutUseCase
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
class AccountViewModel @Inject constructor(
    private val getAccountsUseCase: GetAccountsUseCase,
    private val addAccountUseCase: AddAccountUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val getIncomesUseCase: GetIncomesUseCase,
    private val addIncomeUseCase: AddIncomeUseCase,
    private val updateIncomeUseCase: UpdateIncomeUseCase,
    private val deleteIncomeUseCase: DeleteIncomeUseCase,
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
        loadData()
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

            // Income events
            is AccountEvent.StartAddIncome -> _state.update {
                it.copy(
                    showIncomeSheet = true,
                    editingIncome = null,
                    incomeAmount = "",
                    incomeSource = "",
                    incomeNote = "",
                    incomeDate = System.currentTimeMillis(),
                    incomeAccountId = event.account.id
                )
            }
            is AccountEvent.StartEditIncome -> startEditIncome(event.income)
            is AccountEvent.IncomeAmountChanged -> _state.update { it.copy(incomeAmount = event.amount) }
            is AccountEvent.IncomeSourceChanged -> _state.update { it.copy(incomeSource = event.source) }
            is AccountEvent.IncomeNoteChanged -> _state.update { it.copy(incomeNote = event.note) }
            is AccountEvent.IncomeDateChanged -> _state.update { it.copy(incomeDate = event.date) }
            is AccountEvent.IncomeAccountSelected -> _state.update { it.copy(incomeAccountId = event.accountId) }
            is AccountEvent.ConfirmAddIncome -> addIncome()
            is AccountEvent.ConfirmUpdateIncome -> updateIncome()
            is AccountEvent.DeleteIncome -> deleteIncome(event.income)
            is AccountEvent.DismissIncomeSheet -> _state.update {
                it.copy(
                    showIncomeSheet = false,
                    editingIncome = null,
                    incomeAmount = "",
                    incomeSource = "",
                    incomeNote = "",
                    incomeDate = System.currentTimeMillis(),
                    incomeAccountId = -1L
                )
            }
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

    private fun loadData() {
        viewModelScope.launch {
            combine(
                getAccountsUseCase(),
                getIncomesUseCase()
            ) { accounts, incomes ->
                val accountMap = accounts.associateBy { it.id }
                val enrichedIncomes = incomes.map { income ->
                    income.copy(accountName = accountMap[income.accountId]?.name ?: "Unknown")
                }
                Pair(accounts, enrichedIncomes)
            }.collect { (accounts, incomes) ->
                _state.update {
                    it.copy(accounts = accounts, incomes = incomes, isLoading = false)
                }
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

    private fun startEditIncome(income: Income) {
        _state.update {
            it.copy(
                showIncomeSheet = true,
                editingIncome = income,
                incomeAmount = income.amount.toBigDecimal().stripTrailingZeros().toPlainString(),
                incomeSource = income.source,
                incomeNote = income.note,
                incomeDate = income.date,
                incomeAccountId = income.accountId
            )
        }
    }

    private fun addIncome() {
        val currentState = _state.value
        val amount = currentState.incomeAmount.toDoubleOrNull()

        if (amount == null || amount <= 0) {
            viewModelScope.launch { _effect.send(AccountEffect.ShowSnackbar("Enter a valid amount")) }
            return
        }
        if (currentState.incomeSource.isEmpty()) {
            viewModelScope.launch { _effect.send(AccountEffect.ShowSnackbar("Select a source")) }
            return
        }
        if (currentState.incomeAccountId == -1L) {
            viewModelScope.launch { _effect.send(AccountEffect.ShowSnackbar("Select an account")) }
            return
        }

        viewModelScope.launch {
            try {
                addIncomeUseCase(
                    Income(
                        amount = amount,
                        source = currentState.incomeSource,
                        note = currentState.incomeNote,
                        date = currentState.incomeDate,
                        accountId = currentState.incomeAccountId
                    )
                )
                _state.update {
                    it.copy(
                        showIncomeSheet = false,
                        incomeAmount = "",
                        incomeSource = "",
                        incomeNote = "",
                        incomeDate = System.currentTimeMillis(),
                        incomeAccountId = -1L
                    )
                }
                _effect.send(AccountEffect.ShowSnackbar("Income added"))
            } catch (e: Exception) {
                _effect.send(AccountEffect.ShowSnackbar("Failed to add income"))
            }
        }
    }

    private fun updateIncome() {
        val currentState = _state.value
        val oldIncome = currentState.editingIncome ?: return
        val amount = currentState.incomeAmount.toDoubleOrNull()

        if (amount == null || amount <= 0) {
            viewModelScope.launch { _effect.send(AccountEffect.ShowSnackbar("Enter a valid amount")) }
            return
        }
        if (currentState.incomeSource.isEmpty()) {
            viewModelScope.launch { _effect.send(AccountEffect.ShowSnackbar("Select a source")) }
            return
        }
        if (currentState.incomeAccountId == -1L) {
            viewModelScope.launch { _effect.send(AccountEffect.ShowSnackbar("Select an account")) }
            return
        }

        val newIncome = oldIncome.copy(
            amount = amount,
            source = currentState.incomeSource,
            note = currentState.incomeNote,
            date = currentState.incomeDate,
            accountId = currentState.incomeAccountId
        )

        viewModelScope.launch {
            try {
                updateIncomeUseCase(oldIncome, newIncome)
                _state.update {
                    it.copy(
                        showIncomeSheet = false,
                        editingIncome = null,
                        incomeAmount = "",
                        incomeSource = "",
                        incomeNote = "",
                        incomeDate = System.currentTimeMillis(),
                        incomeAccountId = -1L
                    )
                }
                _effect.send(AccountEffect.ShowSnackbar("Income updated"))
            } catch (e: Exception) {
                _effect.send(AccountEffect.ShowSnackbar("Failed to update income"))
            }
        }
    }

    private fun deleteIncome(income: Income) {
        viewModelScope.launch {
            try {
                deleteIncomeUseCase(income)
                _effect.send(AccountEffect.ShowSnackbar("Income deleted"))
            } catch (e: Exception) {
                _effect.send(AccountEffect.ShowSnackbar("Failed to delete income"))
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
