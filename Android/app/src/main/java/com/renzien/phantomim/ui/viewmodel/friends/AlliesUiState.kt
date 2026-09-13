package com.renzien.phantomim.ui.viewmodel.friends

import com.renzien.phantomim.data.model.UserProfile

enum class AlliesError {
    Network,
    LoadFailed
}

data class AlliesUiState(
    val profiles: List<UserProfile> = emptyList(),
    val hasLoaded: Boolean = false,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = false,
    val error: AlliesError? = null,
    val loadMoreError: AlliesError? = null
) {
    val isBusy: Boolean
        get() = isLoading || isLoadingMore
}