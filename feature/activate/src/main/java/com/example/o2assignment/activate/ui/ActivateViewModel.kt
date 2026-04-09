package com.example.o2assignment.activate.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.card_repository.domain.CardRepository
import com.example.o2assignment.activate.domain.ActivateCardUseCase
import com.example.card_repository.domain.AppError
import com.example.o2assignment.shared.domain.LoadCardByIdUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.coroutines.cancellation.CancellationException

class ActivateViewModel(
    loadCardByIdUseCase: LoadCardByIdUseCase,
    private val activateCardUseCase: ActivateCardUseCase,
    private val cardId: Int
) : ViewModel() {

    private val _effects = MutableSharedFlow<Activate.Effect>()
    val effects = _effects.asSharedFlow()

    private val _uiState = MutableStateFlow(Activate.State())
    val uiState = loadCardByIdUseCase(cardId)
        .combine(_uiState) { cardFromDb, currentState ->
            currentState.copy(card = cardFromDb, isLoading = false)
        }
        .onEach { newState ->
            Timber.i("$TAG uiState: $newState")
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            Activate.State()
        )

    init {
        Timber.i("$TAG init")
    }

    private fun activateCard() {
        uiState.value.card?.code?.let { code ->
            viewModelScope.launch {
                try {
                    val result = activateCardUseCase(cardId, code)
                    if (result.error != null) {
                        _uiState.update { it.copy(error = result.error) }
                    }
                } catch (e: CancellationException) {
                    Timber.e(e, "$TAG Activation viewmodel coroutine cancelled")
                    throw e
                } catch (e: Exception) {
                    Timber.e(e, "$TAG error by activate card")
                    _uiState.update {
                        it.copy(error = AppError("Failed to activate card", cause = e))
                    }
                }
            }
        } ?: run {
            _uiState.update {
                it.copy(error = AppError("Failed to activate, code is null"))
            }
        }
    }

    fun onEvent(event: Activate.Event) {
        when (event) {
            Activate.Event.Back -> sendEffect(Activate.Effect.NavigateBack)
            Activate.Event.ActivateCard -> activateCard()
            Activate.Event.DismissError -> _uiState.update { it.copy(error = null) }
        }
    }

    private fun sendEffect(effect: Activate.Effect) {
        viewModelScope.launch {
            _effects.emit(effect)
        }
    }

    override fun onCleared() {
        Timber.i("$TAG onCleared")
        super.onCleared()
    }

    companion object {
        private val TAG = ActivateViewModel::class.java.simpleName
    }
}
