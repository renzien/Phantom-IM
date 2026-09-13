package com.renzien.phantomim.ui.viewmodel.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FirebaseFirestoreException
import com.renzien.phantomim.data.repository.UserRepository
import com.renzien.phantomim.data.model.AddFriendResult
import com.renzien.phantomim.data.repository.FriendRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FindAlliesViewModel(
    private val repository: UserRepository = UserRepository(),
    private val friendRepository: FriendRepository =
        FriendRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(FindAlliesUiState())

    val uiState: StateFlow<FindAlliesUiState> =
        _uiState.asStateFlow()

    private val usernamePattern = Regex("[A-Za-z0-9_]{3,20}")

    private var searchJob: Job? = null

    fun search(
        username: String,
        currentUserId: String
    ) {
        if (
            _uiState.value.status == FindAlliesStatus.Loading ||
            _uiState.value.isAdding
        ) {
            return
        }
        val cleanUsername = username.trim().removePrefix("@")

        if (!usernamePattern.matches(cleanUsername)) {
            _uiState.value = FindAlliesUiState(
                status = FindAlliesStatus.InvalidUsername
            )
            return
        }

        _uiState.value = FindAlliesUiState(
            status = FindAlliesStatus.Loading,
            query = cleanUsername
        )

        searchJob = viewModelScope.launch {
            try {
                val profile = repository.findProfileByUsername(
                    username = cleanUsername
                )

                val status = when {
                    profile == null ->
                        FindAlliesStatus.NotFound

                    profile.uid == currentUserId ->
                        FindAlliesStatus.OwnAccount

                    else -> FindAlliesStatus.Found
                }

                _uiState.value = FindAlliesUiState(
                    status = status,
                    query = cleanUsername,
                    profile = if (status == FindAlliesStatus.Found) {
                        profile
                    } else {
                        null
                    }
                )
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                val status = when (error) {
                    is FirebaseNetworkException ->
                        FindAlliesStatus.NetworkError

                    is FirebaseFirestoreException -> {
                        when (error.code) {
                            FirebaseFirestoreException.Code.UNAVAILABLE,
                            FirebaseFirestoreException.Code.DEADLINE_EXCEEDED ->
                                FindAlliesStatus.NetworkError

                            else -> FindAlliesStatus.Failed
                        }
                    }

                    else -> FindAlliesStatus.Failed
                }

                _uiState.value = FindAlliesUiState(
                    status = status,
                    query = cleanUsername
                )
            }
        }
    }

    fun addAlly(
        currentUserId: String,
        username: String
    ) {
        val state = _uiState.value
        val profile = state.profile ?: return

        if (
            state.status != FindAlliesStatus.Found ||
            state.isAdding ||
            state.addAllyStatus == AddAllyStatus.Added ||
            state.addAllyStatus == AddAllyStatus.AlreadyAdded
        ) {
            return
        }

        val cleanUsername = username.trim().removePrefix("@")

        if (!cleanUsername.equals(state.query, ignoreCase = true)) {
            return
        }

        if (profile.uid == currentUserId) {
            return
        }

        _uiState.value = state.copy(
            addAllyStatus = AddAllyStatus.Adding
        )

        viewModelScope.launch {
            try {
                val result = friendRepository.addFriend(
                    ownerUid = currentUserId,
                    friendUid = profile.uid
                )

                _uiState.value = _uiState.value.copy(
                    addAllyStatus = when (result) {
                        AddFriendResult.Added ->
                            AddAllyStatus.Added

                        AddFriendResult.AlreadyAdded ->
                            AddAllyStatus.AlreadyAdded
                    }
                )
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                val status = when (error) {
                    is FirebaseNetworkException ->
                        AddAllyStatus.NetworkError

                    is FirebaseFirestoreException -> {
                        when (error.code) {
                            FirebaseFirestoreException.Code.UNAVAILABLE,
                            FirebaseFirestoreException.Code.DEADLINE_EXCEEDED ->
                                AddAllyStatus.NetworkError

                            else -> AddAllyStatus.Failed
                        }
                    }

                    else -> AddAllyStatus.Failed
                }

                _uiState.value = _uiState.value.copy(
                    addAllyStatus = status
                )
            }
        }
    }

    fun reset() {
        if (_uiState.value.isAdding) {
            return
        }

        searchJob?.cancel()
        searchJob = null
        _uiState.value = FindAlliesUiState()
    }
}