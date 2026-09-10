package com.renzien.phantomim.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.renzien.phantomim.data.model.UserProfile
import com.renzien.phantomim.ui.screens.friends.FindAlliesScreen
import com.renzien.phantomim.ui.screens.home.HomeScreen
import com.renzien.phantomim.ui.viewmodel.friends.FindAlliesViewModel

@Composable
fun PhantomMainFlow(
    profile: UserProfile,
    onSignOutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val findAlliesViewModel: FindAlliesViewModel = viewModel()

    val searchState by findAlliesViewModel.uiState
        .collectAsStateWithLifecycle()

    var showFindAllies by rememberSaveable(profile.uid) {
        mutableStateOf(false)
    }

    fun closeSearch() {
        findAlliesViewModel.reset()
        showFindAllies = false
    }

    BackHandler(enabled = showFindAllies) {
        closeSearch()
    }

    if (showFindAllies) {
        val usernameState = rememberTextFieldState()

        FindAlliesScreen(
            usernameState = usernameState,
            uiState = searchState,
            onSearchClick = {
                findAlliesViewModel.search(
                    username = usernameState.text.toString(),
                    currentUserId = profile.uid
                )
            },
            onBackClick = {
                closeSearch()
            },
            modifier = modifier
        )
    } else {
        HomeScreen(
            username = profile.username,
            onFindAlliesClick = {
                findAlliesViewModel.reset()
                showFindAllies = true
            },
            onSignOutClick = {
                findAlliesViewModel.reset()
                onSignOutClick()
            },
            modifier = modifier
        )
    }
}