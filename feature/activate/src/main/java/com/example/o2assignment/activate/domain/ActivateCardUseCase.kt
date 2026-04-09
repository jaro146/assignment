package com.example.o2assignment.activate.domain

import com.example.card_repository.domain.CardRepository
import com.example.card_repository.model.CardActionResult
import com.example.card_repository.model.CardState
import com.example.card_repository.domain.AppError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.seconds

class ActivateCardUseCase(
    private val cardRepository: CardRepository,
    private val externalScope: CoroutineScope,
) {
    suspend operator fun invoke(cardId: Int, code: String): CardActionResult {
        val deferredResult = externalScope.async {
            try {
                cardRepository.updateCardStatus(cardId, CardState.ACTIVATION_IN_PROGRESS)

                val isSuccess = cardRepository.verifyActivationCode(code)

                if (isSuccess) {
                    cardRepository.updateCardStatus(cardId, CardState.ACTIVATED)
                    CardActionResult(activationResult = true)
                } else {
                    cardRepository.updateCardStatus(cardId, CardState.SCRATCHED)
                    CardActionResult(
                        error = AppError("Activation failed: version check did not pass.")
                    )
                }
            } catch (e: CancellationException) {
                cardRepository.updateCardStatus(cardId, CardState.SCRATCHED)
                throw e
            } catch (e: Exception) {
                cardRepository.updateCardStatus(cardId, CardState.SCRATCHED)
                CardActionResult(
                    error = AppError("Failed to activate card", cause = e)
                )
            }
        }
        return deferredResult.await()
    }
}
