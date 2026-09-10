package com.renzien.phantomim.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.renzien.phantomim.R
import com.renzien.phantomim.ui.components.PhantomBackground
import com.renzien.phantomim.ui.components.PhantomButton
import com.renzien.phantomim.ui.components.PhantomLogo
import com.renzien.phantomim.ui.components.PhantomPasswordField
import com.renzien.phantomim.ui.components.PhantomTextButton
import com.renzien.phantomim.ui.components.PhantomTextField
import com.renzien.phantomim.ui.theme.PhantomBlack
import com.renzien.phantomim.ui.theme.PhantomIMTheme
import com.renzien.phantomim.ui.theme.PhantomWhite
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(
    usernameState: TextFieldState,
    emailState: TextFieldState,
    passwordState: TextFieldState,
    onCreateAccountClick: () -> Unit,
    onSignInClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    val shadowOffset = with(LocalDensity.current) {
        4.dp.toPx()
    }

    val titleStyle = MaterialTheme.typography.displaySmall.copy(
        fontSize = 44.sp,
        lineHeight = 46.sp,
        letterSpacing = (-1).sp,
        shadow = Shadow(
            color = PhantomBlack,
            offset = Offset(shadowOffset, shadowOffset),
            blurRadius = 0f
        )
    )

    PhantomBackground(modifier = modifier) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            val availableHeight = maxHeight

            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .widthIn(max = 420.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .heightIn(min = availableHeight)
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Logo, title, and registration fields.
                Column {
                    PhantomLogo(
                        modifier = Modifier
                            .width(80.dp)
                            .rotate(-6f)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Column(
                        modifier = Modifier.rotate(-4f)
                    ) {
                        Text(
                            text = stringResource(R.string.signup_title_top),
                            style = titleStyle,
                            color = PhantomWhite
                        )

                        Text(
                            text = stringResource(R.string.signup_title_bottom),
                            style = titleStyle,
                            color = PhantomWhite,
                            modifier = Modifier
                                .background(PhantomBlack)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    PhantomTextField(
                        state = usernameState,
                        enabled = !isLoading,
                        label = stringResource(R.string.auth_username_label),
                        placeholder = stringResource(
                            R.string.auth_username_placeholder
                        ),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.None,
                            autoCorrectEnabled = false,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    PhantomTextField(
                        state = emailState,
                        enabled = !isLoading,
                        label = stringResource(R.string.auth_email_label),
                        placeholder = stringResource(
                            R.string.auth_email_placeholder
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    PhantomPasswordField(
                        state = passwordState,
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }

                // Bottom actions.
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PhantomButton(
                        text = stringResource(
                            if (isLoading) {
                                R.string.signup_creating_account
                            } else {
                                R.string.signup_create_account
                            }
                        ),
                        onClick = onCreateAccountClick,
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    PhantomTextButton(
                        text = stringResource(R.string.signup_sign_in),
                        onClick = onSignInClick,
                        enabled = !isLoading,
                        modifier = Modifier.align(
                            Alignment.CenterHorizontally
                        )
                    )
                }
            }
        }
    }
}

@Preview(widthDp = 360, heightDp = 760)
@Composable
private fun SignUpScreenPreview() {
    val usernameState = rememberTextFieldState()
    val emailState = rememberTextFieldState()
    val passwordState = remember { TextFieldState() }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val createAccountMessage = stringResource(
        R.string.auth_sign_up_pending
    )

    val signInMessage = stringResource(
        R.string.preview_sign_in_message
    )

    PhantomIMTheme {
        Scaffold(
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.imePadding()
                )
            }
        ) { innerPadding ->
            SignUpScreen(
                usernameState = usernameState,
                emailState = emailState,
                passwordState = passwordState,
                onCreateAccountClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar(createAccountMessage)
                    }
                },
                onSignInClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar(signInMessage)
                    }
                },
                modifier = Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
            )
        }
    }
}