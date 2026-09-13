package com.renzien.phantomim.ui.screens.friends

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.renzien.phantomim.ui.components.PhantomEmptyState
import com.renzien.phantomim.ui.components.PhantomLogo
import com.renzien.phantomim.ui.components.PhantomTextButton
import com.renzien.phantomim.ui.components.PhantomTextField
import com.renzien.phantomim.ui.theme.PhantomBlack
import com.renzien.phantomim.ui.theme.PhantomWhite
import com.renzien.phantomim.ui.viewmodel.friends.FindAlliesStatus
import com.renzien.phantomim.ui.viewmodel.friends.FindAlliesUiState
import com.renzien.phantomim.ui.viewmodel.friends.AddAllyStatus

@Composable
fun FindAlliesScreen(
    usernameState: TextFieldState,
    uiState: FindAlliesUiState,
    onSearchClick: () -> Unit,
    onAddAllyClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val isLoading = uiState.status == FindAlliesStatus.Loading
    val foundProfile = uiState.profile

    val isBusy = isLoading || uiState.isAdding

    val queryMatches = usernameState.text.toString()
        .trim()
        .removePrefix("@")
        .equals(uiState.query, ignoreCase = true)

    val messageRes = when (uiState.status) {
        FindAlliesStatus.OwnAccount ->
            R.string.find_allies_own_account

        FindAlliesStatus.InvalidUsername ->
            R.string.profile_setup_hint

        FindAlliesStatus.NetworkError ->
            R.string.auth_error_network

        FindAlliesStatus.Failed ->
            R.string.find_allies_failed

        else -> null
    }

    PhantomBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .widthIn(max = 420.dp)
                .fillMaxWidth()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PhantomTextButton(
                    text = stringResource(R.string.find_allies_back),
                    onClick = onBackClick,
                    enabled = !uiState.isAdding
                )

                PhantomLogo(
                    modifier = Modifier
                        .width(64.dp)
                        .rotate(-6f)
                )
            }

            Text(
                text = stringResource(R.string.find_allies_title),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontSize = 40.sp,
                    lineHeight = 44.sp,
                    letterSpacing = (-1).sp
                ),
                color = PhantomWhite,
                modifier = Modifier
                    .rotate(-4f)
                    .background(PhantomBlack)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )

            PhantomTextField(
                state = usernameState,
                enabled = !isBusy,
                label = stringResource(R.string.auth_username_label),
                placeholder = stringResource(
                    R.string.find_allies_placeholder
                ),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrectEnabled = false,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = stringResource(R.string.find_allies_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = PhantomWhite
            )

            PhantomButton(
                text = stringResource(
                    if (isLoading) {
                        R.string.find_allies_searching
                    } else {
                        R.string.find_allies_search
                    }
                ),
                onClick = {
                    focusManager.clearFocus(force = true)
                    onSearchClick()
                },
                enabled = !isBusy,
                modifier = Modifier.fillMaxWidth()
            )

            if (
                uiState.status == FindAlliesStatus.Found ||
                uiState.status == FindAlliesStatus.NotFound
            ) {
                Text(
                    text = stringResource(
                        R.string.find_allies_result_for,
                        uiState.query
                    ),
                    style = MaterialTheme.typography.labelLarge,
                    color = PhantomWhite
                )
            }

            if (
                uiState.status == FindAlliesStatus.Found &&
                foundProfile != null
            ) {
                val isAdded =
                    uiState.addAllyStatus == AddAllyStatus.Added ||
                            uiState.addAllyStatus == AddAllyStatus.AlreadyAdded

                val descriptionRes = if (!queryMatches) {
                    R.string.find_allies_search_again
                } else {
                    when (uiState.addAllyStatus) {
                        AddAllyStatus.Idle ->
                            R.string.find_allies_found

                        AddAllyStatus.Adding ->
                            R.string.find_allies_adding

                        AddAllyStatus.Added ->
                            R.string.find_allies_add_success

                        AddAllyStatus.AlreadyAdded ->
                            R.string.find_allies_already_added

                        AddAllyStatus.NetworkError ->
                            R.string.auth_error_network

                        AddAllyStatus.Failed ->
                            R.string.find_allies_add_failed
                    }
                }

                PhantomEmptyState(
                    title = stringResource(
                        R.string.home_username,
                        foundProfile.username
                    ),
                    description = stringResource(descriptionRes),
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics {
                            liveRegion = LiveRegionMode.Polite
                        }
                )

                PhantomButton(
                    text = stringResource(
                        when {
                            uiState.isAdding ->
                                R.string.find_allies_adding

                            isAdded ->
                                R.string.find_allies_added

                            else ->
                                R.string.find_allies_add
                        }
                    ),
                    onClick = {
                        focusManager.clearFocus(force = true)
                        onAddAllyClick()
                    },
                    enabled = !isBusy && queryMatches && !isAdded,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (uiState.status == FindAlliesStatus.NotFound) {
                PhantomEmptyState(
                    title = stringResource(
                        R.string.find_allies_not_found_title
                    ),
                    description = stringResource(
                        R.string.find_allies_not_found_message
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (messageRes != null) {
                Text(
                    text = stringResource(messageRes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = PhantomWhite,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PhantomBlack)
                        .padding(16.dp)
                        .semantics {
                            liveRegion = LiveRegionMode.Polite
                        }
                )
            }
        }
    }
}