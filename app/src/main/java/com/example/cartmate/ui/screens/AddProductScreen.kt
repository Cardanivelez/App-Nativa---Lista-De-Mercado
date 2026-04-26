package com.example.cartmate.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cartmate.data.local.model.ProductUnit
import com.example.cartmate.data.local.model.UnitCategory
import com.example.cartmate.navigation.AppRoutes
import com.example.cartmate.ui.components.ScreenTopBar
import com.example.cartmate.ui.viewmodel.ProductViewModel
import com.example.cartmate.ui.viewmodel.ViewModelProvider

private val InputShape = RoundedCornerShape(14.dp)
private val ButtonShape = RoundedCornerShape(14.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    navController: NavController,
    listId: Long,
    productId: Long
) {
    val context = LocalContext.current
    val productViewModel: ProductViewModel = viewModel(
        factory = ViewModelProvider.provideProductViewModelFactory(context)
    )
    val uiState by productViewModel.addProductUiState.collectAsState()

    LaunchedEffect(listId, productId) {
        productViewModel.prepareAddProductForm(productId)
    }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            val detailRoute = AppRoutes.detailRoute(listId)
            val popped = navController.popBackStack(
                route = detailRoute,
                inclusive = false,
                saveState = false
            )
            if (!popped) {
                navController.popBackStack()
            }
            productViewModel.consumeSaveSuccess()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            ScreenTopBar(
                title = if (uiState.isEditing) "Editar Producto" else "Agregar Producto",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.name,
                onValueChange = productViewModel::onNameChange,
                modifier = Modifier.fillMaxWidth(),
                shape = InputShape,
                singleLine = true,
                label = { Text("Nombre") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.quantity,
                    onValueChange = productViewModel::onQuantityChange,
                    modifier = Modifier.weight(1f),
                    shape = InputShape,
                    singleLine = true,
                    label = { Text("Cantidad") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.width(12.dp))

                var categoryExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded },
                    modifier = Modifier.weight(1.2f)
                ) {
                    OutlinedTextField(
                        value = uiState.selectedCategory.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Unidad") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier.menuAnchor(),
                        shape = InputShape,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        UnitCategory.entries.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.displayName) },
                                onClick = {
                                    productViewModel.onCategoryChange(category)
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            if (uiState.selectedCategory != UnitCategory.OTHER) {
                Spacer(modifier = Modifier.height(16.dp))
                var unitExpanded by remember { mutableStateOf(false) }
                val unitsInCategory = ProductUnit.entries.filter { it.category == uiState.selectedCategory }

                ExposedDropdownMenuBox(
                    expanded = unitExpanded,
                    onExpandedChange = { unitExpanded = !unitExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = if (uiState.selectedUnit.symbol.isNotBlank())
                            "${uiState.selectedUnit.displayName} (${uiState.selectedUnit.symbol})"
                        else uiState.selectedUnit.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo de ${uiState.selectedCategory.displayName}") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitExpanded) },
                        modifier = Modifier.menuAnchor(),
                        shape = InputShape,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = unitExpanded,
                        onDismissRequest = { unitExpanded = false }
                    ) {
                        unitsInCategory.forEach { unit ->
                            DropdownMenuItem(
                                text = { Text(if (unit.symbol.isNotBlank()) "${unit.displayName} (${unit.symbol})" else unit.displayName) },
                                onClick = {
                                    productViewModel.onUnitChange(unit)
                                    unitExpanded = false
                                }
                            )
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = uiState.customUnit,
                    onValueChange = productViewModel::onCustomUnitChange,
                    modifier = Modifier.fillMaxWidth(),
                    shape = InputShape,
                    singleLine = true,
                    label = { Text("Especificar unidad (ej: frasco, bolsa)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.notes,
                onValueChange = productViewModel::onNotesChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(128.dp),
                shape = InputShape,
                label = { Text("Notas") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )

            if (uiState.errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.errorMessage.orEmpty(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { productViewModel.saveProduct(listId) },
                enabled = !uiState.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = ButtonShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = if (uiState.isSaving) "Guardando..." else "Guardar Producto",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }
}
