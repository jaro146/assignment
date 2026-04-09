package com.example.o2assignment.home.ui

import app.cash.turbine.test
import com.example.card_repository.domain.ScratchCard
import com.example.o2assignment.home.domain.CreateNewCardUseCase
import com.example.o2assignment.home.domain.DeleteCardUseCase
import com.example.o2assignment.home.domain.LoadAllCardsUseCase
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
class HomeViewModelTest {

    private val loadAllCardsUseCase: LoadAllCardsUseCase = mockk(relaxed = true)
    private val deleteCardUseCase: DeleteCardUseCase = mockk(relaxed = true)
    private val createNewCardUseCase: CreateNewCardUseCase = mockk(relaxed = true)

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState emits loaded cards from repository`() = runTest {
        val testCard = ScratchCard(id = 1)
        coEvery { loadAllCardsUseCase() } returns flowOf(listOf(testCard))

        val viewModel = HomeViewModel(loadAllCardsUseCase, deleteCardUseCase, createNewCardUseCase)

        viewModel.uiState.test {
            // 1. Initial stateIn value
            assertEquals(false, awaitItem().isLoading)

            // 2. onStart emission
            assertEquals(true, awaitItem().isLoading)

            // 3. combined with DB results
            val loadedState = awaitItem()
            assertEquals(false, loadedState.isLoading)
            assertEquals(1, loadedState.cards.size)
            assertEquals(1, loadedState.cards[0].id)
            assertNull(loadedState.error)
        }
    }

    @Test
    fun `uiState emits error when repository throws exception`() = runTest {
        val testException = RuntimeException("DB Error")
        coEvery { loadAllCardsUseCase() } returns flow { throw testException }

        val viewModel = HomeViewModel(loadAllCardsUseCase, deleteCardUseCase, createNewCardUseCase)

        viewModel.uiState.test {
            // 1. Initial stateIn value
            assertEquals(false, awaitItem().isLoading)

            // 2. onStart emission
            assertEquals(true, awaitItem().isLoading)

            // 3. catch block emission
            val errorState = awaitItem()
            assertEquals(false, errorState.isLoading)
            assertEquals("Error load data", errorState.error?.message)
            assertEquals(testException.message, errorState.error?.cause?.message)
        }
    }

    @Test
    fun `addNewCard calls create usecase`() = runTest {
        coEvery { loadAllCardsUseCase() } returns flowOf(emptyList())
        val viewModel = HomeViewModel(loadAllCardsUseCase, deleteCardUseCase, createNewCardUseCase)

        viewModel.uiState.test {
            awaitItem() // stateIn init
            awaitItem() // onStart
            awaitItem() // DB load

            viewModel.onEvent(Home.Event.AddNewCard)
            testDispatcher.scheduler.advanceUntilIdle()
        }

        coVerify(exactly = 1) { createNewCardUseCase(any()) }
    }

    @Test
    fun `delete flow shows dialog then deletes card on confirm`() = runTest {
        coEvery { loadAllCardsUseCase() } returns flowOf(emptyList())
        val viewModel = HomeViewModel(loadAllCardsUseCase, deleteCardUseCase, createNewCardUseCase)

        viewModel.uiState.test {
            awaitItem() // stateIn init
            awaitItem() // onStart
            awaitItem() // DB load

            // 1. Request delete
            viewModel.onEvent(Home.Event.OnDeleteCard(42))
            val dialogState = awaitItem()
            assertEquals(42, dialogState.selectedCardId)

            // 2. Confirm delete
            viewModel.onEvent(Home.Event.OnDeleteConfirmed)
            val confirmState = awaitItem()
            assertNull(confirmState.selectedCardId)

            testDispatcher.scheduler.advanceUntilIdle()
        }

        coVerify(exactly = 1) { deleteCardUseCase(42) }
    }

    @Test
    fun `delete flow cancels and clears selection`() = runTest {
        coEvery { loadAllCardsUseCase() } returns flowOf(emptyList())
        val viewModel = HomeViewModel(loadAllCardsUseCase, deleteCardUseCase, createNewCardUseCase)

        viewModel.uiState.test {
            awaitItem() // stateIn init
            awaitItem() // onStart
            awaitItem() // DB load

            // Request delete
            viewModel.onEvent(Home.Event.OnDeleteCard(42))
            val dialogState = awaitItem()
            assertEquals(42, dialogState.selectedCardId)

            // Cancel delete
            viewModel.onEvent(Home.Event.OnDeleteCanceled)
            val cancelState = awaitItem()
            assertNull(cancelState.selectedCardId)
        }

        coVerify(exactly = 0) { deleteCardUseCase(any()) }
    }

    @Test
    fun `onScratchCard triggers NavigateToScratch effect`() = runTest {
        coEvery { loadAllCardsUseCase() } returns flowOf(emptyList())
        val viewModel = HomeViewModel(loadAllCardsUseCase, deleteCardUseCase, createNewCardUseCase)

        viewModel.effects.test {
            viewModel.onEvent(Home.Event.OnScratchCard(5))
            assertEquals(Home.Effect.NavigateToScratch(5), awaitItem())
        }
    }

    @Test
    fun `onActivateCard triggers NavigateToActivate effect`() = runTest {
        coEvery { loadAllCardsUseCase() } returns flowOf(emptyList())
        val viewModel = HomeViewModel(loadAllCardsUseCase, deleteCardUseCase, createNewCardUseCase)

        viewModel.effects.test {
            viewModel.onEvent(Home.Event.OnActivateCard(10))
            assertEquals(Home.Effect.NavigateToActivate(10), awaitItem())
        }
    }
}
