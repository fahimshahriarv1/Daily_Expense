package com.fahimshahriarv1.dailyexpense.presentation.auth

sealed class AuthEvent {
    data class SignInWithToken(val idToken: String) : AuthEvent()
    data class SignInFailed(val message: String) : AuthEvent()
    data object SignOut : AuthEvent()
}
