package com.renzien.phantomim.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.renzien.phantomim.R
import com.renzien.phantomim.ui.theme.PhantomRed

@Composable
fun PhantomBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
){
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PhantomRed)
    ) {
        Image(
            painter = painterResource(
                R.drawable.bg_splatter_background
            ),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier =  Modifier.matchParentSize()
        )

        content()
    }
}