package com.fahimshahriarv1.dailyexpense.domain.usecase.account

import com.fahimshahriarv1.dailyexpense.domain.model.Transfer
import com.fahimshahriarv1.dailyexpense.domain.repository.TransferRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTransfersUseCase @Inject constructor(
    private val transferRepository: TransferRepository
) {
    operator fun invoke(): Flow<List<Transfer>> = transferRepository.getAllTransfers()
}
