package com.example.o2assignment

import androidx.compose.material3.SnackbarDuration.Indefinite
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.o2assignment.core.navigation.Navigator
import com.example.o2assignment.core.navigation.toEntries
import com.example.o2assignment.navigation.activateEntry
import com.example.o2assignment.navigation.homeEntry
import com.example.o2assignment.navigation.scratchEntry
import com.example.o2assignment.shared.LocalSnackbarHostState


@Composable
fun O2AssignmentApp(
    appState: AppState,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val navigator = remember { Navigator(appState.navigationState) }

    val isOffline by appState.isOffline.collectAsStateWithLifecycle()
    val notConnectedMessage = stringResource(R.string.not_connected)
    LaunchedEffect(isOffline) {
        if (isOffline) {
            snackbarHostState.showSnackbar(
                message = notConnectedMessage,
                duration = Indefinite,
            )
        }
    }

    val entryProvider = entryProvider {
        homeEntry(navigator)
        scratchEntry(navigator)
        activateEntry(navigator)
    }

    CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
        NavDisplay(
            entries = appState.navigationState.toEntries(entryProvider),
            onBack = { navigator.goBack() },
        )
    }
}