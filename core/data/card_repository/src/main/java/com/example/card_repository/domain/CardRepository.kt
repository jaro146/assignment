package com.example.card_repository.domain

import com.example.card_repository.model.CardState
import kotlinx.coroutines.flow.Flow

interface CardRepository {

    suspend fun insert(card: ScratchCard)

    suspend fun update(card: ScratchCard)

    suspend fun updateCardStatus(cardId: Int, status: CardState)

    suspend fun deleteById(cardId: Int)

    fun getAllCards(): Flow<List<ScratchCard>>

    fun getCardById(cardId: Int): Flow<ScratchCard?>

    suspend fun verifyActivationCode(code: String): Boolean
}
