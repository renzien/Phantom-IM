package com.renzien.phantomim.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import com.renzien.phantomim.ui.components.PhantomTransition
import com.renzien.phantomim.ui.screens.auth.LoginScreen
import com.renzien.phantomim.ui.screens.onboarding.OnboardingScreen
import kotlinx.coroutines.launch

private enum class AuthScreen {
    Onboarding,
    Login
}

@Composable
fun PhantomAuthFlow(
    onGetStartedClick: () -> Unit,
    onSignInClick: () -> Unit,
    onSignUpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentScreen by rememberSaveable {
        mutableStateOf(AuthScreen.Onboarding)
    }

    var isTransitioning by remember {
        mutableStateOf(false)
    }

    var reverseTransition by remember {
        mutableStateOf(false)
    }

    val progress = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    fun navigateTo(destination: AuthScreen) {
        if (isTransitioning || currentScreen == destination) {
            return
        }

        isTransitioning = true
        reverseTransition = destination == AuthScreen.Onboarding
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

                // Switch screens while fully covered.
                currentScreen = destination

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
        enabled = currentScreen == AuthScreen.Login || isTransitioning
    ) {
        navigateTo(AuthScreen.Onboarding)
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        when (currentScreen) {
            AuthScreen.Onboarding -> {
                OnboardingScreen(
                    onGetStartedClick = {
                        if (!isTransitioning) {
                            onGetStartedClick()
                        }
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
                    onSignInClick = {
                        if (!isTransitioning) {
                            onSignInClick()
                        }
                    },
                    onSignUpClick = {
                        if (!isTransitioning) {
                            onSignUpClick()
                        }
                    }
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