package com.example.cryptocoins

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptocoins.api.Crypto
import com.example.cryptocoins.api.onError
import com.example.cryptocoins.api.onException
import com.example.cryptocoins.api.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

class CryptoListViewModel(private val repository: CryptoRepository = CryptoRepositoryImpl()) :
    ViewModel() {

    private val _uiState: MutableStateFlow<CryptoListUiState> =
        MutableStateFlow(CryptoListUiState())
    val uiState: StateFlow<CryptoListUiState>
        get() = _uiState.asStateFlow()

    private val allCrypto = mutableListOf<Crypto>()

    init {
        fetchCoins()
    }

    fun retry() {
        _uiState.update { oldState ->
            oldState.copy(
                query = "",
                filter = Filter()
            )
        }
        fetchCoins()
    }

    private fun fetchCoins() {
        viewModelScope.launch {
            _uiState.update { oldState ->
                oldState.copy(isApiLoading = true)
            }

            val response = repository.fetchCoins()
            response.onSuccess { fetchedCoins ->
                allCrypto.addAll(fetchedCoins)
                _uiState.update { oldState ->
                    oldState.copy(
                        cryptos = fetchedCoins,
                        isApiLoading = false,
                        isApiSuccess = true,
                        apiErrorMessage = ""
                    )
                }
            }.onError { code, message ->
                _uiState.update { oldState ->
                    oldState.copy(
                        cryptos = listOf(),
                        isApiLoading = false,
                        isApiSuccess = false,
                        apiErrorMessage = message ?: "",
                    )
                }
            }.onException { e ->
                _uiState.update { oldState ->
                    oldState.copy(
                        cryptos = listOf(),
                        isApiLoading = false,
                        isApiSuccess = false,
                        apiErrorMessage = "",
                    )
                }
            }
        }
    }

    fun setFilter(filter: Filter) {
        val newCryptos = allCrypto.filter {
            (it.name.contains(_uiState.value.query, ignoreCase = true) ||
                    it.symbol.contains(_uiState.value.query, ignoreCase = true)) &&
                    (filter.isActive == null || it.isActive == filter.isActive) &&
                    (filter.type == null || filter.type == "" || it.type == filter.type) &&
                    (it.isNew == filter.isNew || filter.isNew == false)
        }

        _uiState.update { oldState ->
            oldState.copy(
                cryptos = newCryptos,
                filter = filter
            )
        }
    }

    fun setQuery(query: String) {
        //filter resets to default when query changes

        val newCryptos = allCrypto.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.symbol.contains(query, ignoreCase = true)
        }

        _uiState.update { oldState ->
            oldState.copy(
                cryptos = newCryptos,
                query = query,
                filter = Filter()
            )
        }
    }
}

data class CryptoListUiState(
    val cryptos: List<Crypto> = listOf(),
    val isApiLoading: Boolean = false,
    val isApiSuccess: Boolean = false,
    val apiErrorMessage: String = "",
    val query: String = "",
    val filter: Filter = Filter()
)

@Parcelize
data class Filter(
    val isActive: Boolean? = null,
    val type: String? = null,
    val isNew: Boolean = false,
) : Parcelable {
    companion object {
        const val TYPE_COIN = "coin"
        const val TYPE_TOKEN = "token"
    }
}
