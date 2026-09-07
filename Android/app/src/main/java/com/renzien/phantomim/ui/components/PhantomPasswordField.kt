package com.renzien.phantomim.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.renzien.phantomim.R
import com.renzien.phantomim.ui.theme.PhantomBlack
import com.renzien.phantomim.ui.theme.PhantomWhite
import com.renzien.phantomim.ui.theme.PhantomYellow

@Composable
fun PhantomPasswordField(
    state: TextFieldState,
    modifier: Modifier = Modifier
) {
    var passwordVisible by remember { mutableStateOf(false) }

    val label = stringResource(R.string.auth_password_label)

    val toggleText = stringResource(
        if (passwordVisible) R.string.auth_hide_password
        else R.string.auth_show_password
    )

    val toggleDescription = stringResource(
        if (passwordVisible) R.string.auth_hide_password_description
        else R.string.auth_show_password_description
    )

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
            BasicSecureTextField(
                state = state,
                textObfuscationMode = if (passwordVisible) {
                    TextObfuscationMode.Visible
                } else {
                    TextObfuscationMode.Hidden
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    autoCorrectEnabled = false,
                    imeAction = ImeAction.Done
                ),
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
                    Row(
                        modifier = Modifier.padding(
                            start = 12.dp,
                            end = 4.dp,
                            top = 4.dp,
                            bottom = 4.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (state.text.isEmpty()) {
                                Text(
                                    text = stringResource(
                                        R.string.auth_password_placeholder
                                    ),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = PhantomBlack.copy(alpha = 0.55f)
                                )
                            }

                            innerTextField()
                        }

                        TextButton(
                            onClick = {
                                passwordVisible = !passwordVisible
                            },
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .semantics {
                                    contentDescription = toggleDescription
                                },
                            shape = RectangleShape,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = PhantomBlack
                            ),
                            contentPadding = PaddingValues(
                                horizontal = 8.dp,
                                vertical = 4.dp
                            )
                        ) {
                            Text(
                                text = toggleText,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            )
        }
    }
}