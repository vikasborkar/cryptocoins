package com.example.cryptocoins

import org.junit.Test
import retrofit2.Retrofit
import com.example.cryptocoins.api.ApiClient
import com.example.cryptocoins.api.CryptoApiService
import org.junit.Assert.assertTrue

class ApiClientTest {
    @Test
    fun `ApiClient configuration should have correct base URL`() {
        val retrofitField = ApiClient::class.java.getDeclaredField("retrofit")
        retrofitField.isAccessible = true
        val retrofit = retrofitField.get(ApiClient) as Retrofit

        assertTrue(
            retrofit.baseUrl()
                .toString() == "https://37656be98b8f42ae8348e4da3ee3193f.api.mockbin.io/"
        )
    }

    @Test
    fun `ApiClient should provide CryptoApiService instance`() {
        val service = ApiClient.service
        assertTrue(service is CryptoApiService)
    }
}


