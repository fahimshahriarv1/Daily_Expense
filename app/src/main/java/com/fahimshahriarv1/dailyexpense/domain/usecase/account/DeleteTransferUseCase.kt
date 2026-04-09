package com.fahimshahriarv1.dailyexpense.domain.usecase.account

import com.fahimshahriarv1.dailyexpense.domain.model.Transfer
import com.fahimshahriarv1.dailyexpense.domain.repository.TransferRepository
import javax.inject.Inject

class DeleteTransferUseCase @Inject constructor(
    private val transferRepository: TransferRepository
) {
    suspend operator fun invoke(transfer: Transfer) {
        transferRepository.deleteTransfer(transfer)
    }
}
