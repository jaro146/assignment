package com.example.o2assignment.network.di

import com.example.o2assignment.network.BuildConfig
import com.example.o2assignment.network.di.util.ConnectivityManagerNetworkMonitor
import com.example.o2assignment.network.di.util.NetworkMonitor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.jvm.java

private const val TIMEOUT_SECONDS = 40L

val networkModule = module {

    single<NetworkMonitor> {
        ConnectivityManagerNetworkMonitor(
            context = get(),
            ioDispatcher = get(named(AppDispatchers.IO))
        )
    }

    single<OkHttpClient> {
        OkHttpClient().newBuilder()
            .addInterceptor(
                HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            )
            .callTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
    }
}