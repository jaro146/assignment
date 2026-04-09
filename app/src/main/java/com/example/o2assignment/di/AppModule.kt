package com.example.o2assignment.di

import com.example.card_repository.di.cardRepositoryModule
import com.example.o2assignment.activate.di.activateModule
import com.example.o2assignment.home.di.homeModule
import com.example.o2assignment.network.di.dispatchersModule
import com.example.o2assignment.network.di.networkModule
import com.example.o2assignment.scratch.di.scratchModule
import org.koin.core.module.Module
import org.koin.dsl.module

val appModules: Module
    get() = module {
        includes(
            homeModule, scratchModule, activateModule,
        )
        includes(cardRepositoryModule)
        includes(
            networkModule, dispatchersModule,
        )
    }
