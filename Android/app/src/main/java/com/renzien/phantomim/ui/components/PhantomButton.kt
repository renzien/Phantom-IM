package com.renzien.phantomim.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.renzien.phantomim.ui.theme.PhantomBlack
import com.renzien.phantomim.ui.theme.PhantomWhite

private val PhantomButtonShape = GenericShape { size, _ ->
    moveTo(size.width * 0.02f, size.height * 0.09f)
    lineTo(size.width, 0f)
    lineTo(size.width * 0.96f, size.height * 0.96f)
    lineTo(0f, size.height)
    close()
}

@Composable
fun PhantomButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .heightIn(min = 56.dp)
            .rotate(-2f),
        shape = PhantomButtonShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = PhantomWhite,
            contentColor = PhantomBlack,
            disabledContainerColor = PhantomWhite.copy(alpha = 0.5f),
            disabledContentColor = PhantomBlack.copy(alpha = 0.6f)
        ),
        contentPadding = PaddingValues(
            horizontal = 20.dp,
            vertical = 14.dp
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontSize = 20.sp,
            lineHeight = 24.sp,
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp)
        )

        val arrowColor = LocalContentColor.current

        Canvas(
            modifier = Modifier.size(20.dp)
        ) {
            val w = size.width
            val h = size.height

            val arrow = Path().apply {
                moveTo(w * 0.20f, h * 0.80f)
                lineTo(w * 0.80f, h * 0.20f)

                moveTo(w * 0.20f, h * 0.20f)
                lineTo(w * 0.80f, h * 0.20f)
                lineTo(w * 0.80f, h * 0.80f)
            }

            drawPath(
                path = arrow,
                color = arrowColor,
                style = Stroke(
                    width = 2.dp.toPx(),
                    cap = StrokeCap.Square
                )
            )
        }
    }
}