package com.holamundo.agoralist.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.holamundo.agoralist.data.local.dao.HistoricalProduct
import com.holamundo.agoralist.data.local.entity.ProductHistoryEntity
import com.holamundo.agoralist.data.local.model.UnitCategory
import com.holamundo.agoralist.ui.components.BottomNavigationBar
import com.holamundo.agoralist.ui.components.ScreenTopBar
import com.holamundo.agoralist.ui.viewmodel.FinanzasViewModel
import com.holamundo.agoralist.ui.viewmodel.ViewModelProvider
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FinanzasScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: FinanzasViewModel = viewModel(
        factory = ViewModelProvider.provideFinanzasViewModelFactory(context)
    )
    val uiState by viewModel.uiState.collectAsState()

    BackHandler(enabled = uiState.selectedProductName != null) {
        viewModel.deselectProduct()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            ScreenTopBar(
                title = if (uiState.selectedProductName != null) uiState.selectedProductName!! else "Finanzas",
                onBackClick = {
                    if (uiState.selectedProductName != null) {
                        viewModel.deselectProduct()
                    } else {
                        navController.popBackStack()
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController, currentRoute = "finanzas")
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (uiState.selectedProductName == null) {
                HistoryProductList(uiState.historicalProducts) {
                    viewModel.selectProduct(it)
                }
            } else {
                ProductHistoryDetail(
                    history = uiState.selectedProductHistory,
                    category = uiState.selectedCategory ?: "",
                    unitName = uiState.selectedUnitName ?: ""
                )
            }
        }
    }
}

@Composable
fun HistoryProductList(products: List<HistoricalProduct>, onSelect: (HistoricalProduct) -> Unit) {
    if (products.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay datos históricos disponibles.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products) { product ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onSelect(product) },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(product.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            val catLabel = when(product.category) {
                                UnitCategory.MASS.name -> "Masa"
                                UnitCategory.VOLUME.name -> "Volumen"
                                UnitCategory.COUNT.name -> "Conteo"
                                else -> "Otro (${product.unit_name})"
                            }
                            Text(catLabel, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductHistoryDetail(history: List<ProductHistoryEntity>, category: String, unitName: String) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        val displayUnit = when (category) {
            UnitCategory.MASS.name -> "kg"
            UnitCategory.VOLUME.name -> "L"
            UnitCategory.COUNT.name -> "unidad"
            else -> unitName
        }
        
        val multiplier = when (category) {
            UnitCategory.MASS.name -> 1000.0
            UnitCategory.VOLUME.name -> 1000.0
            else -> 1.0
        }

        Text(
            "Historial de precio por $displayUnit",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        if (history.size < 2) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                Text("Se necesitan más datos para generar un gráfico.", textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.padding(16.dp))
            }
        } else {
            PriceChart(history, multiplier)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Registros", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(history.reversed()) { entry ->
                Row(
                    modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp)).padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(entry.timestamp))
                    Text(date)
                    Text("${entry.currency}${String.format("%.2f", entry.pricePerBaseUnit * multiplier)}", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PriceChart(history: List<ProductHistoryEntity>, multiplier: Double) {
    val prices = history.map { it.pricePerBaseUnit * multiplier }
    val maxPrice = prices.maxOrNull() ?: 1.0
    val minPrice = prices.minOrNull() ?: 0.0
    val range = if (maxPrice == minPrice) 1.0 else maxPrice - minPrice

    val primaryColor = MaterialTheme.colorScheme.primary

    Canvas(modifier = Modifier.fillMaxWidth().height(200.dp)) {
        val width = size.width
        val height = size.height
        val spacing = width / (prices.size - 1)

        val path = Path()
        prices.forEachIndexed { index, price ->
            val x = index * spacing
            val normalizedPrice = (price - minPrice) / range
            val y = height - (normalizedPrice * height).toFloat()
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
            
            drawCircle(color = primaryColor, radius = 4.dp.toPx(), center = Offset(x, y))
        }

        drawPath(path = path, color = primaryColor, style = Stroke(width = 3.dp.toPx()))
    }
}
