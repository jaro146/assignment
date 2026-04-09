package com.example.o2assignment.activate.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.card_repository.domain.ScratchCard
import com.example.o2assignment.shared.LocalSnackbarHostState
import com.example.o2assignment.shared.ui.AppBar
import com.example.o2assignment.shared.ui.LabelView
import com.example.o2assignment.shared.ui.ProgressIndicator
import com.example.o2assignment.theme.R
import com.example.o2assignment.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivateScreen(
    viewModel: ActivateViewModel,
    onBack: () -> Unit,
) {
    val context = LocalContext.current

    LaunchedEffect(viewModel.effects) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is Activate.Effect.NavigateBack -> onBack()
                is Activate.Effect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = LocalSnackbarHostState.current

    Scaffold(
        topBar = {
            AppBar(
                labelRes = R.string.scr_title_activate,
                onBackAction = { viewModel.onEvent(Activate.Event.Back) })
        },
        snackbarHost = {
            SnackbarHost(
                snackbarHostState, modifier = Modifier.windowInsetsPadding(
                    WindowInsets.safeDrawing.exclude(WindowInsets.ime)
                )
            )
        },
    ) { innerPadding ->
        when {
            state.isLoading -> ProgressIndicator()
            state.error != null -> LabelView(
                labelId = R.string.txt_activate_failure,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                onCloseAction = { viewModel.onEvent(Activate.Event.DismissError) })

            else -> ActivateContent(state, Modifier.padding(innerPadding)) {
                if (state.card?.alreadyActivated() == true) {
                    viewModel.onEvent(Activate.Event.Back)
                } else {
                    viewModel.onEvent(Activate.Event.ActivateCard)
                }
            }
        }
    }
}

@Composable
private fun ActivateContent(
    state: Activate.State, modifier: Modifier = Modifier, onActionButton: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            state.getCardStateIcon(),
            contentScale = ContentScale.FillWidth,
            contentDescription = "btnIconScratch",
            modifier = Modifier
                .padding(MaterialTheme.spacing.medium)
                .align(Alignment.CenterHorizontally)
                .size(120.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.medium),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = onActionButton,
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                enabled = state.card?.alreadyActivated() == false
            ) {
                ActivateButtonContent(state.card)
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ActivateButtonContent(cardState: ScratchCard?) {
    AnimatedContent(
        targetState = cardState,
        transitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith
                    fadeOut(animationSpec = tween(300))
        },
        label = "Scratch button state animation"
    ) { cardState ->
        when {
            cardState?.activationInProgress() == true -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }

            else -> {
                Row {
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = Icons.Filled.Edit.name,
                        modifier = Modifier.size(ButtonDefaults.IconSize),
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(stringResource(R.string.btn_activate))
                }
            }
        }
    }
}
