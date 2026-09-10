package com.renzien.phantomim.ui.viewmodel.auth

import com.renzien.phantomim.data.model.UserProfile

enum class AuthError {
    Network,
    TooManyRequests,
    PasswordRejected,
    SignInFailed,
    SignUpFailed
}

enum class ProfileStatus {
    NotLoaded,
    Loading,
    Missing,
    Ready,
    Failed
}

data class AuthUiState(
    val isLoading: Boolean = false,
    val userId: String? = null,
    val error: AuthError? = null,
    val profileStatus: ProfileStatus = ProfileStatus.NotLoaded,
    val profile: UserProfile? = null
)