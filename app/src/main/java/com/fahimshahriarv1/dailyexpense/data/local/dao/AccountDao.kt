package com.fahimshahriarv1.dailyexpense.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.fahimshahriarv1.dailyexpense.data.local.entity.AccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {

    @Insert
    suspend fun insert(account: AccountEntity): Long

    @Update
    suspend fun update(account: AccountEntity)

    @Delete
    suspend fun delete(account: AccountEntity)

    @Query("SELECT * FROM accounts ORDER BY name ASC")
    fun getAllAccounts(): Flow<List<AccountEntity>>

    @Query("SELECT * FROM accounts ORDER BY name ASC")
    suspend fun getAllAccountsOnce(): List<AccountEntity>

    @Query("SELECT * FROM accounts WHERE id = :id")
    suspend fun getById(id: Long): AccountEntity?

    @Query("SELECT * FROM accounts WHERE uuid = :uuid LIMIT 1")
    suspend fun getByUuid(uuid: String): AccountEntity?

    @Query("UPDATE accounts SET balance = balance - :amount WHERE id = :id")
    suspend fun deductBalance(id: Long, amount: Double)

    @Query("UPDATE accounts SET balance = balance + :amount WHERE id = :id")
    suspend fun addBalance(id: Long, amount: Double)
}
