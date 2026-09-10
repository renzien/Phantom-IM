package com.renzien.phantomim

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.key
import com.renzien.phantomim.ui.screens.auth.CompleteProfileScreen
import com.renzien.phantomim.ui.viewmodel.auth.ProfileStatus
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.renzien.phantomim.ui.navigation.PhantomAuthFlow
import com.renzien.phantomim.ui.screens.auth.AuthSuccessScreen
import com.renzien.phantomim.ui.theme.PhantomIMTheme
import com.renzien.phantomim.ui.viewmodel.auth.AuthViewModel
import com.renzien.phantomim.ui.navigation.PhantomMainFlow

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val authViewModel: AuthViewModel = viewModel()
            val authState by authViewModel.uiState
                .collectAsStateWithLifecycle()

            PhantomIMTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    val contentModifier = Modifier
                        .padding(innerPadding)
                        .consumeWindowInsets(innerPadding)

                    val currentProfile = authState.profile

                    when {
                        authState.userId == null -> {
                            PhantomAuthFlow(
                                authState = authState,
                                onSignInClick = authViewModel::signIn,
                                onCreateAccountClick = authViewModel::createAccount,
                                onDismissAuthError = authViewModel::clearError,
                                modifier = contentModifier
                            )
                        }

                        authState.profileStatus == ProfileStatus.Missing -> {
                            key(authState.userId) {
                                val usernameState = rememberTextFieldState(
                                    initialText = authState.usernameDraft
                                )

                                CompleteProfileScreen(
                                    usernameState = usernameState,
                                    isSaving = authState.isSavingProfile,
                                    error = authState.profileSaveError,
                                    onContinueClick = {
                                        authViewModel.completeProfile(
                                            username = usernameState.text.toString()
                                        )
                                    },
                                    onSignOutClick = authViewModel::signOut,
                                    modifier = contentModifier
                                )
                            }
                        }

                        authState.profileStatus == ProfileStatus.Ready &&
                                currentProfile != null -> {
                            key(currentProfile.uid) {
                                PhantomMainFlow(
                                    profile = currentProfile,
                                    onSignOutClick = authViewModel::signOut,
                                    modifier = contentModifier
                                )
                            }
                        }

                        else -> {
                            AuthSuccessScreen(
                                profileStatus = authState.profileStatus,
                                onRetryProfileClick = authViewModel::loadProfile,
                                onSignOutClick = authViewModel::signOut,
                                modifier = contentModifier
                            )
                        }
                    }
                }
            }
        }
    }
}