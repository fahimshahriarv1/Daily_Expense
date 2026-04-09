package com.fahimshahriarv1.dailyexpense.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fahimshahriarv1.dailyexpense.data.local.dao.AccountDao
import com.fahimshahriarv1.dailyexpense.data.local.dao.ExpenseDao
import com.fahimshahriarv1.dailyexpense.data.local.dao.IncomeDao
import com.fahimshahriarv1.dailyexpense.data.local.dao.TransferDao
import com.fahimshahriarv1.dailyexpense.data.local.entity.AccountEntity
import com.fahimshahriarv1.dailyexpense.data.local.entity.ExpenseEntity
import com.fahimshahriarv1.dailyexpense.data.local.entity.IncomeEntity
import com.fahimshahriarv1.dailyexpense.data.local.entity.TransferEntity

@Database(
    entities = [ExpenseEntity::class, AccountEntity::class, IncomeEntity::class, TransferEntity::class],
    version = 5,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun accountDao(): AccountDao
    abstract fun incomeDao(): IncomeDao
    abstract fun transferDao(): TransferDao
}
