package com.example.o2assignment.home.domain

import com.example.card_repository.domain.CardRepository
import com.example.card_repository.domain.ScratchCard

class CreateNewCardUseCase(
    private val cardRepository: CardRepository
) {
    suspend operator fun invoke(card: ScratchCard) = cardRepository.insert(card)
}