package com.holamundo.agoralist.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.holamundo.agoralist.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE list_id = :listId ORDER BY id ASC")
    fun getProductsByListId(listId: Long): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE list_id = :listId")
    suspend fun getProductsByListIdOnce(listId: Long): List<ProductEntity>

    @Query("SELECT * FROM products WHERE id = :productId LIMIT 1")
    suspend fun getProductById(productId: Long): ProductEntity?

    @Query("SELECT * FROM products WHERE id = :productId LIMIT 1")
    fun getProductFlowById(productId: Long): Flow<ProductEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query("UPDATE products SET is_checked = :checked WHERE id = :productId")
    suspend fun updateCheckedState(productId: Long, checked: Boolean)

    @Query("UPDATE products SET is_checked = 0 WHERE list_id = :listId")
    suspend fun uncheckAllProducts(listId: Long)
}
