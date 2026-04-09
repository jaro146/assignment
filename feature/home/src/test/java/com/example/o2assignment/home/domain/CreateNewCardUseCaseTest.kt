package com.example.o2assignment.home.domain

import com.example.card_repository.domain.CardRepository
import com.example.card_repository.domain.ScratchCard
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class CreateNewCardUseCaseTest {

    private val cardRepository: CardRepository = mockk(relaxed = true)
    private lateinit var useCase: CreateNewCardUseCase

    @Before
    fun setup() {
        useCase = CreateNewCardUseCase(cardRepository)
    }

    @Test
    fun `invoke calls insert on repository`() = runTest {
        val testCard = ScratchCard(id = 1)

        useCase(testCard)

        coVerify(exactly = 1) { cardRepository.insert(testCard) }
    }

    @Test(expected = RuntimeException::class)
    fun `invoke throws exception when repository throws`() = runTest {
        val testCard = ScratchCard(id = 1)
        coEvery { cardRepository.insert(testCard) } throws RuntimeException("DB Error")

        useCase(testCard)
    }
}
