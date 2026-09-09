package com.renzien.phantomim.ui.viewmodel.auth

enum class AuthError {
    Network,
    TooManyRequests,
    PasswordRejected,
    SignInFailed,
    SignUpFailed
}

data class AuthUiState(
    val isLoading: Boolean = false,
    val userId: String? = null,
    val error: AuthError? = null
)