package com.example.dailyexpense.di

import com.example.dailyexpense.data.repository.AccountRepositoryImpl
import com.example.dailyexpense.data.repository.ExpenseRepositoryImpl
import com.example.dailyexpense.domain.repository.AccountRepository
import com.example.dailyexpense.domain.repository.ExpenseRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindExpenseRepository(impl: ExpenseRepositoryImpl): ExpenseRepository

    @Binds
    @Singleton
    abstract fun bindAccountRepository(impl: AccountRepositoryImpl): AccountRepository
}
