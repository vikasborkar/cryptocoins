package com.example.cryptocoins.api

import com.example.cryptocoins.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object ApiClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(NetworkResultCallAdapterFactory.create())
        .build()

    val service: CryptoApiService = retrofit.create()
}