package com.fahimshahriarv1.dailyexpense.domain.usecase.auth

import com.fahimshahriarv1.dailyexpense.domain.model.User
import com.fahimshahriarv1.dailyexpense.domain.repository.AccountRepository
import com.fahimshahriarv1.dailyexpense.domain.repository.AuthRepository
import com.fahimshahriarv1.dailyexpense.domain.repository.ExpenseRepository
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val accountRepository: AccountRepository,
    private val expenseRepository: ExpenseRepository
) {
    suspend operator fun invoke(idToken: String): User {
        val user = authRepository.signInWithGoogle(idToken)
        // Push existing local data to Firestore (offline data created before sign-in)
        accountRepository.syncToRemote()
        expenseRepository.syncToRemote()
        // Pull any data from Firestore (from other devices)
        accountRepository.syncFromRemote()
        expenseRepository.syncFromRemote()
        return user
    }
}
