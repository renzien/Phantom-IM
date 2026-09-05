package com.renzien.phantomim.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.renzien.phantomim.R
import com.renzien.phantomim.ui.components.OnboardingCharacters
import com.renzien.phantomim.ui.components.PhantomBackground
import com.renzien.phantomim.ui.components.PhantomLogo
import com.renzien.phantomim.ui.theme.PhantomBlack
import com.renzien.phantomim.ui.theme.PhantomWhite

@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier
){
    val titleStyle = MaterialTheme.typography.displaySmall.copy(
        fontSize = 44.sp,
        lineHeight = 48.sp,
        letterSpacing = (-1).sp
    )

    PhantomBackground(modifier = modifier) {
        Column(
            modifier = modifier
                .align(Alignment.TopCenter)
                .widthIn(max = 420.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            PhantomLogo(
                modifier = modifier
                    .width(126.dp)
                    .rotate(-6f)
            )

            Spacer(modifier = Modifier.height(24.dp))
            Column(
                modifier = Modifier.rotate(-5f)
            ) {
                Text(
                    text = stringResource(R.string.onboarding_title_top),
                    style = titleStyle,
                    color = PhantomWhite
                )

                Text(
                    text = stringResource(R.string.onboarding_title_bottom),
                    style = titleStyle,
                    color = PhantomBlack,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .drawBehind {
                            val shadowOffset = 6.dp.toPx()

                            drawRect(
                                color = PhantomBlack,
                                topLeft = Offset(
                                    shadowOffset,
                                    shadowOffset
                                ),
                                size = size
                            )
                        }
                        .background(PhantomWhite)
                        .padding(horizontal = 10.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            OnboardingCharacters(
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}