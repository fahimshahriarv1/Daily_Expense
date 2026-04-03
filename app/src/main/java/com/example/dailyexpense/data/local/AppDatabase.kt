package com.example.dailyexpense.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.dailyexpense.data.local.dao.AccountDao
import com.example.dailyexpense.data.local.dao.ExpenseDao
import com.example.dailyexpense.data.local.entity.AccountEntity
import com.example.dailyexpense.data.local.entity.ExpenseEntity

@Database(
    entities = [ExpenseEntity::class, AccountEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun accountDao(): AccountDao
}
