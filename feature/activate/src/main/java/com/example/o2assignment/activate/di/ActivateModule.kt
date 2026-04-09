package com.example.o2assignment.activate.di

import com.example.o2assignment.activate.domain.ActivateCardUseCase
import com.example.o2assignment.activate.ui.ActivateViewModel
import kotlinx.coroutines.CoroutineScope
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val activateModule = module {

    factory {
        ActivateCardUseCase(
            get(),
            get<CoroutineScope>(named("AppScope"))
        )
    }

    viewModelOf(::ActivateViewModel)

}
