package com.example.o2assignment.scratch.domain

import com.example.card_repository.model.CardState
import com.example.card_repository.domain.CardRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.coroutines.cancellation.CancellationException

@OptIn(ExperimentalCoroutinesApi::class)
class ScratchCardUseCaseTest {

    private val repository: CardRepository = mockk(relaxed = true)
    private lateinit var useCase: ScratchCardUseCase

    @Before
    fun setup() {
        useCase = ScratchCardUseCase(repository)
    }

    @Test
    fun `invoke success updates status to IN_PROGRESS then SCRATCHED and returns code`() = runTest {
        val cardId = 1

        val result = useCase(cardId)

        coVerify(exactly = 1) { repository.updateCardStatus(cardId, CardState.SCRATCH_IN_PROGRESS) }
        coVerify(exactly = 1) {
            repository.update(match { it.id == cardId && it.cardState == CardState.SCRATCHED && it.code != null })
        }
        Assert.assertNotNull(result.code)
        Assert.assertNull(result.error)
    }

    @Test
    fun `invoke failure reverts status to UNSCRATCHED and returns error`() = runTest {
        val cardId = 1
        coEvery { repository.update(any()) } throws RuntimeException("DB Error")

        val result = useCase(cardId)

        coVerify(exactly = 1) { repository.updateCardStatus(cardId, CardState.SCRATCH_IN_PROGRESS) }
        coVerify(exactly = 1) { repository.updateCardStatus(cardId, CardState.UNSCRATCHED) }
        Assert.assertNotNull(result.error)
        Assert.assertEquals("Failed to scratch card", result.error?.message)
    }

    @Test
    fun `invoke cancellation reverts status properly`() = runTest {
        val cardId = 1
        coEvery { repository.update(any()) } throws CancellationException("Cancelled")

        try {
            useCase(cardId)
        } catch (e: CancellationException) {
            // Expected
        }

        coVerify(exactly = 1) { repository.updateCardStatus(cardId, CardState.SCRATCH_IN_PROGRESS) }
        coVerify(exactly = 1) { repository.updateCardStatus(cardId, CardState.UNSCRATCHED) }
    }
}