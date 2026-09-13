package com.renzien.phantomim.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.renzien.phantomim.R
import com.renzien.phantomim.ui.theme.PhantomBlack
import com.renzien.phantomim.ui.theme.PhantomWhite
import com.renzien.phantomim.ui.theme.PhantomYellow
import java.util.Locale

@Composable
fun PhantomAllyRow(
    username: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .rotate(-1f)
            .background(PhantomBlack)
            .border(2.dp, PhantomWhite)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .rotate(-5f)
                .background(PhantomYellow),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = username.take(1).uppercase(Locale.ROOT),
                style = MaterialTheme.typography.headlineSmall,
                color = PhantomBlack,
                modifier = Modifier.clearAndSetSemantics { }
            )
        }

        Text(
            text = stringResource(
                R.string.home_username,
                username
            ),
            style = MaterialTheme.typography.titleLarge,
            color = PhantomWhite,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }
}