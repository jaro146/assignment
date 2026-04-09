package com.example.o2assignment.home.domain

import com.example.card_repository.domain.CardRepository

class LoadAllCardsUseCase(
    private val cardRepository: CardRepository
) {
    operator fun invoke() = cardRepository.getAllCards()
}