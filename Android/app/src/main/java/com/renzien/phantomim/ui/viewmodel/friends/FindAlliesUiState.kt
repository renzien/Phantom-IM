package com.renzien.phantomim.ui.viewmodel.friends

import com.renzien.phantomim.data.model.UserProfile

enum class FindAlliesStatus {
    Idle,
    Loading,
    Found,
    NotFound,
    OwnAccount,
    InvalidUsername,
    NetworkError,
    Failed
}

data class FindAlliesUiState(
    val status: FindAlliesStatus = FindAlliesStatus.Idle,
    val query: String = "",
    val profile: UserProfile? = null
)