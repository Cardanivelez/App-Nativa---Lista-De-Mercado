package com.holamundo.agoralist.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.holamundo.agoralist.data.local.entity.ProductEntity
import com.holamundo.agoralist.data.local.entity.ProductHistoryEntity
import com.holamundo.agoralist.data.local.model.ProductUnit
import com.holamundo.agoralist.data.local.model.UnitCategory
import com.holamundo.agoralist.data.repository.ProductRepository
import com.holamundo.agoralist.data.repository.ShoppingListRepository
import com.holamundo.agoralist.data.session.UserSession
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetailUiState(
    val products: List<ProductEntity> = emptyList(),
    val isListCompleted: Boolean = false,
    /** True after transition to “all checked”; cleared when list incomplete or [consumeListCompletionCelebration]. */
    val listCompletionCelebration: Boolean = false
)

data class ProductDetailUiState(
    val product: ProductEntity? = null
)

data class AddProductUiState(
    val name: String = "",
    val quantity: String = "",
    val unit: String = "",
    val price: String = "",
    val currency: String = "$",
    val selectedCategory: UnitCategory = UnitCategory.COUNT,
    val selectedUnit: ProductUnit = ProductUnit.UNIT,
    val customUnit: String = "",
    val notes: String = "",
    val isEditing: Boolean = false,
    val editingProductId: Long? = null,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val quantityError: String? = null,
    val priceError: String? = null,
    val saveSuccess: Boolean = false
)

