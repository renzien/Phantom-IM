package com.renzien.phantomim.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.renzien.phantomim.R
import com.renzien.phantomim.ui.theme.PhantomBlack
import com.renzien.phantomim.ui.theme.PhantomIMTheme
import com.renzien.phantomim.ui.theme.PhantomWhite
import com.renzien.phantomim.ui.theme.PhantomYellow

@Composable
fun PhantomTextField(
    state: TextFieldState,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
){
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = PhantomWhite,
            modifier = Modifier
                .rotate(-2f)
                .background(PhantomBlack)
                .padding(horizontal = 8.dp, vertical = 3.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))
        CompositionLocalProvider(
            LocalTextSelectionColors provides TextSelectionColors(
                handleColor = PhantomBlack,
                backgroundColor = PhantomYellow.copy(alpha = 0.45f)
            )
        ) {
            BasicTextField(
                state = state,
                keyboardOptions = keyboardOptions,
                lineLimits = TextFieldLineLimits.SingleLine,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = PhantomBlack
                ),
                cursorBrush = SolidColor(PhantomBlack),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .drawBehind {
                        val shadowOffset = 4.dp.toPx()

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
                    .border(width = 3.dp, color = PhantomBlack)
                    .semantics {
                        contentDescription = label
                    },
                decorator = { innerTextField ->
                    Box(
                        modifier = Modifier.padding(12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (state.text.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = MaterialTheme.typography.bodyLarge,
                                color = PhantomBlack.copy(alpha = 0.55f)
                            )
                        }

                        innerTextField()
                    }
                }
            )
        }
    }
}

@Preview(widthDp = 360, heightDp = 200)
@Composable
private fun PhantomTextFieldPreview(){
    val emailState = rememberTextFieldState()

    PhantomIMTheme {
        PhantomBackground {
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
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp)
            )
        }
    }
}