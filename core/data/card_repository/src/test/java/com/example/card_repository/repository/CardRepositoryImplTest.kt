package com.example.card_repository.repository

import com.example.card_repository.db.CardDao
import com.example.card_repository.model.ActivateResponse
import com.example.card_repository.model.CardState
import com.example.card_repository.domain.ScratchCard
import com.example.card_repository.model.ScratchCardEntity
import com.example.card_repository.domain.toEntity
import com.example.card_repository.network.ActivateApiService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CardRepositoryImplTest {

    private val db: CardDao = mockk(relaxed = true)
    private val activateService: ActivateApiService = mockk()

    private lateinit var repository: CardRepositoryImpl

    private val testCard = ScratchCard(id = 1, cardState = CardState.UNSCRATCHED, code = null)

    @Before
    fun setup() {
        repository = CardRepositoryImpl(db, activateService)
    }

    @Test
    fun insertTest() = runTest {
        repository.insert(testCard)
        coVerify(exactly = 1) { db.insert(testCard.toEntity()) }
    }

    @Test
    fun updateTest() = runTest {
        repository.update(testCard)
        coVerify(exactly = 1) { db.update(testCard.toEntity()) }
    }

    @Test
    fun updateCardStatusTest() = runTest {
        repository.updateCardStatus(testCard.id, CardState.SCRATCH_IN_PROGRESS)
        coVerify(exactly = 1) {
            db.updateCardStatus(testCard.id, CardState.SCRATCH_IN_PROGRESS)
        }
    }

    @Test
    fun deleteByIdTest() = runTest {
        repository.deleteById(testCard.id)
        coVerify(exactly = 1) { db.deleteById(testCard.id) }
    }

    @Test
    fun getAllCardsTest() = runTest {
        val fakeFlow: Flow<List<ScratchCardEntity>> = flowOf(listOf(testCard.toEntity()))
        coEvery { db.getAllCards() } returns fakeFlow

        val result = repository.getAllCards()
        // Here we just test that it maps correctly to Domain layer model
        var collectedResult: List<ScratchCard> = emptyList()
        result.collect { collectedResult = it }

        assertEquals(1, collectedResult.size)
        assertEquals(testCard.id, collectedResult[0].id)
        coVerify(exactly = 1) { db.getAllCards() }
    }

    @Test
    fun getCardByIdTest() = runTest {
        val fakeFlow: Flow<ScratchCardEntity?> = flowOf(testCard.toEntity())
        coEvery { db.getCardById(testCard.id) } returns fakeFlow

        val result = repository.getCardById(testCard.id)

        var collectedResult: ScratchCard? = null
        result.collect { collectedResult = it }

        assertEquals(testCard.id, collectedResult?.id)
        coVerify(exactly = 1) { db.getCardById(testCard.id) }
    }

    @Test
    fun verifyActivationCodeSuccessTest() = runTest {
        val code = "test-code"
        val response = ActivateResponse(android = "300000") // > 277028
        coEvery { activateService.getVersion(code) } returns response

        val result = repository.verifyActivationCode(code)

        assertEquals(true, result)
    }

    @Test
    fun verifyActivationCodeFailureThresholdTest() = runTest {
        val code = "test-code"
        val response = ActivateResponse(android = "100000") // <= 277028
        coEvery { activateService.getVersion(code) } returns response

        val result = repository.verifyActivationCode(code)

        assertEquals(false, result)
    }

    @Test
    fun verifyActivationCodeNullResponseTest() = runTest {
        val code = "test-code"
        val response = ActivateResponse(android = null)
        coEvery { activateService.getVersion(code) } returns response

        val result = repository.verifyActivationCode(code)

        assertEquals(false, result)
    }

    @Test(expected = RuntimeException::class)
    fun verifyActivationCodeExceptionTest() = runTest {
        val code = "test-code"
        coEvery { activateService.getVersion(code) } throws RuntimeException("Network Error")

        repository.verifyActivationCode(code)
    }
}
