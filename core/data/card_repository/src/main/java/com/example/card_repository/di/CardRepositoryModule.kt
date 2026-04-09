package com.example.card_repository.di

import androidx.room.Room
import com.example.card_repository.db.CardDao
import com.example.card_repository.db.CardDatabase
import com.example.card_repository.network.ActivateApiService
import com.example.card_repository.domain.CardRepository
import com.example.card_repository.repository.CardRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val cardRepositoryModule = module {

    single {
        Room.databaseBuilder(
            get(),
            CardDatabase::class.java,
            "card_database"
        ).build()
    }

    single<CardDao> { get<CardDatabase>().cardDao() }

    single<CardRepository> {
        CardRepositoryImpl(
            get<CardDao>(),
            get<ActivateApiService>(),
        )
    }

    single<ActivateApiService> { get<Retrofit>().create(ActivateApiService::class.java) }
}
