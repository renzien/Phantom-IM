package com.renzien.phantomim.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.renzien.phantomim.R
import com.renzien.phantomim.ui.components.PhantomBackground
import com.renzien.phantomim.ui.components.PhantomButton
import com.renzien.phantomim.ui.components.PhantomLogo
import com.renzien.phantomim.ui.components.PhantomTextButton
import com.renzien.phantomim.ui.components.PhantomTextField
import com.renzien.phantomim.ui.theme.PhantomBlack
import com.renzien.phantomim.ui.theme.PhantomWhite
import com.renzien.phantomim.ui.viewmodel.auth.ProfileSaveError

@Composable
fun CompleteProfileScreen(
    usernameState: TextFieldState,
    isSaving: Boolean,
    error: ProfileSaveError?,
    onContinueClick: () -> Unit,
    onSignOutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    val titleStyle = MaterialTheme.typography.displaySmall.copy(
        fontSize = 40.sp,
        lineHeight = 44.sp,
        letterSpacing = (-1).sp
    )

    val errorMessageRes = when (error) {
        ProfileSaveError.InvalidUsername ->
            R.string.profile_error_username_invalid

        ProfileSaveError.UsernameTaken ->
            R.string.profile_error_username_taken

        ProfileSaveError.Network ->
            R.string.auth_error_network

        ProfileSaveError.SaveFailed ->
            R.string.profile_error_save_failed

        null -> null
    }

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
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    PhantomLogo(
                        modifier = Modifier
                            .width(80.dp)
                            .rotate(-6f)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Column(
                        modifier = Modifier.rotate(-4f)
                    ) {
                        Text(
                            text = stringResource(
                                R.string.profile_setup_title_top
                            ),
                            style = titleStyle,
                            color = PhantomWhite
                        )

                        Text(
                            text = stringResource(
                                R.string.profile_setup_title_bottom
                            ),
                            style = titleStyle,
                            color = PhantomWhite,
                            modifier = Modifier
                                .background(PhantomBlack)
                                .padding(
                                    horizontal = 6.dp,
                                    vertical = 2.dp
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    PhantomTextField(
                        state = usernameState,
                        label = stringResource(
                            R.string.auth_username_label
                        ),
                        placeholder = stringResource(
                            R.string.auth_username_placeholder
                        ),
                        enabled = !isSaving,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.None,
                            autoCorrectEnabled = false,
                            imeAction = ImeAction.Done
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = stringResource(
                            R.string.profile_setup_hint
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = PhantomWhite
                    )

                    if (errorMessageRes != null) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = stringResource(errorMessageRes),
                            style = MaterialTheme.typography.bodyMedium,
                            color = PhantomWhite,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(PhantomBlack)
                                .padding(12.dp)
                                .semantics {
                                    liveRegion = LiveRegionMode.Polite
                                }
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PhantomButton(
                        text = stringResource(
                            if (isSaving) {
                                R.string.profile_setup_saving
                            } else {
                                R.string.profile_setup_continue
                            }
                        ),
                        onClick = {
                            focusManager.clearFocus(force = true)
                            onContinueClick()
                        },
                        enabled = !isSaving,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    PhantomTextButton(
                        text = stringResource(R.string.auth_sign_out),
                        onClick = onSignOutClick,
                        enabled = !isSaving,
                        modifier = Modifier.align(
                            Alignment.CenterHorizontally
                        )
                    )
                }
            }
        }
    }
}