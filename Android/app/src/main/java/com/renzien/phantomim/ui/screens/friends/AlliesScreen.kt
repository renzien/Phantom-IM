package com.renzien.phantomim.ui.screens.friends

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.renzien.phantomim.R
import com.renzien.phantomim.ui.components.PhantomAllyRow
import com.renzien.phantomim.ui.components.PhantomBackground
import com.renzien.phantomim.ui.components.PhantomButton
import com.renzien.phantomim.ui.components.PhantomEmptyState
import com.renzien.phantomim.ui.components.PhantomLogo
import com.renzien.phantomim.ui.components.PhantomTextButton
import com.renzien.phantomim.ui.theme.PhantomBlack
import com.renzien.phantomim.ui.theme.PhantomWhite
import com.renzien.phantomim.ui.viewmodel.friends.AlliesError
import com.renzien.phantomim.ui.viewmodel.friends.AlliesUiState

@Composable
fun AlliesScreen(
    uiState: AlliesUiState,
    onRefreshClick: () -> Unit,
    onLoadMoreClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val error = uiState.error ?: uiState.loadMoreError

    val errorMessageRes = when (error) {
        AlliesError.Network -> R.string.auth_error_network
        AlliesError.LoadFailed -> R.string.allies_load_failed
        null -> null
    }

    PhantomBackground(modifier = modifier) {
        LazyColumn(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .widthIn(max = 420.dp)
                .fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item(key = "header") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PhantomTextButton(
                        text = stringResource(R.string.find_allies_back),
                        onClick = onBackClick
                    )

                    PhantomLogo(
                        modifier = Modifier
                            .width(64.dp)
                            .rotate(-6f)
                    )
                }
            }

            item(key = "title") {
                Text(
                    text = stringResource(R.string.allies_title),
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontSize = 40.sp,
                        lineHeight = 44.sp,
                        letterSpacing = (-1).sp
                    ),
                    color = PhantomWhite,
                    modifier = Modifier
                        .rotate(-4f)
                        .background(PhantomBlack)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            item(key = "refresh") {
                PhantomTextButton(
                    text = stringResource(R.string.allies_refresh),
                    onClick = onRefreshClick,
                    enabled = !uiState.isBusy
                )
            }

            if (
                uiState.isLoading ||
                (!uiState.hasLoaded && errorMessageRes == null)
            ) {
                item(key = "loading") {
                    AlliesMessage(
                        text = stringResource(R.string.allies_loading)
                    )
                }
            }

            if (
                uiState.hasLoaded &&
                uiState.profiles.isEmpty() &&
                !uiState.isBusy &&
                errorMessageRes == null
            ) {
                item(key = "empty") {
                    PhantomEmptyState(
                        title = stringResource(
                            R.string.allies_empty_title
                        ),
                        description = stringResource(
                            R.string.allies_empty_description
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            items(
                items = uiState.profiles,
                key = { profile -> "ally_${profile.uid}" }
            ) { profile ->
                PhantomAllyRow(
                    username = profile.username,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (errorMessageRes != null) {
                item(key = "error") {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AlliesMessage(
                            text = stringResource(errorMessageRes)
                        )

                        PhantomButton(
                            text = stringResource(R.string.allies_retry),
                            onClick = {
                                if (uiState.error != null) {
                                    onRefreshClick()
                                } else {
                                    onLoadMoreClick()
                                }
                            },
                            enabled = !uiState.isBusy,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            if (uiState.hasMore && errorMessageRes == null) {
                item(key = "load_more") {
                    PhantomButton(
                        text = stringResource(
                            if (uiState.isLoadingMore) {
                                R.string.allies_loading_more
                            } else {
                                R.string.allies_load_more
                            }
                        ),
                        onClick = onLoadMoreClick,
                        enabled = !uiState.isBusy,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun AlliesMessage(
    text: String
) {
    Text(
        text = text,
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