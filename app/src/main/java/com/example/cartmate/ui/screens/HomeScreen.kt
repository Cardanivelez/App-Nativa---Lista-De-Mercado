package com.example.cartmate.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cartmate.R
import com.example.cartmate.data.local.model.ShoppingListWithProductCount
import com.example.cartmate.data.session.UserSession
import com.example.cartmate.navigation.AppRoutes
import com.example.cartmate.ui.components.BottomNavigationBar
import com.example.cartmate.ui.model.ShoppingListCategories
import com.example.cartmate.ui.viewmodel.HomeViewModel
import com.example.cartmate.ui.viewmodel.ViewModelProvider

private val CardShape = RoundedCornerShape(16.dp)
private val CompletedListCardLightTint = Color(0xFFE8F5E9)

@Composable
fun HomeScreen(navController: NavController) {

    val context = LocalContext.current
    val homeViewModel: HomeViewModel = viewModel(
        factory = ViewModelProvider.provideHomeViewModelFactory(context)
    )

    val uiState by homeViewModel.uiState.collectAsState()
    val currentUser by UserSession.currentUser.collectAsState()

    LaunchedEffect(currentUser?.id) {
        homeViewModel.observeLists(currentUser?.id)
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        val message = uiState.snackbarMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        homeViewModel.consumeSnackbarMessage()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            BottomNavigationBar(navController = navController, currentRoute = "home")
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(AppRoutes.CREATE_LIST) },
                shape = RoundedCornerShape(16.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Crear lista")
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {

            HomeHeader()

            Spacer(modifier = Modifier.height(20.dp))

            if (uiState.lists.isEmpty()) {
                HomeEmptyState(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.lists, key = { it.id }) { item ->
                        ShoppingListCard(
                            item = item,
                            onClick = { navController.navigate(AppRoutes.detailRoute(item.id)) },
                            onDeleteClick = { homeViewModel.requestDeleteList(item.id) }
                        )
                    }
                }
            }
        }
    }

    uiState.listIdPendingDelete?.let {
        AlertDialog(
            onDismissRequest = { homeViewModel.dismissDeleteListDialog() },
            title = { Text("Eliminar lista") },
            text = {
                Text("¿Seguro que deseas eliminar esta lista? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                TextButton(onClick = { homeViewModel.confirmDeleteList() }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { homeViewModel.dismissDeleteListDialog() }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun HomeEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Icon(
            imageVector = Icons.Default.List,
            contentDescription = "Sin listas",
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "No tienes listas aún",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Crea tu primera lista para empezar a organizar tus compras",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun HomeHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Mis Listas",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Gestiona tus listas de compras",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Image(
            painter = painterResource(id = R.drawable.app_icon),
            contentDescription = "Logo AgoraList",
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(12.dp))
        )
    }
}

@Composable
private fun ShoppingListCard(
    item: ShoppingListWithProductCount,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {

    val isDarkTheme = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    val backgroundColor = when {
        !item.isCompleted -> MaterialTheme.colorScheme.surfaceVariant
        isDarkTheme -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        else -> CompletedListCardLightTint
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = ShoppingListCategories.imageVectorForStored(item.icon),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Text(
                            text = item.name,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleMedium
                        )

                        if (item.isCompleted) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "¡Completado!",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${item.productCount} productos",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar lista"
                )
            }
        }
    }
}