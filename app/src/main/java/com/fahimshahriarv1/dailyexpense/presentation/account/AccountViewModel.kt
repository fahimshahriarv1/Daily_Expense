package com.fahimshahriarv1.dailyexpense.presentation.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fahimshahriarv1.dailyexpense.domain.model.Account
import com.fahimshahriarv1.dailyexpense.domain.model.Income
import com.fahimshahriarv1.dailyexpense.domain.usecase.account.AddAccountUseCase
import com.fahimshahriarv1.dailyexpense.domain.usecase.account.AddIncomeUseCase
import com.fahimshahriarv1.dailyexpense.domain.usecase.account.DeleteAccountUseCase
import com.fahimshahriarv1.dailyexpense.domain.usecase.account.DeleteIncomeUseCase
import com.fahimshahriarv1.dailyexpense.domain.usecase.account.DeleteTransferUseCase
import com.fahimshahriarv1.dailyexpense.domain.usecase.account.GetAccountsUseCase
import com.fahimshahriarv1.dailyexpense.domain.usecase.account.GetIncomesUseCase
import com.fahimshahriarv1.dailyexpense.domain.usecase.account.GetTransfersUseCase
import com.fahimshahriarv1.dailyexpense.domain.usecase.account.TransferBetweenAccountsUseCase
import com.fahimshahriarv1.dailyexpense.domain.usecase.account.UpdateIncomeUseCase
import com.fahimshahriarv1.dailyexpense.domain.usecase.auth.GetCurrentUserUseCase
import com.fahimshahriarv1.dailyexpense.domain.usecase.auth.SignInWithGoogleUseCase
import com.fahimshahriarv1.dailyexpense.domain.usecase.auth.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
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
    private val transferBetweenAccountsUseCase: TransferBetweenAccountsUseCase,
    private val getTransfersUseCase: GetTransfersUseCase,
    private val deleteTransferUseCase: DeleteTransferUseCase,
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
                    incomeAccountUuid = event.account.uuid
                )
            }
            is AccountEvent.StartEditIncome -> startEditIncome(event.income)
            is AccountEvent.IncomeAmountChanged -> _state.update { it.copy(incomeAmount = event.amount) }
            is AccountEvent.IncomeSourceChanged -> _state.update { it.copy(incomeSource = event.source) }
            is AccountEvent.IncomeNoteChanged -> _state.update { it.copy(incomeNote = event.note) }
            is AccountEvent.IncomeDateChanged -> _state.update { it.copy(incomeDate = event.date) }
            is AccountEvent.IncomeAccountSelected -> _state.update { it.copy(incomeAccountUuid = event.accountUuid) }
            is AccountEvent.ConfirmAddIncome -> addIncome()
            is AccountEvent.ConfirmUpdateIncome -> updateIncome()
            is AccountEvent.DeleteIncome -> deleteIncome(event.income)

            // Transfer events
            is AccountEvent.StartTransfer -> _state.update {
                it.copy(
                    showTransferSheet = true,
                    transferAmount = "",
                    transferFromAccountUuid = event.account.uuid,
                    transferToAccountUuid = "",
                    transferNote = "",
                    transferDate = System.currentTimeMillis()
                )
            }
            is AccountEvent.TransferAmountChanged -> _state.update { it.copy(transferAmount = event.amount) }
            is AccountEvent.TransferToAccountSelected -> _state.update { it.copy(transferToAccountUuid = event.accountUuid) }
            is AccountEvent.TransferNoteChanged -> _state.update { it.copy(transferNote = event.note) }
            is AccountEvent.TransferDateChanged -> _state.update { it.copy(transferDate = event.date) }
            is AccountEvent.ConfirmTransfer -> confirmTransfer()
            is AccountEvent.DismissTransferSheet -> _state.update {
                it.copy(
                    showTransferSheet = false,
                    transferAmount = "",
                    transferFromAccountUuid = "",
                    transferToAccountUuid = "",
                    transferNote = "",
                    transferDate = System.currentTimeMillis()
                )
            }
            is AccountEvent.DeleteTransfer -> deleteTransfer(event.transfer)

            is AccountEvent.DismissIncomeSheet -> _state.update {
                it.copy(
                    showIncomeSheet = false,
                    editingIncome = null,
                    incomeAmount = "",
                    incomeSource = "",
                    incomeNote = "",
                    incomeDate = System.currentTimeMillis(),
                    incomeAccountUuid = ""
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
                getIncomesUseCase(),
                getTransfersUseCase()
            ) { accounts, incomes, transfers ->
                val accountMap = accounts.associateBy { it.uuid }
                val enrichedIncomes = incomes.map { income ->
                    income.copy(accountName = accountMap[income.accountUuid]?.name ?: "Unknown")
                }
                val enrichedTransfers = transfers.map { transfer ->
                    transfer.copy(
                        fromAccountName = accountMap[transfer.fromAccountUuid]?.name ?: "Unknown",
                        toAccountName = accountMap[transfer.toAccountUuid]?.name ?: "Unknown"
                    )
                }
                Triple(accounts, enrichedIncomes, enrichedTransfers)
            }.collect { (accounts, incomes, transfers) ->
                _state.update {
                    it.copy(accounts = accounts, incomes = incomes, transfers = transfers, isLoading = false)
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
            _state.update { it.copy(isActionLoading = true) }
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
                        showAddDialog = false,
                        isActionLoading = false
                    )
                }
                delay(350)
                _effect.send(AccountEffect.ShowSnackbar("Account added"))
            } catch (e: Exception) {
                _state.update { it.copy(isActionLoading = false) }
                _effect.send(AccountEffect.ShowSnackbar("Failed to add account"))
            }
        }
    }

    private fun deleteAccount(account: Account) {
        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true) }
            try {
                deleteAccountUseCase(account)
                _state.update { it.copy(isActionLoading = false) }
                _effect.send(AccountEffect.ShowSnackbar("Account deleted"))
            } catch (e: Exception) {
                _state.update { it.copy(isActionLoading = false) }
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
                incomeAccountUuid = income.accountUuid
            )
        }
    }

    private suspend fun refreshData() {
        delay(300)
        val accounts = getAccountsUseCase().first()
        val incomes = getIncomesUseCase().first()
        val transfers = getTransfersUseCase().first()
        val accountMap = accounts.associateBy { it.uuid }
        val enrichedIncomes = incomes.map { income ->
            income.copy(accountName = accountMap[income.accountUuid]?.name ?: "Unknown")
        }
        val enrichedTransfers = transfers.map { transfer ->
            transfer.copy(
                fromAccountName = accountMap[transfer.fromAccountUuid]?.name ?: "Unknown",
                toAccountName = accountMap[transfer.toAccountUuid]?.name ?: "Unknown"
            )
        }
        _state.update { it.copy(accounts = accounts, incomes = enrichedIncomes, transfers = enrichedTransfers) }
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
        if (currentState.incomeAccountUuid.isEmpty()) {
            viewModelScope.launch { _effect.send(AccountEffect.ShowSnackbar("Select an account")) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true) }
            try {
                addIncomeUseCase(
                    Income(
                        amount = amount,
                        source = currentState.incomeSource,
                        note = currentState.incomeNote,
                        date = currentState.incomeDate,
                        accountUuid = currentState.incomeAccountUuid
                    )
                )
                _state.update {
                    it.copy(
                        showIncomeSheet = false,
                        incomeAmount = "",
                        incomeSource = "",
                        incomeNote = "",
                        incomeDate = System.currentTimeMillis(),
                        incomeAccountUuid = ""
                    )
                }
                delay(350)
                _effect.send(AccountEffect.ShowSnackbar("Income added"))
                refreshData()
            } catch (e: Exception) {
                _effect.send(AccountEffect.ShowSnackbar("Failed to add income"))
            } finally {
                _state.update { it.copy(isActionLoading = false) }
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
        if (currentState.incomeAccountUuid.isEmpty()) {
            viewModelScope.launch { _effect.send(AccountEffect.ShowSnackbar("Select an account")) }
            return
        }

        val newIncome = oldIncome.copy(
            amount = amount,
            source = currentState.incomeSource,
            note = currentState.incomeNote,
            date = currentState.incomeDate,
            accountUuid = currentState.incomeAccountUuid
        )

        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true) }
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
                        incomeAccountUuid = "",
                        isActionLoading = false
                    )
                }
                delay(350)
                _effect.send(AccountEffect.ShowSnackbar("Income updated"))
            } catch (e: Exception) {
                _state.update { it.copy(isActionLoading = false) }
                _effect.send(AccountEffect.ShowSnackbar("Failed to update income"))
            }
        }
    }

    private fun deleteIncome(income: Income) {
        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true) }
            try {
                deleteIncomeUseCase(income)
                _effect.send(AccountEffect.ShowSnackbar("Income deleted"))
                refreshData()
            } catch (e: Exception) {
                _effect.send(AccountEffect.ShowSnackbar("Failed to delete income"))
            } finally {
                _state.update { it.copy(isActionLoading = false) }
            }
        }
    }

    private fun deleteTransfer(transfer: com.fahimshahriarv1.dailyexpense.domain.model.Transfer) {
        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true) }
            try {
                deleteTransferUseCase(transfer)
                _effect.send(AccountEffect.ShowSnackbar("Transfer record deleted"))
                refreshData()
            } catch (e: Exception) {
                _effect.send(AccountEffect.ShowSnackbar("Failed to delete transfer"))
            } finally {
                _state.update { it.copy(isActionLoading = false) }
            }
        }
    }

    private fun confirmTransfer() {
        val currentState = _state.value
        val amount = currentState.transferAmount.toDoubleOrNull()

        if (amount == null || amount <= 0) {
            viewModelScope.launch { _effect.send(AccountEffect.ShowSnackbar("Enter a valid amount")) }
            return
        }
        if (currentState.transferFromAccountUuid.isEmpty()) {
            viewModelScope.launch { _effect.send(AccountEffect.ShowSnackbar("Select source account")) }
            return
        }
        if (currentState.transferToAccountUuid.isEmpty()) {
            viewModelScope.launch { _effect.send(AccountEffect.ShowSnackbar("Select destination account")) }
            return
        }
        if (currentState.transferFromAccountUuid == currentState.transferToAccountUuid) {
            viewModelScope.launch { _effect.send(AccountEffect.ShowSnackbar("Cannot transfer to the same account")) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true) }
            try {
                transferBetweenAccountsUseCase(
                    fromAccountUuid = currentState.transferFromAccountUuid,
                    toAccountUuid = currentState.transferToAccountUuid,
                    amount = amount,
                    note = currentState.transferNote,
                    date = currentState.transferDate
                )
                _state.update {
                    it.copy(
                        showTransferSheet = false,
                        transferAmount = "",
                        transferFromAccountUuid = "",
                        transferToAccountUuid = "",
                        transferNote = "",
                        transferDate = System.currentTimeMillis()
                    )
                }
                delay(350)
                _effect.send(AccountEffect.ShowSnackbar("Transfer successful"))
                refreshData()
            } catch (e: IllegalArgumentException) {
                _effect.send(AccountEffect.ShowSnackbar(e.message ?: "Transfer failed"))
            } catch (e: Exception) {
                _effect.send(AccountEffect.ShowSnackbar("Transfer failed"))
            } finally {
                _state.update { it.copy(isActionLoading = false) }
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
