package com.renzien.phantomim.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.renzien.phantomim.ui.theme.PhantomWhite

@Composable
fun PhantomTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
){
    TextButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.heightIn(min = 48.dp),
        shape = RectangleShape,
        colors = ButtonDefaults.textButtonColors(
            contentColor = PhantomWhite,
            disabledContentColor = PhantomWhite.copy(alpha = 0.5f)
        ),
        contentPadding = PaddingValues(
            horizontal = 0.dp,
            vertical = 12.dp
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            textDecoration = TextDecoration.Underline
        )
    }
}