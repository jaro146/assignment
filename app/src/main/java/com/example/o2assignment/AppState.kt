package com.example.o2assignment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.example.o2assignment.core.navigation.HomeNavKey
import com.example.o2assignment.core.navigation.NavigationState
import com.example.o2assignment.core.navigation.rememberAppNavigationState
import com.example.o2assignment.network.di.util.NetworkMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Composable
fun rememberAppState(
    networkMonitor: NetworkMonitor,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
): AppState {
    val navigationState = rememberAppNavigationState(HomeNavKey, setOf(HomeNavKey))

    return remember(
        navigationState,
        coroutineScope,
        networkMonitor,
    ) {
        AppState(
            navigationState = navigationState,
            coroutineScope = coroutineScope,
            networkMonitor = networkMonitor,
        )
    }
}


@Stable
class AppState(
    val navigationState: NavigationState,
    coroutineScope: CoroutineScope,
    networkMonitor: NetworkMonitor,
) {
    val isOffline = networkMonitor.isOnline
        .map(Boolean::not)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )
}

