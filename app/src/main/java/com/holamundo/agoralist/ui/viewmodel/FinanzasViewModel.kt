package com.holamundo.agoralist.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.holamundo.agoralist.data.local.dao.HistoricalProduct
import com.holamundo.agoralist.data.local.entity.ProductHistoryEntity
import com.holamundo.agoralist.data.repository.ProductRepository
import com.holamundo.agoralist.data.session.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FinanzasUiState(
    val historicalProducts: List<HistoricalProduct> = emptyList(),
    val selectedProductHistory: List<ProductHistoryEntity> = emptyList(),
    val selectedProductName: String? = null,
    val selectedCategory: String? = null,
    val selectedUnitName: String? = null
)

class FinanzasViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(FinanzasUiState())
    val uiState: StateFlow<FinanzasUiState> = _uiState.asStateFlow()

    init {
        observeHistoricalProducts()
    }

    private fun observeHistoricalProducts() {
        viewModelScope.launch {
            UserSession.currentUser.collect { user ->
                val userId = user?.id ?: return@collect
                productRepository.getUniqueHistoricalProducts(userId).collect { products ->
                    _uiState.update { it.copy(historicalProducts = products) }
                }
            }
        }
    }

    fun selectProduct(product: HistoricalProduct) {
        val userId = UserSession.currentUser.value?.id ?: return
        viewModelScope.launch {
            productRepository.getHistoryForProduct(userId, product.name, product.category, product.unit_name).collect { history ->
                _uiState.update { it.copy(
                    selectedProductHistory = history,
                    selectedProductName = product.name,
                    selectedCategory = product.category,
                    selectedUnitName = product.unit_name
                ) }
            }
        }
    }

    fun deselectProduct() {
        _uiState.update { it.copy(
            selectedProductHistory = emptyList(),
            selectedProductName = null,
            selectedCategory = null,
            selectedUnitName = null
        ) }
    }

    class Factory(private val productRepository: ProductRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FinanzasViewModel(productRepository) as T
        }
    }
}
