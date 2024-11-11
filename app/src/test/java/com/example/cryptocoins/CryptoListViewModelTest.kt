package com.example.cryptocoins

import com.example.cryptocoins.api.Crypto
import com.example.cryptocoins.api.NetworkResult
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class CryptoListViewModelTest {

    @Mock
    private lateinit var mockRepository: CryptoRepository

    private lateinit var viewModel: CryptoListViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(Dispatchers.Unconfined)
        viewModel = CryptoListViewModel(mockRepository)
//        viewModel.repository = mockRepository
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchCoins updates uiState with successful data`() = runBlocking {
        // Arrange
        val cryptoList = listOf(Crypto("Bitcoin", "BTC", true, true, "coin"))
        Mockito.`when`(mockRepository.fetchCoins()).thenReturn(NetworkResult.Success(cryptoList))

        // Act
        viewModel.retry()  // triggers fetchCoins internally

        // Assert
        val uiState = viewModel.uiState.first()
        assertTrue(uiState.isApiSuccess)
        assertEquals(cryptoList, uiState.cryptos)
    }

    @Test
    fun `fetchCoins updates uiState with error message on failure`() = runBlocking {
        // Arrange
        Mockito.`when`(mockRepository.fetchCoins())
            .thenReturn(NetworkResult.Error(404, "Not Found"))

        // Act
        viewModel.retry()  // triggers fetchCoins internally

        // Assert
        val uiState = viewModel.uiState.first()
        assertTrue(!uiState.isApiSuccess)
        assertEquals("Not Found", uiState.apiErrorMessage)
    }

    @Test
    fun `setFilter updates UI with filtered crypto list`() = runBlocking {
        // Arrange
        val cryptoList = listOf(
            Crypto("Bitcoin", "BTC", true, true, "coin"),
            Crypto("Ethereum", "ETH", true, true, "token")
        )

        val allCryptoProperty = CryptoListViewModel::class.java.getDeclaredField("allCrypto")
        allCryptoProperty.isAccessible = true
        val allCrypto = allCryptoProperty.get(viewModel) as MutableList<Crypto>
        allCrypto.addAll(cryptoList)
        val filter = Filter(isActive = true, type = "coin")

        // Act
        viewModel.setFilter(filter)

        // Assert
        val uiState = viewModel.uiState.first()
        assertEquals(listOf(cryptoList[0]), uiState.cryptos)  // Only Bitcoin matches the filter
    }

    @Test
    fun `setQuery updates UI with filtered crypto list based on query`() = runBlocking {
        // Arrange
        val cryptoList = listOf(
            Crypto("Bitcoin", "BTC", true, true, "coin"),
            Crypto("Ethereum", "ETH", true, true, "token")
        )
        val allCryptoProperty = CryptoListViewModel::class.java.getDeclaredField("allCrypto")
        allCryptoProperty.isAccessible = true
        val allCrypto = allCryptoProperty.get(viewModel) as MutableList<Crypto>
        allCrypto.addAll(cryptoList)

        // Act
        viewModel.setQuery("eth")

        // Assert
        val uiState = viewModel.uiState.first()
        assertEquals(listOf(cryptoList[1]), uiState.cryptos)  // Only Ethereum matches "eth"
    }
}
