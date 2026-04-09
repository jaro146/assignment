package com.example.o2assignment.activate.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CreditScore
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.card_repository.domain.ScratchCard
import com.example.card_repository.model.CardState
import com.example.card_repository.domain.AppError

interface Activate {

    @Immutable
    @Stable
    data class State(
        val isLoading: Boolean = false,
        val error: AppError? = null,
        val card: ScratchCard? = null,
    )

    sealed interface Event {
        data object Back : Event
        data object ActivateCard : Event
        data object DismissError : Event
    }

    sealed interface Effect {
        data object NavigateBack : Effect
        data class ShowToast(val message: String) : Effect
    }
}

fun Activate.State.getCardStateIcon(): ImageVector {
    return when (card?.cardState) {
        CardState.ACTIVATED -> Icons.Filled.CreditScore
        CardState.SCRATCHED -> Icons.Filled.CreditCard
        else -> Icons.Filled.Timelapse
    }
}