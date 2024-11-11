package com.example.cryptocoins.api

import retrofit2.http.GET

interface CryptoApiService {
    @GET("/")
    suspend fun getCoins(): NetworkResult<List<Crypto>>
}