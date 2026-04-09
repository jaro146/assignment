package com.example.o2assignment.activate

import app.cash.turbine.test
import com.example.card_repository.model.CardActionResult
import com.example.card_repository.domain.ScratchCard
import com.example.card_repository.domain.CardRepository
import com.example.o2assignment.activate.domain.ActivateCardUseCase
import com.example.o2assignment.activate.ui.Activate
import com.example.o2assignment.activate.ui.ActivateViewModel
import com.example.card_repository.domain.AppError
import com.example.o2assignment.shared.domain.LoadCardByIdUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class ActivateViewModelTest {

    private val useCase: ActivateCardUseCase = mockk()
    private val loadCardByIdUseCase: LoadCardByIdUseCase = mockk()
    private val testDispatcher = StandardTestDispatcher()
    private val cardId = 100

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

        val viewModel = ActivateViewModel(loadCardByIdUseCase, useCase, cardId)

        viewModel.uiState.test {
            val initialState = awaitItem()
            assertEquals(false, initialState.isLoading)

            val loadedState = awaitItem()
            assertEquals(false, loadedState.isLoading)
            assertEquals(testCard, loadedState.card)
            assertNull(loadedState.error)
        }
    }

    @Test
    fun `onBack triggers NavigateBack effect`() = runTest {
        coEvery { loadCardByIdUseCase(cardId) } returns flowOf(null)
        val viewModel = ActivateViewModel(loadCardByIdUseCase, useCase, cardId)

        viewModel.effects.test {
            viewModel.onEvent(Activate.Event.Back)
            assertEquals(Activate.Effect.NavigateBack, awaitItem())
        }
    }

    @Test
    fun `onActivateCard handles missing code`() = runTest {
        val testCard = ScratchCard(id = cardId, code = null)
        coEvery { loadCardByIdUseCase(cardId) } returns flowOf(testCard)

        val viewModel = ActivateViewModel(loadCardByIdUseCase, useCase, cardId)

        viewModel.uiState.test {
            awaitItem() // Empty state
            awaitItem() // Loaded state

            viewModel.onEvent(Activate.Event.ActivateCard)

            val updatedState = awaitItem()
            assertEquals(
                "Failed to activate, code is null", updatedState.error?.message
            )
        }
    }

    @Test
    fun `onActivateCard executes usecase successfully`() = runTest {
        val testCard = ScratchCard(id = cardId, code = "CODE-123")
        coEvery { loadCardByIdUseCase(cardId) } returns flowOf(testCard)
        coEvery { useCase(cardId, "CODE-123") } returns CardActionResult(activationResult = true)

        val viewModel = ActivateViewModel(loadCardByIdUseCase, useCase, cardId)

        viewModel.uiState.test {
            awaitItem() // Empty State
            awaitItem() // Loaded State

            viewModel.onEvent(Activate.Event.ActivateCard)

            testDispatcher.scheduler.advanceUntilIdle()
        }

        coVerify(exactly = 1) { useCase(cardId, "CODE-123") }
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `onActivateCard handles usecase error result`() = runTest {
        val testCard = ScratchCard(id = cardId, code = "CODE-123")
        coEvery { loadCardByIdUseCase(cardId) } returns flowOf(testCard)

        val useCaseError = AppError("Activation failed remotely")
        coEvery { useCase(cardId, "CODE-123") } returns CardActionResult(error = useCaseError)

        val viewModel = ActivateViewModel(loadCardByIdUseCase, useCase, cardId)

        viewModel.uiState.test {
            awaitItem() // Empty State
            awaitItem() // Loaded State

            viewModel.onEvent(Activate.Event.ActivateCard)

            val updatedState = awaitItem()
            assertEquals(useCaseError, updatedState.error)
        }
    }

    @Test
    fun `onActivateCard catches unexpected exception`() = runTest {
        val testCard = ScratchCard(id = cardId, code = "CODE-123")
        coEvery { loadCardByIdUseCase(cardId) } returns flowOf(testCard)

        val exception = RuntimeException("Network Error")
        coEvery { useCase(cardId, "CODE-123") } throws exception

        val viewModel = ActivateViewModel(loadCardByIdUseCase, useCase, cardId)

        viewModel.uiState.test {
            awaitItem() // Empty state
            awaitItem() // Loaded state

            viewModel.onEvent(Activate.Event.ActivateCard)

            val updatedState = awaitItem()
            assertEquals("Failed to activate card", updatedState.error?.message)
            assertEquals(exception, updatedState.error?.cause)
        }
    }

    @Test
    fun `DismissError clears error in uiState`() = runTest {
        val testCard = ScratchCard(id = cardId, code = null)
        coEvery { loadCardByIdUseCase(cardId) } returns flowOf(testCard)

        val viewModel = ActivateViewModel(loadCardByIdUseCase, useCase, cardId)

        viewModel.uiState.test {
            awaitItem() // Empty State
            awaitItem() // Loaded State

            // Force an error first (code is null)
            viewModel.onEvent(Activate.Event.ActivateCard)
            val errorState = awaitItem()
            assertEquals(
                "Failed to activate, code is null", errorState.error?.message
            )

            // Now dismiss it
            viewModel.onEvent(Activate.Event.DismissError)
            val cleanState = awaitItem()
            assertNull(cleanState.error)
        }
    }
}
