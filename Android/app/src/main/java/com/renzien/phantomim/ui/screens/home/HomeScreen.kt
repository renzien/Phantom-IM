package com.renzien.phantomim.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.renzien.phantomim.R
import com.renzien.phantomim.ui.components.PhantomBackground
import com.renzien.phantomim.ui.components.PhantomEmptyState
import com.renzien.phantomim.ui.components.PhantomLogo
import com.renzien.phantomim.ui.components.PhantomTextButton
import com.renzien.phantomim.ui.theme.PhantomBlack
import com.renzien.phantomim.ui.theme.PhantomWhite
import com.renzien.phantomim.ui.components.PhantomButton

@Composable
fun HomeScreen(
    username: String,
    onFindAlliesClick: () -> Unit,
    onSignOutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    PhantomBackground(modifier = modifier) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        PhantomLogo(
                            modifier = Modifier
                                .width(64.dp)
                                .rotate(-6f)
                        )

                        Text(
                            text = stringResource(
                                R.string.home_username,
                                username
                            ),
                            style = MaterialTheme.typography.titleMedium,
                            color = PhantomWhite,
                            textAlign = TextAlign.End,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = stringResource(R.string.home_title),
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 60.sp,
                            lineHeight = 64.sp,
                            letterSpacing = (-2).sp
                        ),
                        color = PhantomBlack,
                        modifier = Modifier
                            .rotate(-4f)
                            .background(PhantomWhite)
                            .padding(
                                horizontal = 10.dp,
                                vertical = 2.dp
                            )
                    )
                }

                PhantomEmptyState(
                    title = stringResource(
                        R.string.home_empty_title
                    ),
                    description = stringResource(
                        R.string.home_empty_description
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp)
                )

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PhantomButton(
                        text = stringResource(R.string.find_allies_title),
                        onClick = onFindAlliesClick,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    PhantomTextButton(
                        text = stringResource(R.string.auth_sign_out),
                        onClick = onSignOutClick,
                        modifier = Modifier.align(
                            Alignment.CenterHorizontally
                        )
                    )
                }
            }
        }
    }
}