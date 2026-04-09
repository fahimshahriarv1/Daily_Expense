package com.fahimshahriarv1.dailyexpense.di

import android.content.Context
import androidx.room.Room
import com.fahimshahriarv1.dailyexpense.data.local.AppDatabase
import com.fahimshahriarv1.dailyexpense.data.local.dao.AccountDao
import com.fahimshahriarv1.dailyexpense.data.local.dao.ExpenseDao
import com.fahimshahriarv1.dailyexpense.data.local.dao.IncomeDao
import com.fahimshahriarv1.dailyexpense.data.local.dao.TransferDao
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
        ).fallbackToDestructiveMigration(dropAllTables = true).build()
    }

    @Provides
    fun provideExpenseDao(db: AppDatabase): ExpenseDao = db.expenseDao()

    @Provides
    fun provideAccountDao(db: AppDatabase): AccountDao = db.accountDao()

    @Provides
    fun provideIncomeDao(db: AppDatabase): IncomeDao = db.incomeDao()

    @Provides
    fun provideTransferDao(db: AppDatabase): TransferDao = db.transferDao()
}
