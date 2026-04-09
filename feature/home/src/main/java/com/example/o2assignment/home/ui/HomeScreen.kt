package com.example.o2assignment.home.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.o2assignment.shared.LocalSnackbarHostState
import com.example.o2assignment.shared.getHomeColumnCount
import com.example.o2assignment.shared.ui.AppBar
import com.example.o2assignment.shared.ui.LabelView
import com.example.o2assignment.shared.ui.ProgressIndicator
import com.example.o2assignment.theme.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onScratch: (Int) -> Unit,
    onActivate: (Int) -> Unit,
) {

    val windowInfo = currentWindowAdaptiveInfo()
    val windowSizeClass = windowInfo.windowSizeClass

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val showDeleteDialog = remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.effects) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is Home.Effect.ShowDeleteDialog -> showDeleteDialog.value = true
                is Home.Effect.CloseDeleteDialog -> showDeleteDialog.value = false
                is Home.Effect.NavigateToScratch -> onScratch(effect.id)
                is Home.Effect.NavigateToActivate -> onActivate(effect.id)
            }
        }
    }

    val snackbarHostState = LocalSnackbarHostState.current
    
    val gridState = rememberLazyGridState()

    Scaffold(
        topBar = {
            AppBar(labelRes = R.string.scr_title_home)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(Home.Event.AddNewCard) },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(Icons.Filled.Add, stringResource(R.string.btn_add_cntDesc))
            }
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
                R.string.txt_error_generic, modifier = Modifier.fillMaxSize()
            )

            state.cards.isEmpty() -> LabelView(
                R.string.txt_no_cards, modifier = Modifier.fillMaxSize()
            )

            else -> {
                val columns = windowSizeClass.getHomeColumnCount()
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    state = gridState,
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    items(state.cards, key = { item -> item.id }) { card ->
                        ScratchCardView(
                            state = card, onEvent = viewModel::onEvent
                        )
                    }
                    item("bottomSpacer") { Spacer(modifier = Modifier.height(150.dp)) }
                }
            }
        }
    }

    if (showDeleteDialog.value) {
        AlertDialog(
            shape = RoundedCornerShape(20.dp),
            onDismissRequest = { viewModel.onEvent(Home.Event.OnDeleteCanceled) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onEvent(Home.Event.OnDeleteConfirmed)
                }) { Text(text = stringResource(R.string.btn_yes)) }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onEvent(Home.Event.OnDeleteCanceled) }) {
                    Text(text = stringResource(R.string.btn_cancel))
                }
            },
            text = { Text(text = stringResource(R.string.txt_delete_card)) })
    }
}
