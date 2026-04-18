package com.example.cartmate.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.animation.*
import androidx.compose.animation.core.*
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

import com.example.cartmate.data.local.entity.ProductEntity
import com.example.cartmate.navigation.AppRoutes
import com.example.cartmate.ui.components.ScreenTopBar
import com.example.cartmate.ui.viewmodel.ProductViewModel
import com.example.cartmate.ui.viewmodel.ViewModelProvider

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

            ListCompletedBanner(uiState.listCompletionCelebration)

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
                            onCheckedChange = {
                                productViewModel.updateCheckedState(product.id, it)
                            },
                            onClick = {
                                navController.navigate("productDetail/${product.id}")
                            },
                            onDelete = {
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
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    navController.navigate(AppRoutes.addProductRoute(listId))
                },
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
                onCheckedChange = onCheckedChange
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onClick() }
            ) {
                Text(
                    product.name,
                    textDecoration = line,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    product.quantity,
                    textDecoration = line
                )
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = null)
            }
        }
    }
}