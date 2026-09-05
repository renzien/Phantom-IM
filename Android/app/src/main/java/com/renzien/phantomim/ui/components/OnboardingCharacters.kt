package com.renzien.phantomim.ui.components

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.zIndex
import com.renzien.phantomim.R
import com.renzien.phantomim.ui.theme.PhantomBlack
import com.renzien.phantomim.ui.theme.PhantomBlue
import com.renzien.phantomim.ui.theme.PhantomPink
import com.renzien.phantomim.ui.theme.PhantomYellow

@Composable
fun OnboardingCharacters(
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(1.6f)
            .drawBehind {
                val w = size.width
                val h = size.height

                val backgroundPanel = Path().apply {
                    moveTo(0f, h * 0.40f)
                    lineTo(w, h * 0.12f)
                    lineTo(w * 0.96f, h * 0.82f)
                    lineTo(w * 0.05f, h * 0.98f)
                    close()
                }

                drawPath(backgroundPanel, PhantomBlack)
            }
    ) {
        val avatarWidth = maxWidth * 0.43f

        PhantomAvatar(
            imageRes = R.drawable.ann,
            backgroundColor = PhantomPink,
            contentDescription = "Ann",
            modifier = Modifier
                .offset(
                    x = maxWidth * 0.03f,
                    y = maxHeight * 0.22f
                )
                .width(avatarWidth)
                .rotate(-8f)
        )

        PhantomAvatar(
            imageRes = R.drawable.ryuji,
            backgroundColor = PhantomYellow,
            contentDescription = "Ryuji",
            modifier = Modifier
                .offset(
                    x = maxWidth * 0.28f,
                    y = maxHeight * 0.06f
                )
                .width(avatarWidth)
                .rotate(8f)
                .zIndex(1f)
        )

        PhantomAvatar(
            imageRes = R.drawable.yusuke,
            backgroundColor = PhantomBlue,
            contentDescription = "Yusuke",
            modifier = Modifier
                .offset(
                    x = maxWidth * 0.54f,
                    y = maxHeight * 0.24f
                )
                .width(avatarWidth)
                .rotate(-4f)
        )
    }
}