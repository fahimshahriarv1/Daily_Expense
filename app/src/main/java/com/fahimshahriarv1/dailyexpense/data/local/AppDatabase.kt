package com.fahimshahriarv1.dailyexpense.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fahimshahriarv1.dailyexpense.data.local.dao.AccountDao
import com.fahimshahriarv1.dailyexpense.data.local.dao.ExpenseDao
import com.fahimshahriarv1.dailyexpense.data.local.entity.AccountEntity
import com.fahimshahriarv1.dailyexpense.data.local.entity.ExpenseEntity

@Database(
    entities = [ExpenseEntity::class, AccountEntity::class],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun accountDao(): AccountDao
}
