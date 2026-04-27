package com.holamundo.agoralist.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(
            entity = ShoppingListEntity::class,
            parentColumns = ["id"],
            childColumns = ["list_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["list_id"])]
)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "quantity")
    val quantity: String,
    @ColumnInfo(name = "unit")
    val unit: String = "",
    @ColumnInfo(name = "price")
    val price: Double? = null,
    @ColumnInfo(name = "currency")
    val currency: String = "$",
    @ColumnInfo(name = "notes")
    val notes: String = "",
    @ColumnInfo(name = "is_checked")
    val isChecked: Boolean = false,
    @ColumnInfo(name = "list_id")
    val listId: Long
)
