package com.example.cartmate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cartmate.data.local.entity.ProductEntity
import com.example.cartmate.data.local.model.ProductUnit
import com.example.cartmate.data.local.model.UnitCategory
import com.example.cartmate.data.repository.ProductRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetailUiState(
    val products: List<ProductEntity> = emptyList(),
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
    val selectedCategory: UnitCategory = UnitCategory.COUNT,
    val selectedUnit: ProductUnit = ProductUnit.UNIT,
    val customUnit: String = "",
    val notes: String = "",
    val isEditing: Boolean = false,
    val editingProductId: Long? = null,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val quantityError: String? = null,
    val saveSuccess: Boolean = false
)

class ProductViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {
    private val _detailUiState = MutableStateFlow(DetailUiState())
    val detailUiState: StateFlow<DetailUiState> = _detailUiState.asStateFlow()

    private val _addProductUiState = MutableStateFlow(AddProductUiState())
    val addProductUiState: StateFlow<AddProductUiState> = _addProductUiState.asStateFlow()
    private val _productDetailUiState = MutableStateFlow(ProductDetailUiState())
    val productDetailUiState: StateFlow<ProductDetailUiState> = _productDetailUiState.asStateFlow()

    private var observeProductsJob: Job? = null
    private var observedListId: Long? = null
    private var observeProductDetailJob: Job? = null
    private var observedProductId: Long? = null

    fun observeProducts(listId: Long) {
        if (observedListId == listId && observeProductsJob != null) return
        observedListId = listId
        observeProductsJob?.cancel()
        observeProductsJob = viewModelScope.launch {
            var initialized = false
            var prevAllChecked = false
            productRepository.getProductsByListId(listId).collect { products ->
                val allChecked = products.isNotEmpty() && products.all { it.isChecked }
                val fireCelebration = initialized && allChecked && !prevAllChecked
                prevAllChecked = allChecked
                initialized = true
                _detailUiState.update { state ->
                    val celebration =
                        allChecked && (fireCelebration || state.listCompletionCelebration)
                    state.copy(
                        products = products,
                        listCompletionCelebration = celebration
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

        if (state.name.isBlank() || state.quantity.isBlank() || !isQuantityValid) {
            _addProductUiState.update {
                it.copy(
                    errorMessage = if (state.name.isBlank()) "El nombre es obligatorio" else null,
                    quantityError = if (state.quantity.isBlank()) "La cantidad es obligatoria"
                                    else if (!isQuantityValid) "Debe ser un número válido (ej: 10 o 2.5)"
                                    else null
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
                selectedCategory = UnitCategory.COUNT,
                selectedUnit = ProductUnit.UNIT,
                customUnit = "",
                notes = "",
                isEditing = false,
                editingProductId = null,
                errorMessage = null,
                quantityError = null
            )
        }
    }

    class Factory(
        private val productRepository: ProductRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
                return ProductViewModel(productRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
