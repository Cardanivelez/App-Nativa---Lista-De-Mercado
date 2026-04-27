package com.holamundo.agoralist.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.animation.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import com.holamundo.agoralist.data.local.entity.ProductEntity
import com.holamundo.agoralist.navigation.AppRoutes
import com.holamundo.agoralist.ui.components.ScreenTopBar
import com.holamundo.agoralist.ui.viewmodel.ProductViewModel
import com.holamundo.agoralist.ui.viewmodel.ViewModelProvider

private val ProductCardShape = RoundedCornerShape(16.dp)

@Composable
fun DetailScreen(
    navController: NavController,
    listId: Long
) {
    val context = LocalContext.current
    val productViewModel: ProductViewModel = viewModel(
        factory = ViewModelProvider.provideProductViewModelFactory(context)
    )

    val uiState by productViewModel.detailUiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(listId) {
        productViewModel.observeProducts(listId)
    }

    LaunchedEffect(uiState.listCompletionCelebration) {
        if (uiState.listCompletionCelebration) {
            delay(2800)
            productViewModel.consumeListCompletionCelebration()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            ScreenTopBar(
                title = "Detalle de productos",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
        ) {

            ListCompletedBanner(uiState.isListCompleted)

            if (uiState.isListCompleted) {
                Button(
                    onClick = { productViewModel.restartList(listId) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    )
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reiniciar Lista")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (uiState.products.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    DetailProductsEmptyState()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.products, key = { it.id }) { product ->
                        ProductCard(
                            product = product,
                            isListCompleted = uiState.isListCompleted,
                            onCheckedChange = {
                                if (!uiState.isListCompleted) {
                                    productViewModel.updateCheckedState(product.id, it)
                                }
                            },
                            onClick = {
                                navController.navigate("productDetail/${product.id}")
                            },
                            onDelete = {
                                if (!uiState.isListCompleted) {
                                    val snapshot = product
                                    scope.launch {
                                        productViewModel.deleteProduct(snapshot)
                                        val result = snackbarHostState.showSnackbar(
                                            "Producto eliminado",
                                            "Deshacer"
                                        )
                                        if (result == SnackbarResult.ActionPerformed) {
                                            productViewModel.restoreProductAfterUndo(snapshot)
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!uiState.isListCompleted && uiState.products.isNotEmpty()) {
                val hasSelection = uiState.products.any { it.isChecked }
                Button(
                    onClick = { productViewModel.completeList(listId) },
                    enabled = hasSelection,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    )
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Finalizar Compra")
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Button(
                onClick = {
                    navController.navigate(AppRoutes.addProductRoute(listId))
                },
                enabled = !uiState.isListCompleted,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Agregar Producto")
            }
        }
    }
}

@Composable
private fun DetailProductsEmptyState() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.ShoppingCart,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Aún no hay productos",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Agrega productos para comenzar",
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ListCompletedBanner(visible: Boolean) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Text(
                "¡Lista completada!",
                modifier = Modifier.padding(12.dp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun ProductCard(
    product: ProductEntity,
    isListCompleted: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {

    val line = if (product.isChecked) TextDecoration.LineThrough else TextDecoration.None

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = ProductCardShape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Checkbox(
                checked = product.isChecked,
                onCheckedChange = onCheckedChange,
                enabled = !isListCompleted
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .then(if (!isListCompleted) Modifier.clickable { onClick() } else Modifier)
            ) {
                Text(
                    product.name,
                    textDecoration = line,
                    fontWeight = FontWeight.SemiBold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        if (product.unit.isBlank()) product.quantity else "${product.quantity} ${product.unit}",
                        textDecoration = line,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (product.price != null) {
                        Text(
                            " • ${product.currency}${String.format("%.2f", product.price)}",
                            textDecoration = line,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (isListCompleted && !product.isChecked) {
                    Text(
                        "Pendiente",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            if (!isListCompleted) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                }
            }
        }
    }
}