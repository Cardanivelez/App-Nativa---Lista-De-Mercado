package com.example.cartmate.ui.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector

/** Valor legado en BD (lista sin categoría explícita). */
const val LIST_CATEGORY_DEFAULT = ""

/** Categoría por defecto explícita (Figma / producto). */
const val LIST_CATEGORY_GENERAL = "GENERAL"

const val LIST_CATEGORY_ALIMENTOS = "ALIMENTOS"
const val LIST_CATEGORY_BELLEZA = "BELLEZA"
const val LIST_CATEGORY_ELECTRONICA = "ELECTRONICA"
const val LIST_CATEGORY_ASEO = "ASEO"
const val LIST_CATEGORY_UTILES = "UTILES"
const val LIST_CATEGORY_HOGAR = "HOGAR"
const val LIST_CATEGORY_HERRAMIENTAS = "HERRAMIENTAS"
const val LIST_CATEGORY_ROPA = "ROPA"
const val LIST_CATEGORY_MASCOTAS = "MASCOTAS"
const val LIST_CATEGORY_SALUD = "SALUD"
const val LIST_CATEGORY_OFICINA = "OFICINA"
const val LIST_CATEGORY_JARDIN = "JARDIN"
const val LIST_CATEGORY_BEBIDAS = "BEBIDAS"
const val LIST_CATEGORY_SNACKS = "SNACKS"

data class ShoppingListCategoryOption(
    val key: String,
    val label: String,
    val imageVector: ImageVector
)

object ShoppingListCategories {

    /** Orden: General (defecto) + resto. */
    val optionsForCreateScreen: List<ShoppingListCategoryOption> = listOf(
        ShoppingListCategoryOption(LIST_CATEGORY_GENERAL, "General", Icons.Default.List),
        ShoppingListCategoryOption(LIST_CATEGORY_ALIMENTOS, "Alimentos", Icons.Default.Restaurant),
        ShoppingListCategoryOption(LIST_CATEGORY_BELLEZA, "Belleza", Icons.Default.Face),
        ShoppingListCategoryOption(LIST_CATEGORY_ELECTRONICA, "Electrónica", Icons.Default.Devices),
        ShoppingListCategoryOption(LIST_CATEGORY_ASEO, "Aseo", Icons.Default.CleaningServices),
        ShoppingListCategoryOption(LIST_CATEGORY_UTILES, "Útiles", Icons.Default.School),
        ShoppingListCategoryOption(LIST_CATEGORY_HOGAR, "Hogar", Icons.Default.Home),
        ShoppingListCategoryOption(LIST_CATEGORY_HERRAMIENTAS, "Herramientas", Icons.Default.Build),
        ShoppingListCategoryOption(LIST_CATEGORY_ROPA, "Ropa", Icons.Default.Checkroom),
        ShoppingListCategoryOption(LIST_CATEGORY_MASCOTAS, "Mascotas", Icons.Default.Pets),
        ShoppingListCategoryOption(LIST_CATEGORY_SALUD, "Salud", Icons.Default.LocalHospital),
        ShoppingListCategoryOption(LIST_CATEGORY_OFICINA, "Oficina", Icons.Default.Work),
        ShoppingListCategoryOption(LIST_CATEGORY_JARDIN, "Jardín", Icons.Default.Grass),
        ShoppingListCategoryOption(LIST_CATEGORY_BEBIDAS, "Bebidas", Icons.Default.LocalDrink),
        ShoppingListCategoryOption(LIST_CATEGORY_SNACKS, "Snacks", Icons.Default.Fastfood)
    )

    /** Icono para tarjeta según valor guardado en `ShoppingListEntity.icon`. */
    fun imageVectorForStored(icon: String?): ImageVector {
        if (icon.isNullOrBlank()) return Icons.Default.List
        return when (icon) {
            LIST_CATEGORY_GENERAL, LIST_CATEGORY_DEFAULT -> Icons.Default.List
            LIST_CATEGORY_ALIMENTOS -> Icons.Default.Restaurant
            LIST_CATEGORY_BELLEZA -> Icons.Default.Face
            LIST_CATEGORY_ELECTRONICA -> Icons.Default.Devices
            LIST_CATEGORY_ASEO -> Icons.Default.CleaningServices
            LIST_CATEGORY_UTILES -> Icons.Default.School
            LIST_CATEGORY_HOGAR -> Icons.Default.Home
            LIST_CATEGORY_HERRAMIENTAS -> Icons.Default.Build
            LIST_CATEGORY_ROPA -> Icons.Default.Checkroom
            LIST_CATEGORY_MASCOTAS -> Icons.Default.Pets
            LIST_CATEGORY_SALUD -> Icons.Default.LocalHospital
            LIST_CATEGORY_OFICINA -> Icons.Default.Work
            LIST_CATEGORY_JARDIN -> Icons.Default.Grass
            LIST_CATEGORY_BEBIDAS -> Icons.Default.LocalDrink
            LIST_CATEGORY_SNACKS -> Icons.Default.Fastfood
            else -> Icons.Default.List
        }
    }
}
