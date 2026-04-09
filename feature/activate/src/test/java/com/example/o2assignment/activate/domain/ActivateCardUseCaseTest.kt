package com.example.o2assignment.activate.domain

import com.example.card_repository.domain.CardRepository
import com.example.card_repository.model.CardState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.coroutines.cancellation.CancellationException

@OptIn(ExperimentalCoroutinesApi::class)
class ActivateCardUseCaseTest {

    private val repository: CardRepository = mockk(relaxed = true)
    private val testScope = TestScope()
    private lateinit var useCase: ActivateCardUseCase

    @Before
    fun setup() {
        useCase = ActivateCardUseCase(repository, testScope)
    }

    @Test
    fun `invoke success updates status to IN_PROGRESS then ACTIVATED and returns success`() =
        testScope.runTest {
            val cardId = 1
            val code = "TEST-CODE"
            coEvery { repository.verifyActivationCode(code) } returns true

            val result = useCase(cardId, code)

            coVerify(exactly = 1) {
                repository.updateCardStatus(
                    cardId,
                    CardState.ACTIVATION_IN_PROGRESS
                )
            }
            coVerify(exactly = 1) { repository.updateCardStatus(cardId, CardState.ACTIVATED) }
            assertEquals(true, result.activationResult)
            assertNull(result.error)
        }

    @Test
    fun `invoke failure threshold reverts status to SCRATCHED and returns error`() = testScope.runTest {
        val cardId = 1
        val code = "TEST-CODE"
        coEvery { repository.verifyActivationCode(code) } returns false

        val result = useCase(cardId, code)

        coVerify(exactly = 1) {
            repository.updateCardStatus(
                cardId,
                CardState.ACTIVATION_IN_PROGRESS
            )
        }
        coVerify(exactly = 1) { repository.updateCardStatus(cardId, CardState.SCRATCHED) }
        assertNotNull(result.error)
        assertTrue(result.error?.message?.contains("version check did not pass") == true)
    }

    @Test
    fun `invoke exception reverts status to SCRATCHED and returns error`() = testScope.runTest {
        val cardId = 1
        val code = "TEST-CODE"
        coEvery { repository.verifyActivationCode(code) } throws RuntimeException("Network Error")

        val result = useCase(cardId, code)

        coVerify(exactly = 1) {
            repository.updateCardStatus(
                cardId,
                CardState.ACTIVATION_IN_PROGRESS
            )
        }
        coVerify(exactly = 1) { repository.updateCardStatus(cardId, CardState.SCRATCHED) }
        assertNotNull(result.error)
        assertEquals("Failed to activate card", result.error?.message)
    }

    @Test
    fun `invoke cancellation reverts status properly`() = testScope.runTest {
        val cardId = 1
        val code = "TEST-CODE"
        coEvery { repository.verifyActivationCode(code) } throws CancellationException("Cancelled")

        try {
            useCase(cardId, code)
        } catch (e: CancellationException) {
            // nothing to do here
        }

        coVerify(exactly = 1) {
            repository.updateCardStatus(
                cardId,
                CardState.ACTIVATION_IN_PROGRESS
            )
        }
        coVerify(exactly = 1) { repository.updateCardStatus(cardId, CardState.SCRATCHED) }
    }
}
