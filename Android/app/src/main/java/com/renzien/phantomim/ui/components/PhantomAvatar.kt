package com.renzien.phantomim.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.renzien.phantomim.ui.theme.PhantomBlack
import com.renzien.phantomim.ui.theme.PhantomWhite

private val PortraitImageShape = GenericShape { size, _ ->
    moveTo(0f, 0f)
    lineTo(size.width, 0f)
    lineTo(size.width, size.height * 0.88f)
    lineTo(size.width * 0.15f, size.height)
    close()
}

@Composable
fun PhantomAvatar(
    @DrawableRes imageRes: Int,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
){
    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(92f / 87f)
            .drawBehind {
                val w = size.width
                val h = size.height

                val blackFrame = Path().apply {
                    moveTo(0f, h * 0.19f)
                    lineTo(w * 0.91f, h * 0.30f)
                    lineTo(w, h * 0.81f)
                    lineTo(w * 0.30f, h)
                    close()
                }

                val whiteFrame = Path().apply {
                    moveTo(w * 0.15f, h * 0.23f)
                    lineTo(w * 0.88f, h * 0.34f)
                    lineTo(w * 0.97f, h * 0.78f)
                    lineTo(w * 0.34f, h * 0.89f)
                    close()
                }

                val colorPanel = Path().apply {
                    moveTo(w * 0.20f, h * 0.31f)
                    lineTo(w * 0.86f, h * 0.35f)
                    lineTo(w * 0.95f, h * 0.75f)
                    lineTo(w * 0.36f, h * 0.85f)
                    close()
                }

                drawPath(blackFrame, PhantomBlack)
                drawPath(whiteFrame, PhantomWhite)
                drawPath(colorPanel, backgroundColor)
            }
    ) {
        val potraitWidth = maxWidth * (74f / 92f)
        val potraitInset = maxWidth * (6f / 92f)

        Image(
            painter = painterResource(imageRes),
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = -potraitInset)
                .width(potraitWidth)
                .aspectRatio(1f)
                .clip(PortraitImageShape)
        )
    }
}