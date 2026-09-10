package com.renzien.phantomim.ui.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.renzien.phantomim.data.repository.AuthRepository
import com.renzien.phantomim.data.repository.UserRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository(),
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AuthUiState(userId = repository.currentUserId)
    )

    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private var profileJob: Job? = null

    init {
        loadProfile()
    }

    fun signIn(email: String, password: String) {
        authenticate(defaultError = AuthError.SignInFailed) {
            repository.signIn(
                email = email,
                password = password
            )
        }
    }

    fun createAccount(email: String, password: String) {
        authenticate(defaultError = AuthError.SignUpFailed) {
            repository.createAccount(
                email = email,
                password = password
            )
        }
    }

    fun loadProfile() {
        val userId = _uiState.value.userId ?: return

        if (_uiState.value.profileStatus == ProfileStatus.Loading) {
            return
        }

        _uiState.update {
            it.copy(
                profileStatus = ProfileStatus.Loading,
                profile = null
            )
        }

        profileJob = viewModelScope.launch {
            try {
                val profile = userRepository.getProfile(userId)

                _uiState.update { currentState ->
                    // Ignore results from a previous session.
                    if (currentState.userId != userId) {
                        currentState
                    } else {
                        currentState.copy(
                            profile = profile,
                            profileStatus = if (profile == null) {
                                ProfileStatus.Missing
                            } else {
                                ProfileStatus.Ready
                            }
                        )
                    }
                }
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                _uiState.update { currentState ->
                    if (currentState.userId != userId) {
                        currentState
                    } else {
                        currentState.copy(
                            profileStatus = ProfileStatus.Failed,
                            profile = null
                        )
                    }
                }
            }
        }
    }

    fun signOut() {
        if (_uiState.value.isLoading) {
            return
        }

        profileJob?.cancel()
        profileJob = null

        repository.signOut()
        _uiState.value = AuthUiState()
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
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

        _uiState.update {
            it.copy(isLoading = true, error = null)
        }

        viewModelScope.launch {
            try {
                action()

                val userId = checkNotNull(repository.currentUserId)

                _uiState.update {
                    it.copy(userId = userId)
                }

                loadProfile()
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

                _uiState.update {
                    it.copy(error = authError)
                }
            } finally {
                _uiState.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }
}