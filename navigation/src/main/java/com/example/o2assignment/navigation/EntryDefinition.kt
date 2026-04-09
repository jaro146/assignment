package com.example.o2assignment.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.example.o2assignment.activate.ui.ActivateScreen
import com.example.o2assignment.activate.ui.ActivateViewModel
import com.example.o2assignment.core.navigation.ActivateNav
import com.example.o2assignment.core.navigation.HomeNavKey
import com.example.o2assignment.core.navigation.Navigator
import com.example.o2assignment.core.navigation.ScratchNavKey
import com.example.o2assignment.home.ui.HomeScreen
import com.example.o2assignment.scratch.ui.ScratchScreen
import com.example.o2assignment.scratch.ui.ScratchViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf


fun EntryProviderScope<NavKey>.homeEntry(navigator: Navigator) {
    entry<HomeNavKey> {
        HomeScreen(
            onScratch = { navigator.navigate(ScratchNavKey(it)) },
            onActivate = { navigator.navigate(ActivateNav(it)) },
        )
    }
}

fun EntryProviderScope<NavKey>.scratchEntry(navigator: Navigator) {
    entry<ScratchNavKey> { entry ->
        val viewModel = koinViewModel<ScratchViewModel> {
            parametersOf(entry.cardId)
        }
        ScratchScreen(
            viewModel = viewModel,
            onBack = { navigator.goBack() }
        )
    }
}

fun EntryProviderScope<NavKey>.activateEntry(navigator: Navigator) {
    entry<ActivateNav> { entry ->
        val viewModel = koinViewModel<ActivateViewModel> {
            parametersOf(entry.cardId)
        }
        ActivateScreen(
            viewModel = viewModel,
            onBack = { navigator.goBack() }
        )
    }
}
