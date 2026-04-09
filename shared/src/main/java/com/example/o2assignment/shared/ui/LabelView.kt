package com.example.o2assignment.shared.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.o2assignment.theme.AppTheme
import com.example.o2assignment.theme.R

@Composable
fun LabelView(
    @StringRes labelId: Int,
    onCloseAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        Column(
            modifier = Modifier.matchParentSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(labelId),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.size(24.dp))
            onCloseAction?.let {
                Button(
                    onClick = it,
                    contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                ) {
                    Row {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = Icons.Filled.Close.name,
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(
                            stringResource(R.string.btn_close)
                        )
                    }

                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LabelViewPreview() {
    AppTheme {
        LabelView(labelId = R.string.txt_no_cards)
    }
}

@Preview(showSystemUi = true)
@Composable
private fun LabelViewFullScreenPreview() {
    AppTheme {
        LabelView(labelId = R.string.txt_no_cards, {}, Modifier.fillMaxSize())
    }
}