package com.example.o2assignment.home.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CreditScore
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.card_repository.domain.ScratchCard
import com.example.card_repository.model.CardState
import com.example.card_repository.domain.AppError
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

interface Home {

    @Immutable
    @Stable
    data class State(
        val isLoading: Boolean = false,
        val error: AppError? = null,
        val cards: ImmutableList<ScratchCard> = persistentListOf(),
        val selectedCardId: Int? = null,
    )

    sealed interface Event {
        data class OnActivateCard(val id: Int) : Event
        data class OnScratchCard(val id: Int) : Event
        data class OnDeleteCard(val id: Int) : Event
        object OnDeleteConfirmed : Event
        object OnDeleteCanceled : Event
        object AddNewCard : Event
    }

    sealed interface Effect {
        object ShowDeleteDialog : Effect
        object CloseDeleteDialog : Effect
        data class NavigateToScratch(val id: Int) : Effect
        data class NavigateToActivate(val id: Int) : Effect
    }
}

fun CardState.getCardStateIcon(): ImageVector {
    return when (this) {
        CardState.SCRATCHED -> Icons.Filled.CreditCard
        CardState.ACTIVATED -> Icons.Filled.CreditScore
        CardState.ACTIVATION_IN_PROGRESS -> Icons.Filled.Timelapse
        else -> Icons.Filled.CardGiftcard
    }
}
