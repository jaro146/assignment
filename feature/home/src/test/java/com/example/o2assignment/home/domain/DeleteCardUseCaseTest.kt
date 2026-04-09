package com.example.o2assignment.home.domain

import com.example.card_repository.domain.CardRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DeleteCardUseCaseTest {

    private val cardRepository: CardRepository = mockk(relaxed = true)
    private lateinit var useCase: DeleteCardUseCase

    @Before
    fun setup() {
        useCase = DeleteCardUseCase(cardRepository)
    }

    @Test
    fun `invoke calls deleteById on repository`() = runTest {
        val cardId = 42

        useCase(cardId)

        coVerify(exactly = 1) { cardRepository.deleteById(cardId) }
    }

    @Test(expected = RuntimeException::class)
    fun `invoke throws exception when repository throws`() = runTest {
        val cardId = 42
        coEvery { cardRepository.deleteById(cardId) } throws RuntimeException("DB Error")

        useCase(cardId)
    }
}
