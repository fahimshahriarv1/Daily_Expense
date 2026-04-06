package com.fahimshahriarv1.dailyexpense.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.fahimshahriarv1.dailyexpense.data.local.entity.IncomeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeDao {

    @Insert
    suspend fun insert(income: IncomeEntity): Long

    @Update
    suspend fun update(income: IncomeEntity)

    @Delete
    suspend fun delete(income: IncomeEntity)

    @Query("SELECT * FROM incomes ORDER BY date DESC")
    fun getAllIncomes(): Flow<List<IncomeEntity>>

    @Query("SELECT * FROM incomes ORDER BY date DESC")
    suspend fun getAllIncomesOnce(): List<IncomeEntity>

    @Query("SELECT * FROM incomes WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    fun getIncomesBetween(start: Long, end: Long): Flow<List<IncomeEntity>>

    @Query("SELECT * FROM incomes WHERE uuid = :uuid LIMIT 1")
    suspend fun getByUuid(uuid: String): IncomeEntity?
}
