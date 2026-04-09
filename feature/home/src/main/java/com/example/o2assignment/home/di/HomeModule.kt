package com.example.o2assignment.home.di

import com.example.o2assignment.home.domain.CreateNewCardUseCase
import com.example.o2assignment.home.domain.DeleteCardUseCase
import com.example.o2assignment.home.domain.LoadAllCardsUseCase
import com.example.o2assignment.home.ui.HomeViewModel
import com.example.o2assignment.shared.domain.LoadCardByIdUseCase
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val homeModule = module {

    viewModelOf(::HomeViewModel)

    factory { LoadAllCardsUseCase(get()) }
    factory { DeleteCardUseCase(get()) }
    factory { CreateNewCardUseCase(get()) }
}