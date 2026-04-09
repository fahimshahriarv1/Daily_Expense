package com.fahimshahriarv1.dailyexpense.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.fahimshahriarv1.dailyexpense.data.local.entity.TransferEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransferDao {

    @Insert
    suspend fun insert(transfer: TransferEntity): Long

    @Delete
    suspend fun delete(transfer: TransferEntity)

    @Query("SELECT * FROM transfers ORDER BY date DESC")
    fun getAllTransfers(): Flow<List<TransferEntity>>

    @Query("SELECT * FROM transfers ORDER BY date DESC")
    suspend fun getAllTransfersOnce(): List<TransferEntity>

    @Query("SELECT * FROM transfers WHERE uuid = :uuid LIMIT 1")
    suspend fun getByUuid(uuid: String): TransferEntity?
}
