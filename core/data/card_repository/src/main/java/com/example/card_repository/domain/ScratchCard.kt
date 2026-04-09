package com.example.card_repository.domain

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.example.card_repository.model.CardState

@Immutable
@Stable
data class ScratchCard(
    val id: Int = 0,
    val cardState: CardState = CardState.UNSCRATCHED,
    val code: String? = null
) {
    fun alreadyScratched(): Boolean = cardState == CardState.SCRATCHED
    fun scratchInProgress(): Boolean = cardState == CardState.SCRATCH_IN_PROGRESS
    fun alreadyActivated(): Boolean = cardState == CardState.ACTIVATED
    fun activationInProgress(): Boolean = cardState == CardState.ACTIVATION_IN_PROGRESS

    fun scratchButtonEnabled(): Boolean =
        cardState in listOf(CardState.UNSCRATCHED, CardState.SCRATCH_IN_PROGRESS)

    fun activateButtonEnabled(): Boolean =
        cardState in listOf(CardState.SCRATCHED, CardState.ACTIVATION_IN_PROGRESS)
}
