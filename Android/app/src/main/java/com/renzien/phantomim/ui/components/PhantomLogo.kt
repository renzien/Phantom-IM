package com.renzien.phantomim.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.renzien.phantomim.R

@Composable
fun PhantomLogo(
    modifier: Modifier = Modifier
){
    Image(
        painter = painterResource(R.drawable.logo_im),
        contentDescription = stringResource(R.string.app_name),
        contentScale = ContentScale.Fit,
        modifier = modifier.aspectRatio(218f / 170f)
    )
}