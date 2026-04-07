package com.fahimshahriarv1.dailyexpense.domain.usecase.auth

import com.fahimshahriarv1.dailyexpense.domain.repository.AuthRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() {
        authRepository.signOut()
    }
}
