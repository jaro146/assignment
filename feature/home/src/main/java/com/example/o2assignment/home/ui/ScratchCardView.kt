package com.example.o2assignment.home.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LibraryAddCheck
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.card_repository.model.CardState
import com.example.o2assignment.theme.AppTheme
import com.example.o2assignment.theme.R
import com.example.o2assignment.theme.spacing
import java.util.UUID
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.ColorFilter
import com.example.card_repository.domain.ScratchCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScratchCardView(
    state: ScratchCard,
    onEvent: (Home.Event) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        elevation = CardDefaults.cardElevation(),
        modifier = modifier
            .padding(MaterialTheme.spacing.medium),
    ) {
        Image(
            state.cardState.getCardStateIcon(),
            contentScale = ContentScale.FillWidth,
            contentDescription = "btnIconScratch",
            modifier = modifier
                .padding(MaterialTheme.spacing.medium)
                .align(Alignment.CenterHorizontally)
                .size(120.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
        )
        state.code?.let {
            Text(it, textAlign = TextAlign.Center, modifier = modifier.fillMaxWidth())
        }
        ButtonsView(state, onEvent, modifier)
    }
}

@Composable
private fun ButtonsView(
    card: ScratchCard,
    onEvent: (Home.Event) -> Unit,
    modifier: Modifier = Modifier,
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.medium),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = { onEvent(Home.Event.OnScratchCard(card.id)) },
            enabled = card.scratchButtonEnabled(),
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
        ) {
            Icon(
                Icons.Filled.Edit,
                contentDescription = "btnIconScratch",
                modifier = Modifier.size(ButtonDefaults.IconSize),
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(stringResource(R.string.btn_scratch))
        }
        Button(
            onClick = { onEvent(Home.Event.OnActivateCard(card.id)) },
            enabled = card.activateButtonEnabled(),
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
        ) {
            Icon(
                Icons.Filled.LibraryAddCheck,
                contentDescription = "btnIconActivate",
                modifier = Modifier.size(ButtonDefaults.IconSize),
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(stringResource(R.string.btn_activate))
        }
        IconButton(onClick = { onEvent(Home.Event.OnDeleteCard(card.id)) }) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "btnIconDelete"
            )
        }
    }
}

@Preview(heightDp = 1500)
@Composable
private fun ScratchCardViewPreview() {
    AppTheme {
        Column {
            ScratchCardView(
                state = ScratchCard(
                    code = UUID.randomUUID().toString(), cardState = CardState.UNSCRATCHED
                ), onEvent = {})
            ScratchCardView(
                state = ScratchCard(
                    cardState = CardState.SCRATCHED
                ), onEvent = {})
            ScratchCardView(
                state = ScratchCard(
                    cardState = CardState.ACTIVATED
                ), onEvent = {})
        }
    }
}