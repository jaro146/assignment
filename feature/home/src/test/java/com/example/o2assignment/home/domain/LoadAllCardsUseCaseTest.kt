package com.example.o2assignment.home.domain

import com.example.card_repository.domain.CardRepository
import com.example.card_repository.domain.ScratchCard
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LoadAllCardsUseCaseTest {

    private val cardRepository: CardRepository = mockk(relaxed = true)
    private lateinit var useCase: LoadAllCardsUseCase

    @Before
    fun setup() {
        useCase = LoadAllCardsUseCase(cardRepository)
    }

    @Test
    fun `invoke returns flow of cards from repository`() = runTest {
        val testCardList = listOf(ScratchCard(id = 1), ScratchCard(id = 2))
        every { cardRepository.getAllCards() } returns flowOf(testCardList)

        val flow = useCase()
        
        // Collect the emitted list to verify
        val emittedList = flow.toList().first()

        assertEquals(2, emittedList.size)
        assertEquals(1, emittedList[0].id)
        assertEquals(2, emittedList[1].id)
        
        verify(exactly = 1) { cardRepository.getAllCards() }
    }
}
