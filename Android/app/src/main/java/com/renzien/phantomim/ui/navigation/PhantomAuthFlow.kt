package com.renzien.phantomim.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import com.renzien.phantomim.ui.components.PhantomTransition
import com.renzien.phantomim.ui.screens.auth.LoginScreen
import com.renzien.phantomim.ui.screens.auth.SignUpScreen
import com.renzien.phantomim.ui.screens.onboarding.OnboardingScreen
import com.renzien.phantomim.ui.theme.PhantomBlack
import com.renzien.phantomim.ui.theme.PhantomWhite
import com.renzien.phantomim.ui.validation.AuthValidator
import com.renzien.phantomim.R
import com.renzien.phantomim.ui.components.PhantomTextButton
import com.renzien.phantomim.ui.viewmodel.auth.AuthError
import com.renzien.phantomim.ui.viewmodel.auth.AuthUiState
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job

private enum class AuthScreen {
    Onboarding,
    Login,
    SignUp
}

private val AuthBackStackSaver = listSaver<List<AuthScreen>, String>(
    save = { screens ->
        screens.map { it.name }
    },
    restore = { names ->
        names.map { AuthScreen.valueOf(it) }
    }
)

@Composable
fun PhantomAuthFlow(
    authState: AuthUiState,
    onSignInClick: (String, String) -> Unit,
    onCreateAccountClick: () -> Unit,
    onDismissAuthError: () -> Unit,
    modifier: Modifier = Modifier
) {
    var backStack by rememberSaveable(
        stateSaver = AuthBackStackSaver
    ) {
        mutableStateOf(listOf(AuthScreen.Onboarding))
    }

    val currentScreen = backStack.last()
    val screenStateHolder = rememberSaveableStateHolder()

    var isTransitioning by remember {
        mutableStateOf(false)
    }

    var reverseTransition by remember {
        mutableStateOf(false)
    }

    val progress = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val resources = LocalResources.current
    val snackbarHostState = remember { SnackbarHostState() }

    var validationJob by remember {
        mutableStateOf<Job?>(null)
    }

    fun submitForm(
        errorRes: Int?,
        onValid: () -> Unit
    ) {
        if (isTransitioning || authState.isLoading) {
            return
        }

        validationJob?.cancel()
        snackbarHostState.currentSnackbarData?.dismiss()
        onDismissAuthError()

        if (errorRes == null) {
            onValid()
            return
        }

        val message = resources.getString(errorRes)

        validationJob = scope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    fun navigateTo(destination: AuthScreen) {
        if (
            isTransitioning ||
            authState.isLoading ||
            currentScreen == destination
        ) {
            return
        }

        validationJob?.cancel()
        snackbarHostState.currentSnackbarData?.dismiss()
        onDismissAuthError()
        val existingIndex = backStack.indexOf(destination)

        // Return to an existing screen or add a new destination.
        val nextBackStack = if (existingIndex >= 0) {
            backStack.take(existingIndex + 1)
        } else {
            backStack + destination
        }

        isTransitioning = true
        reverseTransition = existingIndex >= 0
        focusManager.clearFocus(force = true)

        scope.launch {
            try {
                progress.snapTo(0f)

                // Accelerate smoothly as the panel enters.
                progress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = 320,
                        easing = CubicBezierEasing(
                            0.5f, 0f,
                            0.75f, 0.5f
                        )
                    )
                )

                // Update navigation while fully covered.
                backStack = nextBackStack

                // Match the incoming speed, then slow down.
                progress.animateTo(
                    targetValue = 2f,
                    animationSpec = tween(
                        durationMillis = 320,
                        easing = CubicBezierEasing(
                            0.25f, 0.5f,
                            0.5f, 1f
                        )
                    )
                )
            } finally {
                isTransitioning = false
            }
        }
    }

    BackHandler(
        enabled = backStack.size > 1 ||
                isTransitioning ||
                authState.isLoading
    ) {
        if (
            !isTransitioning &&
            !authState.isLoading &&
            backStack.size > 1
        ) {
            val previousScreen = backStack[backStack.lastIndex - 1]
            navigateTo(previousScreen)
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        screenStateHolder.SaveableStateProvider(
            key = currentScreen.name
        ) {
            when (currentScreen) {
                AuthScreen.Onboarding -> {
                    OnboardingScreen(
                        onGetStartedClick = {
                            navigateTo(AuthScreen.SignUp)
                        },
                        onSignInClick = {
                            navigateTo(AuthScreen.Login)
                        }
                    )
                }

                AuthScreen.Login -> {
                    val emailState = rememberTextFieldState()
                    val passwordState = remember { TextFieldState() }

                    LoginScreen(
                        emailState = emailState,
                        passwordState = passwordState,
                        isLoading = authState.isLoading,
                        onSignInClick = {
                            submitForm(
                                errorRes = AuthValidator.validateLogin(
                                    email = emailState.text,
                                    password = passwordState.text
                                ),
                                onValid = {
                                    focusManager.clearFocus(force = true)

                                    onSignInClick(
                                        emailState.text.toString(),
                                        passwordState.text.toString()
                                    )
                                }
                            )
                        },
                        onSignUpClick = {
                            navigateTo(AuthScreen.SignUp)
                        }
                    )
                }

                AuthScreen.SignUp -> {
                    val usernameState = rememberTextFieldState()
                    val emailState = rememberTextFieldState()
                    val passwordState = remember { TextFieldState() }

                    SignUpScreen(
                        usernameState = usernameState,
                        emailState = emailState,
                        passwordState = passwordState,
                        onCreateAccountClick = {
                            submitForm(
                                errorRes = AuthValidator.validateSignUp(
                                    username = usernameState.text,
                                    email = emailState.text,
                                    password = passwordState.text
                                ),
                                onValid = onCreateAccountClick
                            )
                        },
                        onSignInClick = {
                            navigateTo(AuthScreen.Login)
                        }
                    )
                }
            }
        }

        val authError = authState.error

        if (authError != null) {
            val messageRes = when (authError) {
                AuthError.Network ->
                    R.string.auth_error_network

                AuthError.TooManyRequests ->
                    R.string.auth_error_too_many_requests

                AuthError.PasswordRejected ->
                    R.string.auth_error_password_rejected

                AuthError.SignInFailed ->
                    R.string.auth_error_sign_in_failed

                AuthError.SignUpFailed ->
                    R.string.auth_error_sign_up_failed
            }

            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .imePadding(),
                dismissAction = {
                    PhantomTextButton(
                        text = stringResource(R.string.auth_dismiss),
                        onClick = onDismissAuthError
                    )
                },
                shape = RectangleShape,
                containerColor = PhantomBlack,
                contentColor = PhantomWhite
            ) {
                Text(text = stringResource(messageRes))
            }
        } else {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .imePadding()
            ) { data ->
                Snackbar(
                    snackbarData = data,
                    shape = RectangleShape,
                    containerColor = PhantomBlack,
                    contentColor = PhantomWhite
                )
            }
        }

        if (isTransitioning) {
            PhantomTransition(
                progress = { progress.value },
                reverse = reverseTransition
            )
        }
    }
}