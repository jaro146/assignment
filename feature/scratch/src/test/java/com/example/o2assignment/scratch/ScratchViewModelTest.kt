package com.example.o2assignment.scratch

import app.cash.turbine.test
import com.example.card_repository.model.CardActionResult
import com.example.o2assignment.shared.domain.LoadCardByIdUseCase
import com.example.o2assignment.scratch.domain.ScratchCardUseCase
import com.example.o2assignment.scratch.ui.Scratch
import com.example.o2assignment.scratch.ui.ScratchViewModel
import com.example.card_repository.domain.AppError
import com.example.card_repository.domain.ScratchCard
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ScratchViewModelTest {

    private val loadCardByIdUseCase: LoadCardByIdUseCase = mockk()
    private val scratchCardUseCase: ScratchCardUseCase = mockk()
    private val testDispatcher = StandardTestDispatcher()
    private val cardId = 42

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState emits card from repository`() = runTest {
        val testCard = ScratchCard(id = cardId)
        coEvery { loadCardByIdUseCase(cardId) } returns flowOf(testCard)

        val viewModel = ScratchViewModel(loadCardByIdUseCase, scratchCardUseCase, cardId)

        viewModel.uiState.test {
            assertEquals(false, awaitItem().isLoading) // initial stateIn value
            assertEquals(true, awaitItem().isLoading)  // onStart emission

            val loadedState = awaitItem()              // DB emission
            assertEquals(false, loadedState.isLoading)
            assertEquals(testCard, loadedState.card)
            assertNull(loadedState.error)
        }
    }

    @Test
    fun `uiState emits error when repository throws exception`() = runTest {
        val exception = RuntimeException("DB Error")
        coEvery { loadCardByIdUseCase(cardId) } returns flow { throw exception }

        val viewModel = ScratchViewModel(loadCardByIdUseCase, scratchCardUseCase, cardId)

        viewModel.uiState.test {
            assertEquals(false, awaitItem().isLoading) // initial stateIn value
            assertEquals(true, awaitItem().isLoading)  // onStart emission

            val errorState = awaitItem()               // catch emission
            assertEquals(false, errorState.isLoading)
            assertEquals("Error load data", errorState.error?.message)
            assertEquals(exception.message, errorState.error?.cause?.message)
        }
    }

    @Test
    fun `onBack triggers NavigateBack effect`() = runTest {
        coEvery { loadCardByIdUseCase(cardId) } returns flowOf(null)
        val viewModel = ScratchViewModel(loadCardByIdUseCase, scratchCardUseCase, cardId)

        viewModel.effects.test {
            viewModel.onEvent(Scratch.Event.Back)
            assertEquals(Scratch.Effect.NavigateBack, awaitItem())
        }
    }

    @Test
    fun `onScratchCard executes usecase successfully`() = runTest {
        coEvery { loadCardByIdUseCase(cardId) } returns flowOf(null)
        coEvery { scratchCardUseCase(cardId) } returns CardActionResult(code = "XYZ")
        
        val viewModel = ScratchViewModel(loadCardByIdUseCase, scratchCardUseCase, cardId)

        viewModel.uiState.test {
            awaitItem() // initial stateIn
            awaitItem() // onStart
            awaitItem() // DB load

            viewModel.onEvent(Scratch.Event.ScratchCard)
            
            testDispatcher.scheduler.advanceUntilIdle()
        }

        coVerify(exactly = 1) { scratchCardUseCase(cardId) }
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `onScratchCard handles usecase error result`() = runTest {
        coEvery { loadCardByIdUseCase(cardId) } returns flowOf(null)
        val usecaseError = AppError("Failed to scratch via API")
        coEvery { scratchCardUseCase(cardId) } returns CardActionResult(error = usecaseError)
        
        val viewModel = ScratchViewModel(loadCardByIdUseCase, scratchCardUseCase, cardId)

        viewModel.uiState.test {
            awaitItem() // initial stateIn
            awaitItem() // onStart
            awaitItem() // DB load

            viewModel.onEvent(Scratch.Event.ScratchCard)

            val errorState = awaitItem()
            assertEquals(usecaseError, errorState.error)
        }
    }

    @Test
    fun `onScratchCard catches unexpected exception`() = runTest {
        coEvery { loadCardByIdUseCase(cardId) } returns flowOf(null)
        val exception = RuntimeException("Unexpected exception")
        coEvery { scratchCardUseCase(cardId) } throws exception
        
        val viewModel = ScratchViewModel(loadCardByIdUseCase, scratchCardUseCase, cardId)

        viewModel.uiState.test {
            awaitItem() // initial stateIn
            awaitItem() // onStart
            awaitItem() // DB load

            viewModel.onEvent(Scratch.Event.ScratchCard)

            val errorState = awaitItem()
            assertEquals("Failed to scratch card", errorState.error?.message)
            assertEquals(exception.message, errorState.error?.cause?.message)
        }
    }

    @Test
    fun `DismissError clears error in uiState`() = runTest {
        coEvery { loadCardByIdUseCase(cardId) } returns flowOf(null)
        val usecaseError = AppError("Failed to scratch via API")
        coEvery { scratchCardUseCase(cardId) } returns CardActionResult(error = usecaseError)
        
        val viewModel = ScratchViewModel(loadCardByIdUseCase, scratchCardUseCase, cardId)

        viewModel.uiState.test {
            awaitItem() // initial stateIn
            awaitItem() // onStart
            awaitItem() // DB load

            // Force an error first
            viewModel.onEvent(Scratch.Event.ScratchCard)
            val errorState = awaitItem()
            assertEquals(usecaseError, errorState.error)

            // Now dismiss it
            viewModel.onEvent(Scratch.Event.DismissError)
            val cleanState = awaitItem()
            assertNull(cleanState.error)
        }
    }
}
