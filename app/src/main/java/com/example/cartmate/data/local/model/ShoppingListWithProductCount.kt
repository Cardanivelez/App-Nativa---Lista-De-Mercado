package com.example.cartmate.data.local.model

import androidx.room.ColumnInfo

data class ShoppingListWithProductCount(
    val id: Long,
    val name: String,
    val icon: String?,
    @ColumnInfo(name = "user_id")
    val userId: Long,
    @ColumnInfo(name = "product_count")
    val productCount: Int,
    /**
     * `true` cuando hay al menos un producto y todos están marcados (`is_checked`).
     */
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean
)
