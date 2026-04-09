package com.example.o2assignment.scratch.di

import com.example.o2assignment.shared.domain.LoadCardByIdUseCase
import com.example.o2assignment.scratch.domain.ScratchCardUseCase
import com.example.o2assignment.scratch.ui.ScratchViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val scratchModule = module {

    viewModel { (cardId: Int) -> ScratchViewModel(get(), get(), cardId) }

    factory { ScratchCardUseCase(get()) }

    factory { LoadCardByIdUseCase(get()) }
}
