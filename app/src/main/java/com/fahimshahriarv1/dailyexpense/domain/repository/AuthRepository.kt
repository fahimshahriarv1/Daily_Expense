package com.fahimshahriarv1.dailyexpense.domain.repository

import com.fahimshahriarv1.dailyexpense.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>
    fun isSignedIn(): Boolean
    fun getCurrentUser(): User?
    suspend fun signInWithGoogle(idToken: String): User
    suspend fun signOut()
}
