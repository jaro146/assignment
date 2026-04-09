package com.example.o2assignment.scratch.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.card_repository.domain.ScratchCard
import com.example.card_repository.model.CardState
import com.example.o2assignment.shared.ui.AppBar
import com.example.o2assignment.shared.ui.LabelView
import com.example.o2assignment.shared.ui.ProgressIndicator
import com.example.o2assignment.theme.R
import com.example.o2assignment.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScratchScreen(
    viewModel: ScratchViewModel,
    onBack: () -> Unit,
) {
    val context = LocalContext.current

    LaunchedEffect(viewModel.effects) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is Scratch.Effect.NavigateBack -> onBack()
                is Scratch.Effect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            AppBar(
                labelRes = R.string.scr_title_scratch,
                onBackAction = { viewModel.onEvent(Scratch.Event.Back) })
        }
    ) { innerPadding ->
        when {
            state.isLoading -> ProgressIndicator()
            state.error != null -> LabelView(
                labelId = R.string.txt_error_generic,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                onCloseAction = { viewModel.onEvent(Scratch.Event.DismissError) }
            )

            else -> ScratchContent(state, Modifier.padding(innerPadding)) {
                if (state.card?.cardState == CardState.SCRATCHED) {
                    viewModel.onEvent(Scratch.Event.Back)
                } else {
                    viewModel.onEvent(Scratch.Event.ScratchCard)
                }
            }
        }
    }
}

@Composable
private fun ScratchContent(
    state: Scratch.State,
    modifier: Modifier,
    onActionButton: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            state.getScratchCardIcon(),
            contentScale = ContentScale.FillWidth,
            contentDescription = "btnIconScratch",
            modifier = Modifier
                .padding(MaterialTheme.spacing.medium)
                .align(Alignment.CenterHorizontally)
                .size(120.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
        )
        AnimatedVisibility(!state.card?.code.isNullOrEmpty()) {
            state.card?.code?.let {
                Text(it, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.medium),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = onActionButton,
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                enabled = state.card?.alreadyScratched() == false
            ) {
                ScratchButtonContent(state.card)
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ScratchButtonContent(state: ScratchCard?) {
    AnimatedContent(
        targetState = state,
        transitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith
                    fadeOut(animationSpec = tween(300))
        },
    ) { state ->
        Box(Modifier.wrapContentWidth(), contentAlignment = Alignment.Center) {
            when {
                state?.scratchInProgress() == true -> {
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
                            contentDescription = "btnIconScratch",
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(
                            if (state?.alreadyScratched() == true) {
                                stringResource(R.string.btn_card_scratched)
                            } else {
                                stringResource(R.string.btn_scratch_card)
                            }
                        )
                    }
                }
            }
        }
    }
}
