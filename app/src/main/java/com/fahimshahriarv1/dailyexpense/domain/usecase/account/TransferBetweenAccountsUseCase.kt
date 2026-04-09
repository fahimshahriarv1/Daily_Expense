package com.fahimshahriarv1.dailyexpense.domain.usecase.account

import com.fahimshahriarv1.dailyexpense.domain.repository.AccountRepository
import javax.inject.Inject

class TransferBetweenAccountsUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(fromAccountUuid: String, toAccountUuid: String, amount: Double) {
        val fromAccount = accountRepository.getAccountByUuid(fromAccountUuid)
            ?: throw IllegalArgumentException("Source account not found")
        val toAccount = accountRepository.getAccountByUuid(toAccountUuid)
            ?: throw IllegalArgumentException("Destination account not found")

        if (fromAccount.balance < amount) {
            throw IllegalArgumentException("Insufficient balance")
        }

        accountRepository.updateAccount(fromAccount.copy(balance = fromAccount.balance - amount))
        accountRepository.updateAccount(toAccount.copy(balance = toAccount.balance + amount))
    }
}
