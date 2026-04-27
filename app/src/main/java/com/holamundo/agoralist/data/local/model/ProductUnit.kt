package com.holamundo.agoralist.data.local.model

enum class UnitCategory(val displayName: String) {
    MASS("Masa"),
    VOLUME("Volumen"),
    COUNT("Conteo"),
    OTHER("Otro")
}

enum class ProductUnit(
    val displayName: String,
    val symbol: String,
    val category: UnitCategory,
    val factor: Double // Factor de conversión a unidad base (g, ml o unidad)
) {
    // Masa (Base: Gramo)
    KILOGRAM("Kilogramo", "kg", UnitCategory.MASS, 1000.0),
    GRAM("Gramo", "g", UnitCategory.MASS, 1.0),
    POUND("Libra", "lb", UnitCategory.MASS, 453.59),
    OUNCE("Onza", "oz", UnitCategory.MASS, 28.35),
    
    // Volumen (Base: Mililitro)
    LITER("Litro", "l", UnitCategory.VOLUME, 1000.0),
    MILLILITER("Mililitro", "ml", UnitCategory.VOLUME, 1.0),
    GALLON("Galón", "gal", UnitCategory.VOLUME, 3785.41),
    CUP("Taza", "taza", UnitCategory.VOLUME, 240.0),
    
    // Conteo (Base: Unidad)
    UNIT("Unidad", "un", UnitCategory.COUNT, 1.0),
    HALF_DOZEN("Media Docena", "6un", UnitCategory.COUNT, 6.0),
    DOZEN("Docena", "docena", UnitCategory.COUNT, 12.0),
    
    // Otro
    OTHER("Otro", "", UnitCategory.OTHER, 1.0);

    companion object {
        fun fromSymbol(symbol: String): ProductUnit? {
            return entries.find { it.symbol == symbol && it != OTHER }
        }
    }
}
