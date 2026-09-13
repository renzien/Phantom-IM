package com.renzien.phantomim.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.renzien.phantomim.data.model.UserProfile
import com.renzien.phantomim.ui.screens.friends.AlliesScreen
import com.renzien.phantomim.ui.screens.friends.FindAlliesScreen
import com.renzien.phantomim.ui.screens.home.HomeScreen
import com.renzien.phantomim.ui.viewmodel.friends.AlliesViewModel
import com.renzien.phantomim.ui.viewmodel.friends.FindAlliesViewModel

private enum class MainScreen {
    Inbox,
    FindAllies,
    Allies
}

@Composable
fun PhantomMainFlow(
    profile: UserProfile,
    onSignOutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val findAlliesViewModel: FindAlliesViewModel = viewModel(
        key = "find_allies_${profile.uid}"
    )

    val alliesViewModel: AlliesViewModel = viewModel(
        key = "allies_${profile.uid}"
    )

    val searchState by findAlliesViewModel.uiState
        .collectAsStateWithLifecycle()

    val alliesState by alliesViewModel.uiState
        .collectAsStateWithLifecycle()

    var screen by rememberSaveable(profile.uid) {
        mutableStateOf(MainScreen.Inbox)
    }

    fun closeScreen() {
        if (findAlliesViewModel.uiState.value.isAdding) {
            return
        }

        findAlliesViewModel.reset()
        alliesViewModel.reset()
        screen = MainScreen.Inbox
    }

    BackHandler(enabled = screen != MainScreen.Inbox) {
        closeScreen()
    }

    when (screen) {
        MainScreen.Inbox -> {
            HomeScreen(
                username = profile.username,
                onFindAlliesClick = {
                    findAlliesViewModel.reset()
                    screen = MainScreen.FindAllies
                },
                onAlliesClick = {
                    alliesViewModel.reset()
                    screen = MainScreen.Allies
                },
                onSignOutClick = {
                    findAlliesViewModel.reset()
                    alliesViewModel.reset()
                    onSignOutClick()
                },
                modifier = modifier
            )
        }

        MainScreen.FindAllies -> {
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
                onAddAllyClick = {
                    findAlliesViewModel.addAlly(
                        currentUserId = profile.uid,
                        username = usernameState.text.toString()
                    )
                },
                onBackClick = {
                    closeScreen()
                },
                modifier = modifier
            )
        }

        MainScreen.Allies -> {
            LaunchedEffect(profile.uid) {
                alliesViewModel.load(
                    ownerUid = profile.uid
                )
            }

            AlliesScreen(
                uiState = alliesState,
                onRefreshClick = alliesViewModel::refresh,
                onLoadMoreClick = alliesViewModel::loadMore,
                onBackClick = {
                    closeScreen()
                },
                modifier = modifier
            )
        }
    }
}