package com.example.cryptocoins

import com.example.cryptocoins.api.ApiClient
import com.example.cryptocoins.api.Crypto
import com.example.cryptocoins.api.CryptoApiService
import com.example.cryptocoins.api.NetworkResult

class CryptoRepositoryImpl(private val apiService: CryptoApiService = ApiClient.service) :
    CryptoRepository {
    override suspend fun fetchCoins(): NetworkResult<List<Crypto>> = apiService.getCoins()
}

interface CryptoRepository {
    suspend fun fetchCoins(): NetworkResult<List<Crypto>>
}