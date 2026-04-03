package com.example.dailyexpense.di

import android.content.Context
import androidx.room.Room
import com.example.dailyexpense.data.local.AppDatabase
import com.example.dailyexpense.data.local.dao.AccountDao
import com.example.dailyexpense.data.local.dao.ExpenseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "daily_expense_db"
        ).build()
    }

    @Provides
    fun provideExpenseDao(db: AppDatabase): ExpenseDao = db.expenseDao()

    @Provides
    fun provideAccountDao(db: AppDatabase): AccountDao = db.accountDao()
}
