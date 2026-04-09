package com.example.o2assignment.scratch.domain

import app.cash.turbine.test
import com.example.card_repository.domain.CardRepository
import com.example.card_repository.domain.ScratchCard
import com.example.o2assignment.shared.domain.LoadCardByIdUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoadCardByIdUseCaseTest {

    private val repository: CardRepository = mockk()
    private val useCase = LoadCardByIdUseCase(repository)

    @Test
    fun `invoke should return flow from repository`() = runTest {
        val cardId = 1
        val mockCard = ScratchCard(id = cardId, code = "123")

        every { repository.getCardById(cardId) } returns flowOf(mockCard)

        val resultFlow = useCase(cardId)

        resultFlow.test {
            Assert.assertEquals(mockCard, awaitItem())
            awaitComplete()
        }
    }
}