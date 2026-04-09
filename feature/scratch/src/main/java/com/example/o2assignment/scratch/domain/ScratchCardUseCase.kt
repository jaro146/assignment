package com.example.o2assignment.scratch.domain

import com.example.card_repository.model.CardActionResult
import com.example.card_repository.model.CardState
import com.example.card_repository.domain.CardRepository
import com.example.card_repository.domain.ScratchCard
import com.example.card_repository.domain.AppError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.seconds

class ScratchCardUseCase(
    private val cardRepository: CardRepository
) {
    suspend operator fun invoke(cardId: Int): CardActionResult {
        return withContext(Dispatchers.IO) {
            try {
                cardRepository.updateCardStatus(cardId, CardState.SCRATCH_IN_PROGRESS)

                delay(5.seconds.inWholeMilliseconds)
                val code = UUID.randomUUID().toString()

                cardRepository.update(
                    ScratchCard(id = cardId, cardState = CardState.SCRATCHED, code = code)
                )

                CardActionResult(code = code)
            } catch (e: CancellationException) {
                cardRepository.updateCardStatus(cardId, CardState.UNSCRATCHED)
                throw e
            } catch (e: Exception) {
                cardRepository.updateCardStatus(cardId, CardState.UNSCRATCHED)
                CardActionResult(
                    error = AppError("Failed to scratch card", cause = e)
                )
            }
        }
    }
}