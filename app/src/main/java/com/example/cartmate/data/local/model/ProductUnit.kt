package com.example.cartmate.data.local.model

enum class UnitCategory(val displayName: String) {
    MASS("Masa"),
    VOLUME("Volumen"),
    COUNT("Conteo"),
    OTHER("Otro")
}

enum class ProductUnit(val displayName: String, val symbol: String, val category: UnitCategory) {
    // Masa
    KILOGRAM("Kilogramo", "kg", UnitCategory.MASS),
    GRAM("Gramo", "g", UnitCategory.MASS),
    POUND("Libra", "lb", UnitCategory.MASS),
    OUNCE("Onza", "oz", UnitCategory.MASS),
    
    // Volumen
    LITER("Litro", "l", UnitCategory.VOLUME),
    MILLILITER("Mililitro", "ml", UnitCategory.VOLUME),
    GALLON("Galón", "gal", UnitCategory.VOLUME),
    CUP("Taza", "taza", UnitCategory.VOLUME),
    
    // Conteo
    UNIT("Unidad", "un", UnitCategory.COUNT),
    PACK("Paquete", "paquete", UnitCategory.COUNT),
    BOX("Caja", "caja", UnitCategory.COUNT),
    DOZEN("Docena", "docena", UnitCategory.COUNT),
    
    // Otro
    OTHER("Otro", "", UnitCategory.OTHER);

    companion object {
        fun fromSymbol(symbol: String): ProductUnit? {
            return entries.find { it.symbol == symbol && it != OTHER }
        }
    }
}
