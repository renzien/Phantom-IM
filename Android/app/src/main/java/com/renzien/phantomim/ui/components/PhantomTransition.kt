package com.renzien.phantomim.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.renzien.phantomim.ui.theme.PhantomBlack
import com.renzien.phantomim.ui.theme.PhantomWhite

@Composable
fun PhantomTransition(
    progress: () -> Float,
    reverse: Boolean = false,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
            .pointerInput(Unit) {
                // Block touches while the transition is visible.
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()

                        event.changes.forEach { change ->
                            change.consume()
                        }
                    }
                }
            }
    ) {
        val slant = size.width * 0.22f
        val edgeWidth = 6.dp.toPx()
        val panelWidth = size.width + slant * 2f + edgeWidth * 2f
        val travel = panelWidth + edgeWidth

        // 0: outside, 1: fully covered, 2: outside again.
        val left = (progress() - 1f) * travel - slant - edgeWidth
        val right = left + panelWidth

        scale(
            scaleX = if (reverse) -1f else 1f,
            scaleY = 1f
        ) {
            val panel = Path().apply {
                moveTo(left, 0f)
                lineTo(right, 0f)
                lineTo(right + slant, size.height)
                lineTo(left + slant, size.height)
                close()
            }

            drawPath(
                path = panel,
                color = PhantomBlack
            )

            drawLine(
                color = PhantomWhite,
                start = Offset(left, 0f),
                end = Offset(left + slant, size.height),
                strokeWidth = edgeWidth
            )

            drawLine(
                color = PhantomWhite,
                start = Offset(right, 0f),
                end = Offset(right + slant, size.height),
                strokeWidth = edgeWidth
            )
        }
    }
}