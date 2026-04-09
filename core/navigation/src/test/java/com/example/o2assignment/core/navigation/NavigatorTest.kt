package com.example.o2assignment.core.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

// Create simple objects to act as NavKeys
private object TopLevelKeyA : NavKey
private object TopLevelKeyB : NavKey
private object TopLevelKeyC : NavKey
private object DetailKey1 : NavKey
private object DetailKey2 : NavKey

class NavigatorTest {

    private lateinit var navigator: Navigator
    private lateinit var state: NavigationState

    @Before
    fun setup() {
        val topLevelStack = NavBackStack(mutableStateListOf<NavKey>(TopLevelKeyA))
        val subStacks = mapOf(
            TopLevelKeyA to NavBackStack(mutableStateListOf<NavKey>(TopLevelKeyA)),
            TopLevelKeyB to NavBackStack(mutableStateListOf<NavKey>(TopLevelKeyB)),
            TopLevelKeyC to NavBackStack(mutableStateListOf<NavKey>(TopLevelKeyC))
        )

        state = NavigationState(
            startKey = TopLevelKeyA,
            topLevelStack = topLevelStack,
            subStacks = subStacks
        )
        navigator = Navigator(state)
    }

    @Test
    fun `navigate to new top level key switches stack`() {
        // Start state: TopLevelKeyA
        navigator.navigate(TopLevelKeyB)

        assertEquals(TopLevelKeyB, state.currentTopLevelKey)
        assertEquals(TopLevelKeyB, state.currentKey)
        assertEquals(listOf(TopLevelKeyA, TopLevelKeyB), state.topLevelStack.toList())
    }

    @Test
    fun `navigate to start key clears top level stack`() {
        // Build up stack
        navigator.navigate(TopLevelKeyB)
        navigator.navigate(TopLevelKeyC)
        assertEquals(listOf(TopLevelKeyA, TopLevelKeyB, TopLevelKeyC), state.topLevelStack.toList())

        // Navigate back to start key
        navigator.navigate(TopLevelKeyA)

        assertEquals(TopLevelKeyA, state.currentTopLevelKey)
        assertEquals(listOf(TopLevelKeyA), state.topLevelStack.toList())
    }

    @Test
    fun `navigate to existing top level key moves it to top`() {
        // Build up stack: A -> B -> C
        navigator.navigate(TopLevelKeyB)
        navigator.navigate(TopLevelKeyC)
        
        // Navigate to B
        navigator.navigate(TopLevelKeyB)

        assertEquals(TopLevelKeyB, state.currentTopLevelKey)
        assertEquals(listOf(TopLevelKeyA, TopLevelKeyC, TopLevelKeyB), state.topLevelStack.toList())
    }

    @Test
    fun `navigate to detail key adds it to current sub stack`() {
        // Start state: TopLevelKeyA
        navigator.navigate(DetailKey1)

        assertEquals(TopLevelKeyA, state.currentTopLevelKey) // Top level hasn't changed
        assertEquals(DetailKey1, state.currentKey)
        assertEquals(listOf(TopLevelKeyA, DetailKey1), state.currentSubStack.toList())
    }

    @Test
    fun `navigate to already existing detail key moves it to top of sub stack`() {
        // Navigate A -> D1 -> D2
        navigator.navigate(DetailKey1)
        navigator.navigate(DetailKey2)
        assertEquals(listOf(TopLevelKeyA, DetailKey1, DetailKey2), state.currentSubStack.toList())

        // Navigate to D1 again
        navigator.navigate(DetailKey1)

        assertEquals(DetailKey1, state.currentKey)
        assertEquals(listOf(TopLevelKeyA, DetailKey2, DetailKey1), state.currentSubStack.toList())
    }

    @Test
    fun `navigate to current top level key clears its sub stack`() {
        // Build up sub stack: A -> D1 -> D2
        navigator.navigate(DetailKey1)
        navigator.navigate(DetailKey2)
        assertEquals(listOf(TopLevelKeyA, DetailKey1, DetailKey2), state.currentSubStack.toList())

        // Navigate to A (current top level)
        navigator.navigate(TopLevelKeyA)

        // Sub stack should be cleared except for the root key
        assertEquals(TopLevelKeyA, state.currentKey)
        assertEquals(listOf(TopLevelKeyA), state.currentSubStack.toList())
    }

    @Test
    fun `go back from detail key removes it from sub stack`() {
        // Build up: A -> D1 -> D2
        navigator.navigate(DetailKey1)
        navigator.navigate(DetailKey2)

        // Go back
        navigator.goBack()

        assertEquals(DetailKey1, state.currentKey)
        assertEquals(listOf(TopLevelKeyA, DetailKey1), state.currentSubStack.toList())
    }

    @Test
    fun `go back from root of top level key returns to previous top level key`() {
        // Build up: A -> B
        navigator.navigate(TopLevelKeyB)
        assertEquals(TopLevelKeyB, state.currentTopLevelKey)

        // Go back (B is root of its sub stack)
        navigator.goBack()

        assertEquals(TopLevelKeyA, state.currentTopLevelKey)
        assertEquals(TopLevelKeyA, state.currentKey)
        assertEquals(listOf(TopLevelKeyA), state.topLevelStack.toList())
    }

    @Test
    fun `go back from start key does nothing`() {
        // Start state is already start key (A)
        assertEquals(TopLevelKeyA, state.currentKey)
        val initialStackSize = state.currentSubStack.size

        navigator.goBack()

        // State shouldn't change
        assertEquals(TopLevelKeyA, state.currentKey)
        assertEquals(initialStackSize, state.currentSubStack.size)
    }
}
