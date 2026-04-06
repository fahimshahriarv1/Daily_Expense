package com.fahimshahriarv1.dailyexpense.di

import com.fahimshahriarv1.dailyexpense.data.repository.AccountRepositoryImpl
import com.fahimshahriarv1.dailyexpense.data.repository.AuthRepositoryImpl
import com.fahimshahriarv1.dailyexpense.data.repository.ExpenseRepositoryImpl
import com.fahimshahriarv1.dailyexpense.data.repository.IncomeRepositoryImpl
import com.fahimshahriarv1.dailyexpense.domain.repository.AccountRepository
import com.fahimshahriarv1.dailyexpense.domain.repository.AuthRepository
import com.fahimshahriarv1.dailyexpense.domain.repository.ExpenseRepository
import com.fahimshahriarv1.dailyexpense.domain.repository.IncomeRepository
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

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindIncomeRepository(impl: IncomeRepositoryImpl): IncomeRepository
}
