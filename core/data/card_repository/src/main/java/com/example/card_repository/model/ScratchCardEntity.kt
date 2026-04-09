package com.example.card_repository.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "cards")
data class ScratchCardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val cardState: CardState = CardState.UNSCRATCHED,
    val code: String? = null
)

enum class CardState {
    UNSCRATCHED, SCRATCHED, SCRATCH_IN_PROGRESS, ACTIVATED, ACTIVATION_IN_PROGRESS
}