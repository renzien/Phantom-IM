package com.renzien.phantomim.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.renzien.phantomim.R
import com.renzien.phantomim.ui.components.PhantomBackground
import com.renzien.phantomim.ui.components.PhantomLogo
import com.renzien.phantomim.ui.components.PhantomTextField
import com.renzien.phantomim.ui.theme.PhantomBlack
import com.renzien.phantomim.ui.theme.PhantomIMTheme
import com.renzien.phantomim.ui.theme.PhantomWhite

@Composable
fun LoginScreen(
    emailState: TextFieldState,
    modifier: Modifier = Modifier
){
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
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .widthIn(max = 420.dp)
                .fillMaxWidth()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
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
                    text = stringResource(R.string.login_title_top),
                    style = titleStyle,
                    color = PhantomWhite
                )

                Text(
                    text = stringResource(R.string.login_title_bottom),
                    style = titleStyle,
                    modifier = Modifier
                        .background(PhantomBlack)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            PhantomTextField(
                state = emailState,
                label = stringResource(R.string.auth_email_label),
                placeholder = stringResource(
                    R.string.auth_email_placeholder
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(widthDp = 360, heightDp = 760)
@Composable
private fun LoginScreenPreview() {
    val emailState = rememberTextFieldState()

    PhantomIMTheme {
        Scaffold { innerPadding ->
            LoginScreen(
                emailState = emailState,
                modifier = Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
            )
        }
    }
}