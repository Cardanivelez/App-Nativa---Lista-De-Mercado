package com.example.cartmate.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cartmate.ui.components.BottomNavigationBar

private val PrimaryGreen = Color(0xFF16A34A)
private val CardShape = RoundedCornerShape(16.dp)

private data class ShoppingListItem(
    val title: String,
    val productCount: Int
)

@Composable
fun HomeScreen(navController: NavController) {
    val lists = listOf(
        ShoppingListItem(title = "Compras del mes", productCount = 12),
        ShoppingListItem(title = "Lista semanal", productCount = 8),
        ShoppingListItem(title = "Supermercado", productCount = 15),
        ShoppingListItem(title = "Farmacia", productCount = 5)
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BottomNavigationBar(navController = navController, currentRoute = "home")
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

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(lists) { item ->
                    ShoppingListCard(
                        item = item,
                        onClick = { navController.navigate("detail") }
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeHeader() {
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

@Composable
private fun ShoppingListCard(
    item: ShoppingListItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = CardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${item.productCount} productos",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = ">",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = PrimaryGreen,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