class ProductViewModel(
    private val productRepository: ProductRepository,
    private val shoppingListRepository: ShoppingListRepository
) : ViewModel() {
    private val _detailUiState = MutableStateFlow(DetailUiState())
    val detailUiState: StateFlow<DetailUiState> = _detailUiState.asStateFlow()

    private val _addProductUiState = MutableStateFlow(AddProductUiState())
    val addProductUiState: StateFlow<AddProductUiState> = _addProductUiState.asStateFlow()
    private val _productDetailUiState = MutableStateFlow(ProductDetailUiState())
    val productDetailUiState: StateFlow<ProductDetailUiState> = _productDetailUiState.asStateFlow()

    private var observeProductsJob: Job? = null
    private var observedListId: Long? = null
    private var observeListStatusJob: Job? = null
    private var observeProductDetailJob: Job? = null
    private var observedProductId: Long? = null

    fun observeProducts(listId: Long) {
        if (observedListId == listId && observeProductsJob != null) return
        observedListId = listId
        observeProductsJob?.cancel()
        observeListStatusJob?.cancel()

        observeProductsJob = viewModelScope.launch {
            productRepository.getProductsByListId(listId).collect { products ->
                _detailUiState.update { state ->
                    state.copy(products = products)
                }
            }
        }

        observeListStatusJob = viewModelScope.launch {
            // No hay flow para una sola lista por ID en el repo actual, pero podemos consultar o crear uno.
            // Para simplificar, consultaremos el estado de la lista.
            val list = shoppingListRepository.getListById(listId)
            _detailUiState.update { it.copy(isListCompleted = list?.isCompleted ?: false) }
        }
    }

    fun completeList(listId: Long) {
        viewModelScope.launch {
            val list = shoppingListRepository.getListById(listId)
            if (list != null && !list.isCompleted) {
                shoppingListRepository.updateList(list.copy(isCompleted = true))
                
                // Guardar histórico
                val products = productRepository.getProductsByListIdOnce(listId)
                val userId = UserSession.currentUser.value?.id ?: 0L

                products.filter { it.isChecked && it.price != null }.forEach { product ->
                    val unit = ProductUnit.fromSymbol(product.unit) ?: ProductUnit.OTHER
                    val quantityDouble = product.quantity.toDoubleOrNull() ?: 1.0

                    // Precio por unidad base (gramo, ml o unidad)
                    val pricePerBaseUnit = (product.price!! / quantityDouble) / unit.factor

                    productRepository.insertHistory(
                        ProductHistoryEntity(
                            name = product.name,
                            category = unit.category.name,
                            unitName = if (unit == ProductUnit.OTHER) product.unit else "",
                            pricePerBaseUnit = pricePerBaseUnit,
                            currency = product.currency,
                            timestamp = System.currentTimeMillis(),
                            userId = userId
                        )
                    )
                }

                _detailUiState.update {
                    it.copy(
                        isListCompleted = true,
                        listCompletionCelebration = true
                    )
                }
            }
        }
    }

    fun restartList(listId: Long) {
        viewModelScope.launch {
            val list = shoppingListRepository.getListById(listId)
            if (list != null && list.isCompleted) {
                // Desmarcar la lista en la DB
                shoppingListRepository.updateList(list.copy(isCompleted = false))
                
                // Desmarcar todos los productos para empezar de nuevo
                productRepository.uncheckAllProducts(listId)

                _detailUiState.update {
                    it.copy(
                        isListCompleted = false,
                        listCompletionCelebration = false
                    )
                }
            }
        }
    }

    fun consumeListCompletionCelebration() {
        _detailUiState.update { it.copy(listCompletionCelebration = false) }
    }

    fun updateCheckedState(productId: Long, checked: Boolean) {
        viewModelScope.launch {
            productRepository.updateCheckedState(productId, checked)
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            productRepository.deleteProduct(product)
        }
    }

    /** Re-insert after undo; Room assigns a new row id. */
    fun restoreProductAfterUndo(product: ProductEntity) {
        viewModelScope.launch {
            productRepository.insertProduct(
                product.copy(id = 0L)
            )
        }
    }

    fun observeProductDetail(productId: Long) {
        if (observedProductId == productId && observeProductDetailJob != null) return
        observedProductId = productId
        observeProductDetailJob?.cancel()
        observeProductDetailJob = viewModelScope.launch {
            productRepository.getProductFlowById(productId).collect { product ->
                _productDetailUiState.update { it.copy(product = product) }
            }
        }
    }

    fun onNameChange(value: String) {
        _addProductUiState.update { it.copy(name = value, errorMessage = null) }
    }

    fun onQuantityChange(value: String) {
        // Permitir solo números y un punto decimal mientras se escribe para mejorar la UX
        if (value.isEmpty() || value.matches(Regex("""^\d*\.?\d*$"""))) {
            _addProductUiState.update { it.copy(quantity = value, quantityError = null, errorMessage = null) }
        }
    }

    fun onCategoryChange(category: UnitCategory) {
        _addProductUiState.update {
            val defaultUnit = ProductUnit.entries.find { it.category == category } ?: ProductUnit.UNIT
            it.copy(selectedCategory = category, selectedUnit = defaultUnit, errorMessage = null)
        }
    }

    fun onUnitChange(unit: ProductUnit) {
        _addProductUiState.update { it.copy(selectedUnit = unit, errorMessage = null) }
    }

    fun onCustomUnitChange(value: String) {
        _addProductUiState.update { it.copy(customUnit = value, errorMessage = null) }
    }

    fun onPriceChange(value: String) {
        if (value.isEmpty() || value.matches(Regex("""^\d*[.,]?\d{0,2}$"""))) {
            _addProductUiState.update { it.copy(price = value.replace(",", "."), priceError = null, errorMessage = null) }
        }
    }

    fun onCurrencyChange(value: String) {
        _addProductUiState.update { it.copy(currency = value, errorMessage = null) }
    }

    fun onNotesChange(value: String) {
        _addProductUiState.update { it.copy(notes = value, errorMessage = null) }
    }

    fun prepareAddProductForm(productId: Long) {
        viewModelScope.launch {
            if (productId <= 0L) {
                _addProductUiState.value = AddProductUiState()
                return@launch
            }
            val existing = productRepository.getProductById(productId)
            if (existing != null) {
                val unitObj = ProductUnit.fromSymbol(existing.unit)
                val category = unitObj?.category ?: if (existing.unit.isNotBlank()) UnitCategory.OTHER else UnitCategory.COUNT
                val selectedUnit = unitObj ?: ProductUnit.OTHER
                val customUnit = if (unitObj == null) existing.unit else ""

                _addProductUiState.value = AddProductUiState(
                    name = existing.name,
                    quantity = existing.quantity,
                    unit = existing.unit,
                    price = existing.price?.toString() ?: "",
                    currency = existing.currency,
                    selectedCategory = category,
                    selectedUnit = selectedUnit,
                    customUnit = customUnit,
                    notes = existing.notes,
                    isEditing = true,
                    editingProductId = existing.id
                )
            } else {
                _addProductUiState.value = AddProductUiState(
                    errorMessage = "Producto no encontrado"
                )
            }
        }
    }

    fun saveProduct(listId: Long) {
        val state = _addProductUiState.value

        var processedQuantity = state.quantity.trim()
        if (processedQuantity.startsWith(".")) processedQuantity = "0$processedQuantity"
        if (processedQuantity.endsWith(".")) processedQuantity = processedQuantity.removeSuffix(".")

        val isQuantityValid = processedQuantity.isNotEmpty() && processedQuantity.toDoubleOrNull() != null
        
        val priceDouble = state.price.toDoubleOrNull()
        val isPriceValid = state.price.isEmpty() || priceDouble != null

        if (state.name.isBlank() || state.quantity.isBlank() || !isQuantityValid || !isPriceValid) {
            _addProductUiState.update {
                it.copy(
                    errorMessage = if (state.name.isBlank()) "El nombre es obligatorio" else null,
                    quantityError = if (state.quantity.isBlank()) "La cantidad es obligatoria"
                                    else if (!isQuantityValid) "Debe ser un número válido (ej: 10 o 2.5)"
                                    else null,
                    priceError = if (!isPriceValid) "Precio inválido" else null
                )
            }
            return
        }

        val unitToSave = if (state.selectedCategory == UnitCategory.OTHER) {
            state.customUnit.trim()
        } else {
            state.selectedUnit.symbol
        }

        if (state.selectedCategory == UnitCategory.OTHER && unitToSave.isBlank()) {
            _addProductUiState.update {
                it.copy(errorMessage = "Especifique el tipo de unidad")
            }
            return
        }

        viewModelScope.launch {
            _addProductUiState.update { it.copy(isSaving = true, errorMessage = null) }
            val editingId = state.editingProductId
            if (state.isEditing && editingId != null) {
                val existing = productRepository.getProductById(editingId)
                if (existing == null) {
                    _addProductUiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = "No se pudo actualizar: producto no encontrado"
                        )
                    }
                    return@launch
                }
                productRepository.updateProduct(
                    existing.copy(
                        name = state.name.trim(),
                        quantity = processedQuantity,
                        unit = unitToSave,
                        price = priceDouble,
                        currency = state.currency.trim(),
                        notes = state.notes.trim(),
                        listId = listId
                    )
                )
            } else {
                productRepository.insertProduct(
                    ProductEntity(
                        name = state.name.trim(),
                        quantity = processedQuantity,
                        unit = unitToSave,
                        price = priceDouble,
                        currency = state.currency.trim(),
                        notes = state.notes.trim(),
                        listId = listId
                    )
                )
            }
            _addProductUiState.update { it.copy(isSaving = false, saveSuccess = true) }
        }
    }

    fun consumeSaveSuccess() {
        _addProductUiState.update {
            it.copy(
                saveSuccess = false,
                name = "",
                quantity = "",
                unit = "",
                price = "",
                currency = "$",
                selectedCategory = UnitCategory.COUNT,
                selectedUnit = ProductUnit.UNIT,
                customUnit = "",
                notes = "",
                isEditing = false,
                editingProductId = null,
                errorMessage = null,
                quantityError = null,
                priceError = null
            )
        }
    }

    class Factory(
        private val productRepository: ProductRepository,
        private val shoppingListRepository: ShoppingListRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
                return ProductViewModel(productRepository, shoppingListRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
