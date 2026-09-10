package com.renzien.phantomim.ui.viewmodel.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FirebaseFirestoreException
import com.renzien.phantomim.data.repository.UserRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FindAlliesViewModel(
    private val repository: UserRepository = UserRepository()
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
        if (_uiState.value.status == FindAlliesStatus.Loading) {
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

    fun reset() {
        searchJob?.cancel()
        searchJob = null
        _uiState.value = FindAlliesUiState()
    }
}