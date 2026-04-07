package com.fahimshahriarv1.dailyexpense.domain.usecase.auth

import com.fahimshahriarv1.dailyexpense.domain.model.User
import com.fahimshahriarv1.dailyexpense.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<User?> = authRepository.currentUser

    fun isSignedIn(): Boolean = authRepository.isSignedIn()

    fun getUser(): User? = authRepository.getCurrentUser()
}
