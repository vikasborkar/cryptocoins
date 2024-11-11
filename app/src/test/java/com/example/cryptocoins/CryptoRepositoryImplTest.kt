package com.example.cryptocoins

import com.example.cryptocoins.api.Crypto
import com.example.cryptocoins.api.CryptoApiService
import com.example.cryptocoins.api.NetworkResult
import com.google.gson.JsonParseException
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import kotlin.jvm.java


@RunWith(MockitoJUnitRunner::class)
class CryptoRepositoryImplTest {

    private lateinit var mockService: CryptoApiService
    private lateinit var repository: CryptoRepositoryImpl

    @Before
    fun setup() {
        mockService = mock(CryptoApiService::class.java)
        repository = CryptoRepositoryImpl(mockService)
    }

    @Test
    fun `fetchCoins returns NetworkResult Success on successful response`() = runBlocking {
        // Arrange
        val cryptoList = listOf(Crypto("Bitcoin", "BTC", true, true, "coin"))
        `when`(mockService.getCoins()).thenReturn(NetworkResult.Success(cryptoList))

        // Act
        val result = repository.fetchCoins()

        // Assert
        assertTrue(result is NetworkResult.Success)
        assertEquals(cryptoList, (result as NetworkResult.Success).data)
    }

    @Test
    fun `fetchCoins returns NetworkResult Error on failed response`() = runBlocking {
        // Arrange
        `when`(mockService.getCoins()).thenReturn(NetworkResult.Error(404, "Not Found"))

        // Act
        val result = repository.fetchCoins()

        // Assert
        assertTrue(result is NetworkResult.Error)
        assertEquals(404, (result as NetworkResult.Error).code)
        assertEquals("Not Found", result.message)
    }

    @Test
    fun `fetchCoins handles parsing failure`() = runBlocking {
        // Arrange
        `when`(mockService.getCoins()).thenReturn(NetworkResult.Exception(JsonParseException("Parsing error")))

        // Act
        val result = repository.fetchCoins()

        // Assert
        assertTrue(result is NetworkResult.Exception)
        assertEquals("Parsing error", (result as NetworkResult.Exception).e.message)
    }
}
