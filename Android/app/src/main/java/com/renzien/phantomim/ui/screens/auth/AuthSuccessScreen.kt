package com.renzien.phantomim.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp
import com.renzien.phantomim.R
import com.renzien.phantomim.ui.components.PhantomBackground
import com.renzien.phantomim.ui.components.PhantomButton
import com.renzien.phantomim.ui.components.PhantomLogo
import com.renzien.phantomim.ui.theme.PhantomWhite

@Composable
fun AuthSuccessScreen(
    onSignOutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    PhantomBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .widthIn(max = 420.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            PhantomLogo(
                modifier = Modifier
                    .width(80.dp)
                    .rotate(-6f)
            )

            Text(
                text = stringResource(R.string.auth_signed_in),
                style = MaterialTheme.typography.headlineLarge,
                color = PhantomWhite
            )

            PhantomButton(
                text = stringResource(R.string.auth_sign_out),
                onClick = onSignOutClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}