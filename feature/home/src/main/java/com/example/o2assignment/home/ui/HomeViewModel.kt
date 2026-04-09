package com.example.o2assignment.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.card_repository.domain.ScratchCard
import com.example.o2assignment.home.ui.Home.Effect.*
import com.example.card_repository.domain.AppError
import com.example.o2assignment.home.domain.CreateNewCardUseCase
import com.example.o2assignment.home.domain.DeleteCardUseCase
import com.example.o2assignment.home.domain.LoadAllCardsUseCase
import kotlinx.collections.immutable.toImmutableList
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

class HomeViewModel(
    loadAllCardsUseCase: LoadAllCardsUseCase,
    private val deleteCardUseCase: DeleteCardUseCase,
    private val createNewCardUseCase: CreateNewCardUseCase
) : ViewModel() {

    init {
        Timber.i("$TAG init")
    }

    private val _uiState = MutableStateFlow(Home.State())

    val uiState = loadAllCardsUseCase().combine(_uiState) { cardsFromDb, currentState ->
        currentState.copy(cards = cardsFromDb.toImmutableList(), isLoading = false)
    }.onEach { Timber.i("$TAG _uiState:${it}") }.catch { e ->
        Timber.e(e, "$TAG error")
        emit(Home.State(error = AppError("Error load data", e), isLoading = false))
    }.onStart { emit(Home.State(isLoading = true)) }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000L), Home.State()
    )

    private val _effects = MutableSharedFlow<Home.Effect>(replay = 0)
    val effects = _effects.asSharedFlow()

    fun onEvent(event: Home.Event) {
        when (event) {
            is Home.Event.OnDeleteCard -> {
                _uiState.update { it.copy(selectedCardId = event.id) }
                sendEffect(Home.Effect.ShowDeleteDialog)
            }

            is Home.Event.OnDeleteConfirmed -> {
                uiState.value.selectedCardId?.let {
                    onDelete(it)
                    _uiState.update { it.copy(selectedCardId = null) }
                    sendEffect(Home.Effect.CloseDeleteDialog)
                }
            }

            is Home.Event.OnDeleteCanceled -> {
                _uiState.update { it.copy(selectedCardId = null) }
                sendEffect(Home.Effect.CloseDeleteDialog)
            }

            is Home.Event.OnScratchCard -> sendEffect(NavigateToScratch(event.id))

            is Home.Event.OnActivateCard -> sendEffect(NavigateToActivate(event.id))

            Home.Event.AddNewCard -> addNewCard()
        }
    }

    internal fun addNewCard() {
        viewModelScope.launch {
            try {
                createNewCardUseCase(ScratchCard())
            } catch (e: Exception) {
                Timber.e(e, "$TAG addNewCard error")
            }
        }
    }

    internal fun onDelete(cardId: Int) {
        viewModelScope.launch {
            Timber.i("$TAG delete id:$cardId")
            try {
                deleteCardUseCase(cardId)
            } catch (e: Exception) {
                Timber.e(e, "onDelete error")
            }
        }
    }

    private fun sendEffect(effect: Home.Effect) {
        viewModelScope.launch {
            _effects.emit(effect)
        }
    }

    override fun onCleared() {
        Timber.i("$TAG onCleared")
        super.onCleared()
    }

    companion object {
        private val TAG = HomeViewModel::class.java.simpleName
    }
}