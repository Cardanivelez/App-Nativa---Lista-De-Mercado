package com.holamundo.agoralist.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.holamundo.agoralist.data.local.entity.ShoppingListEntity
import com.holamundo.agoralist.data.local.model.ShoppingListWithProductCount
import com.holamundo.agoralist.data.repository.ShoppingListRepository
import com.holamundo.agoralist.ui.model.LIST_CATEGORY_GENERAL
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val lists: List<ShoppingListWithProductCount> = emptyList(),
    val newListName: String = "",
    /** Clave persistida en `ShoppingListEntity.icon`; por defecto [LIST_CATEGORY_GENERAL]. */
    val selectedCategoryKey: String = LIST_CATEGORY_GENERAL,
    val errorMessage: String? = null,
    val createListSaved: Boolean = false,
    /** Si no es null, mostrar diálogo de confirmación para borrar esta lista. */
    val listIdPendingDelete: Long? = null,
    /** Mensaje puntual para Snackbar (p. ej. tras eliminar). */
    val snackbarMessage: String? = null
)

class HomeViewModel(
    private val shoppingListRepository: ShoppingListRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var observedUserId: Long? = null

    fun observeLists(userId: Long?) {
        if (userId == null || observedUserId == userId) return
        observedUserId = userId
        viewModelScope.launch {
            shoppingListRepository.getListsWithProductCountByUserId(userId).collect { lists ->
                _uiState.update { it.copy(lists = lists) }
            }
        }
    }

    fun clearCreateForm() {
        _uiState.update {
            it.copy(
                newListName = "",
                selectedCategoryKey = LIST_CATEGORY_GENERAL,
                errorMessage = null,
                createListSaved = false
            )
        }
    }

    fun onListNameChange(value: String) {
        _uiState.update { it.copy(newListName = value, errorMessage = null) }
    }

    fun onListCategorySelected(categoryKey: String) {
        _uiState.update { it.copy(selectedCategoryKey = categoryKey, errorMessage = null) }
    }

    fun createList(userId: Long?) {
        if (userId == null) {
            _uiState.update { it.copy(errorMessage = "Sesión no disponible") }
            return
        }
        val name = _uiState.value.newListName.trim()
        if (name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Ingresa un nombre de lista") }
            return
        }
        viewModelScope.launch {
            val categoryKey = _uiState.value.selectedCategoryKey.trim()
                .ifBlank { LIST_CATEGORY_GENERAL }
            shoppingListRepository.insertList(
                ShoppingListEntity(
                    name = name,
                    icon = categoryKey,
                    userId = userId
                )
            )
            _uiState.update { it.copy(createListSaved = true, errorMessage = null) }
        }
    }

    fun consumeCreateSuccess() {
        clearCreateForm()
    }

    fun requestDeleteList(listId: Long) {
        _uiState.update { it.copy(listIdPendingDelete = listId) }
    }

    fun dismissDeleteListDialog() {
        _uiState.update { it.copy(listIdPendingDelete = null) }
    }

    fun confirmDeleteList() {
        val listId = _uiState.value.listIdPendingDelete ?: return
        viewModelScope.launch {
            val entity = shoppingListRepository.getListById(listId) ?: run {
                _uiState.update { it.copy(listIdPendingDelete = null) }
                return@launch
            }
            shoppingListRepository.deleteList(entity)
            _uiState.update {
                it.copy(
                    listIdPendingDelete = null,
                    snackbarMessage = "Lista eliminada"
                )
            }
        }
    }

    fun consumeSnackbarMessage() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    class Factory(
        private val shoppingListRepository: ShoppingListRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(shoppingListRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
