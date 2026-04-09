package com.example.o2assignment.scratch.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.card_repository.domain.CardRepository
import com.example.o2assignment.shared.domain.LoadCardByIdUseCase
import com.example.o2assignment.scratch.domain.ScratchCardUseCase
import com.example.card_repository.domain.AppError
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.coroutines.cancellation.CancellationException

class ScratchViewModel(
    loadCardByIdUseCase: LoadCardByIdUseCase,
    private val scratchCardUseCase: ScratchCardUseCase,
    private val cardId: Int
) : ViewModel() {

    private val _effects = MutableSharedFlow<Scratch.Effect>()
    val effects = _effects.asSharedFlow()

    private val _uiState = MutableStateFlow(Scratch.State())
    val uiState = loadCardByIdUseCase(cardId)
        .combine(_uiState) { cardFromDb, currentState ->
            currentState.copy(card = cardFromDb, isLoading = false)
        }
        .onEach { Timber.i("$TAG uiState:$it") }
        .onStart { emit(Scratch.State(isLoading = true)) }
        .catch { e ->
            Timber.e(e, "$TAG error load card from db")
            emit(Scratch.State(error = AppError("Error load data", e), isLoading = false))
        }
        .stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000L), Scratch.State()
        )

    fun onEvent(event: Scratch.Event) {
        when (event) {
            Scratch.Event.Back -> sendEffect(Scratch.Effect.NavigateBack)
            Scratch.Event.ScratchCard -> scratchCard()
            Scratch.Event.DismissError -> _uiState.update { it.copy(error = null) }
        }
    }

    private fun scratchCard() {
        viewModelScope.launch {
            try {
                val result = scratchCardUseCase(cardId)
                if (result.error != null) {
                    _uiState.update { it.copy(error = result.error) }
                }
            } catch (e: CancellationException) {
                Timber.e(e, "$TAG Scratch card was cancelled.")
                throw e
            } catch (e: Exception) {
                Timber.e(e, "$TAG error by scratching card")
                _uiState.update {
                    it.copy(error = AppError("Failed to scratch card", cause = e))
                }
            }
        }
    }

    private fun sendEffect(effect: Scratch.Effect) {
        viewModelScope.launch {
            _effects.emit(effect)
        }
    }

    override fun onCleared() {
        Timber.i("$TAG onCleared")
        super.onCleared()
    }

    companion object {
        private val TAG = ScratchViewModel::class.java.simpleName
    }
}
