package com.fahimshahriarv1.dailyexpense.presentation.auth

import com.fahimshahriarv1.dailyexpense.domain.model.User

data class AuthState(
    val isSignedIn: Boolean = false,
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
