package com.holamundo.agoralist.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_history")
data class ProductHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "category")
    val category: String,
    @ColumnInfo(name = "unit_name")
    val unitName: String,
    @ColumnInfo(name = "price_per_base_unit")
    val pricePerBaseUnit: Double,
    @ColumnInfo(name = "currency")
    val currency: String,
    @ColumnInfo(name = "timestamp")
    val timestamp: Long,
    @ColumnInfo(name = "user_id")
    val userId: Long
)
