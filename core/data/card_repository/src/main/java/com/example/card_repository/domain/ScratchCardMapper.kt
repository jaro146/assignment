package com.example.card_repository.domain

import com.example.card_repository.model.ScratchCardEntity

fun ScratchCardEntity.toDomain(): ScratchCard {
    return ScratchCard(
        id = this.id,
        cardState = this.cardState,
        code = this.code
    )
}

fun ScratchCard.toEntity(): ScratchCardEntity {
    return ScratchCardEntity(
        id = this.id,
        cardState = this.cardState,
        code = this.code
    )
}
