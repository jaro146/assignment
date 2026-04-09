package com.example.o2assignment.home.domain

import com.example.card_repository.domain.CardRepository

class DeleteCardUseCase(
    private val cardRepository: CardRepository
) {
    suspend operator fun invoke(cardId: Int) = cardRepository.deleteById(cardId)
}