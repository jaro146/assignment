package com.example.o2assignment.network.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.qualifier.named
import org.koin.dsl.module
import timber.log.Timber

enum class AppDispatchers {
    Default,
    IO,
}

val dispatchersModule = module {

    single(named("GlobalExceptionHandler")) {
        CoroutineExceptionHandler { _, throwable ->
            Timber.e(throwable, "GlobalExceptionHandler throwable")
            // FirebaseCrashlytics.getInstance().recordException(throwable)
        }
    }

    single<CoroutineScope>(named("AppScope")) {
        val dispatcher = get<CoroutineDispatcher>(named(AppDispatchers.Default))
        val handler = get<CoroutineExceptionHandler>(named("GlobalExceptionHandler"))
        CoroutineScope(SupervisorJob() + dispatcher + handler)
    }

    single(named(AppDispatchers.IO)) { Dispatchers.IO }
    single(named(AppDispatchers.Default)) { Dispatchers.Default }
}