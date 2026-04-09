package com.fahimshahriarv1.dailyexpense.domain.usecase.account

import com.fahimshahriarv1.dailyexpense.domain.model.Transfer
import com.fahimshahriarv1.dailyexpense.domain.repository.AccountRepository
import com.fahimshahriarv1.dailyexpense.domain.repository.TransferRepository
import javax.inject.Inject

class TransferBetweenAccountsUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    private val transferRepository: TransferRepository
) {
    suspend operator fun invoke(fromAccountUuid: String, toAccountUuid: String, amount: Double, note: String = "", date: Long = System.currentTimeMillis()) {
        val fromAccount = accountRepository.getAccountByUuid(fromAccountUuid)
            ?: throw IllegalArgumentException("Source account not found")
        val toAccount = accountRepository.getAccountByUuid(toAccountUuid)
            ?: throw IllegalArgumentException("Destination account not found")

        accountRepository.updateAccount(fromAccount.copy(balance = fromAccount.balance - amount))
        accountRepository.updateAccount(toAccount.copy(balance = toAccount.balance + amount))

        transferRepository.addTransfer(
            Transfer(
                amount = amount,
                fromAccountUuid = fromAccountUuid,
                toAccountUuid = toAccountUuid,
                note = note,
                date = date
            )
        )
    }
}
