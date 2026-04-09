package com.fahimshahriarv1.dailyexpense.data.repository

import android.util.Log
import com.fahimshahriarv1.dailyexpense.data.local.dao.TransferDao
import com.fahimshahriarv1.dailyexpense.data.local.entity.toDomain
import com.fahimshahriarv1.dailyexpense.data.local.entity.toEntity
import com.fahimshahriarv1.dailyexpense.data.remote.FirestoreTransferDataSource
import com.fahimshahriarv1.dailyexpense.domain.model.Transfer
import com.fahimshahriarv1.dailyexpense.domain.repository.TransferRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransferRepositoryImpl @Inject constructor(
    private val transferDao: TransferDao,
    private val firestoreDataSource: FirestoreTransferDataSource
) : TransferRepository {

    override fun getAllTransfers(): Flow<List<Transfer>> {
        return transferDao.getAllTransfers().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addTransfer(transfer: Transfer) {
        transferDao.insert(transfer.toEntity())
        try {
            firestoreDataSource.upsert(transfer)
        } catch (e: Exception) {
            Log.w("TransferRepo", "Firestore sync failed for add", e)
        }
    }

    override suspend fun deleteTransfer(transfer: Transfer) {
        transferDao.delete(transfer.toEntity())
        try {
            firestoreDataSource.delete(transfer.uuid)
        } catch (e: Exception) {
            Log.w("TransferRepo", "Firestore sync failed for delete", e)
        }
    }

    override suspend fun syncFromRemote() {
        try {
            val remoteTransfers = firestoreDataSource.getAll()
            remoteTransfers.forEach { remote ->
                val existing = transferDao.getByUuid(remote.uuid)
                if (existing == null) {
                    transferDao.insert(remote.toEntity().copy(id = 0))
                }
            }
        } catch (e: Exception) {
            Log.w("TransferRepo", "Firestore pull failed", e)
        }
    }

    override suspend fun syncToRemote() {
        try {
            val localTransfers = transferDao.getAllTransfersOnce()
            localTransfers.forEach { entity ->
                firestoreDataSource.upsert(entity.toDomain())
            }
        } catch (e: Exception) {
            Log.w("TransferRepo", "Firestore push failed", e)
        }
    }
}
