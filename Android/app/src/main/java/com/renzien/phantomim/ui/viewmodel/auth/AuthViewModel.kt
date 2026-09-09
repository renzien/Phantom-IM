package com.renzien.phantomim.ui.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.renzien.phantomim.data.repository.AuthRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AuthUiState(
            userId = repository.currentUserId
        )
    )

    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signIn(
        email: String,
        password: String
    ) {
        authenticate(
            defaultError = AuthError.SignInFailed
        ) {
            repository.signIn(
                email = email,
                password = password
            )
        }
    }

    fun createAccount(
        email: String,
        password: String
    ) {
        authenticate(
            defaultError = AuthError.SignUpFailed
        ) {
            repository.createAccount(
                email = email,
                password = password
            )
        }
    }

    fun signOut() {
        if (_uiState.value.isLoading) {
            return
        }

        repository.signOut()
        _uiState.value = AuthUiState()
    }

    fun clearError() {
        _uiState.update { currentState ->
            currentState.copy(error = null)
        }
    }

    private fun authenticate(
        defaultError: AuthError,
        action: suspend () -> Unit
    ) {
        if (
            _uiState.value.isLoading ||
            _uiState.value.userId != null
        ) {
            return
        }

        _uiState.update { currentState ->
            currentState.copy(
                isLoading = true,
                error = null
            )
        }

        viewModelScope.launch {
            try {
                action()

                val userId = checkNotNull(repository.currentUserId)

                _uiState.update { currentState ->
                    currentState.copy(userId = userId)
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                val authError = when (error) {
                    is FirebaseNetworkException ->
                        AuthError.Network

                    is FirebaseTooManyRequestsException ->
                        AuthError.TooManyRequests

                    is FirebaseAuthWeakPasswordException ->
                        AuthError.PasswordRejected

                    else -> defaultError
                }

                _uiState.update { currentState ->
                    currentState.copy(error = authError)
                }
            } finally {
                _uiState.update { currentState ->
                    currentState.copy(isLoading = false)
                }
            }
        }
    }
}