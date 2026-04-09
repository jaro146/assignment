package com.example.o2assignment.scratch.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CreditCardOff
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.card_repository.domain.ScratchCard
import com.example.card_repository.model.CardState
import com.example.card_repository.domain.AppError

interface Scratch {

    @Immutable
    @Stable
    data class State(
        val isLoading: Boolean = false,
        val error: AppError? = null,
        val card: ScratchCard? = null,
    )

    sealed interface Event {
        data object Back : Event
        data object ScratchCard : Event
        data object DismissError : Event
    }

    sealed interface Effect {
        data object NavigateBack : Effect
        data class ShowToast(val message: String) : Effect
    }
}

fun Scratch.State.getScratchCardIcon(): ImageVector {
    return when {
        error != null -> Icons.Filled.CreditCardOff
        card?.cardState == CardState.SCRATCH_IN_PROGRESS -> Icons.Filled.Timelapse
        !card?.code.isNullOrEmpty() -> Icons.Filled.CreditCard
        else -> Icons.Filled.CardGiftcard
    }
}


