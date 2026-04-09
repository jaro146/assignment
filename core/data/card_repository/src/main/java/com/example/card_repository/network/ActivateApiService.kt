package com.example.card_repository.network

import com.example.card_repository.model.ActivateResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ActivateApiService {

    @GET("version")
    suspend fun getVersion(@Query("code") code: String): ActivateResponse

}