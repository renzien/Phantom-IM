package com.renzien.phantomim.ui.viewmodel.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.renzien.phantomim.data.repository.FriendRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AlliesViewModel(
    private val repository: FriendRepository = FriendRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlliesUiState())

    val uiState: StateFlow<AlliesUiState> =
        _uiState.asStateFlow()

    private var ownerUid: String? = null
    private var nextCursor: DocumentSnapshot? = null
    private var loadJob: Job? = null
    private var requestVersion = 0L

    fun load(ownerUid: String) {
        require(ownerUid.isNotBlank()) {
            "Owner user ID must not be blank."
        }

        if (this.ownerUid != ownerUid) {
            reset()
            this.ownerUid = ownerUid
        }

        if (_uiState.value.hasLoaded || _uiState.value.isBusy) {
            return
        }

        loadPage(append = false)
    }

    fun refresh() {
        loadPage(append = false)
    }

    fun loadMore() {
        if (!_uiState.value.hasLoaded || nextCursor == null) {
            return
        }

        loadPage(append = true)
    }

    fun reset() {
        requestVersion++
        loadJob?.cancel()
        loadJob = null
        ownerUid = null
        nextCursor = null
        _uiState.value = AlliesUiState()
    }

    private fun loadPage(append: Boolean) {
        val uid = ownerUid ?: return
        val state = _uiState.value

        if (state.isBusy) {
            return
        }

        val cursor = if (append) {
            nextCursor ?: return
        } else {
            null
        }

        val version = ++requestVersion

        _uiState.value = state.copy(
            isLoading = !append,
            isLoadingMore = append,
            error = null,
            loadMoreError = null
        )

        loadJob = viewModelScope.launch {
            try {
                val page = repository.getFriendsPage(
                    ownerUid = uid,
                    after = cursor
                )

                if (version != requestVersion) {
                    return@launch
                }

                val profiles = if (append) {
                    (state.profiles + page.profiles)
                        .distinctBy { it.uid }
                } else {
                    page.profiles
                }

                nextCursor = page.nextCursor

                _uiState.value = AlliesUiState(
                    profiles = profiles,
                    hasLoaded = true,
                    hasMore = nextCursor != null
                )
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                if (version != requestVersion) {
                    return@launch
                }

                val loadError = when (error) {
                    is FirebaseNetworkException ->
                        AlliesError.Network

                    is FirebaseFirestoreException -> {
                        when (error.code) {
                            FirebaseFirestoreException.Code.UNAVAILABLE,
                            FirebaseFirestoreException.Code.DEADLINE_EXCEEDED ->
                                AlliesError.Network

                            else -> AlliesError.LoadFailed
                        }
                    }

                    else -> AlliesError.LoadFailed
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    error = if (append) null else loadError,
                    loadMoreError = if (append) loadError else null
                )
            }
        }
    }
}