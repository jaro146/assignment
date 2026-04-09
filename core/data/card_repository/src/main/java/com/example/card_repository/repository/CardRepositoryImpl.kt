package com.example.card_repository.repository

import com.example.card_repository.db.CardDao
import com.example.card_repository.domain.CardRepository
import com.example.card_repository.domain.ScratchCard
import com.example.card_repository.domain.toDomain
import com.example.card_repository.domain.toEntity
import com.example.card_repository.model.CardState
import com.example.card_repository.network.ActivateApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import kotlin.time.Duration.Companion.seconds

class CardRepositoryImpl(
    private val db: CardDao,
    private val activateService: ActivateApiService,
) : CardRepository {

    override suspend fun insert(card: ScratchCard) {
        db.insert(card.toEntity())
    }

    override suspend fun update(card: ScratchCard) {
        db.update(card.toEntity())
    }

    override suspend fun updateCardStatus(cardId: Int, status: CardState) {
        db.updateCardStatus(cardId, status)
    }

    override suspend fun deleteById(cardId: Int) {
        db.deleteById(cardId)
    }

    override fun getAllCards(): Flow<List<ScratchCard>> {
        return db.getAllCards().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getCardById(cardId: Int): Flow<ScratchCard?> {
        return db.getCardById(cardId).map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun verifyActivationCode(code: String): Boolean {
        delay(7.seconds.inWholeMilliseconds)
        val versionResponse = activateService.getVersion(code)
        return versionResponse.android?.toBigDecimal()?.let {
            it > ACTIVATION_THRESHOLD
        } ?: false
    }
}

private val ACTIVATION_THRESHOLD = BigDecimal.valueOf(277028)
