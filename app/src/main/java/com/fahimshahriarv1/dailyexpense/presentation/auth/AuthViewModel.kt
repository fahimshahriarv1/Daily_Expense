package com.fahimshahriarv1.dailyexpense.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fahimshahriarv1.dailyexpense.domain.usecase.auth.GetCurrentUserUseCase
import com.fahimshahriarv1.dailyexpense.domain.usecase.auth.SignInWithGoogleUseCase
import com.fahimshahriarv1.dailyexpense.domain.usecase.auth.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    init {
        _state.update {
            it.copy(
                isSignedIn = getCurrentUserUseCase.isSignedIn(),
                user = getCurrentUserUseCase.getUser()
            )
        }
        observeAuthState()
    }

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.SignInWithToken -> signIn(event.idToken)
            is AuthEvent.SignInFailed -> {
                _state.update { it.copy(isLoading = false, error = event.message) }
            }
            is AuthEvent.SignOut -> signOut()
        }
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            getCurrentUserUseCase().collect { user ->
                _state.update {
                    it.copy(isSignedIn = user != null, user = user)
                }
            }
        }
    }

    private fun signIn(idToken: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val user = signInWithGoogleUseCase(idToken)
                _state.update { it.copy(isSignedIn = true, user = user, isLoading = false) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, error = e.message ?: "Sign-in failed")
                }
            }
        }
    }

    private fun signOut() {
        viewModelScope.launch {
            signOutUseCase()
            _state.update { AuthState() }
        }
    }
}
