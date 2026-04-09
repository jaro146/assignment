package com.example.o2assignment.shared.domain

import com.example.card_repository.domain.CardRepository

class LoadCardByIdUseCase(
    private val cardRepository: CardRepository
) {
    operator fun invoke(cardId: Int) = cardRepository.getCardById(cardId)
}