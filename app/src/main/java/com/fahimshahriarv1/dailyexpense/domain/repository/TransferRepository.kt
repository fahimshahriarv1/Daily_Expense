package com.fahimshahriarv1.dailyexpense.domain.repository

import com.fahimshahriarv1.dailyexpense.domain.model.Transfer
import kotlinx.coroutines.flow.Flow

interface TransferRepository {
    fun getAllTransfers(): Flow<List<Transfer>>
    suspend fun addTransfer(transfer: Transfer)
    suspend fun deleteTransfer(transfer: Transfer)
    suspend fun syncFromRemote()
    suspend fun syncToRemote()
}
