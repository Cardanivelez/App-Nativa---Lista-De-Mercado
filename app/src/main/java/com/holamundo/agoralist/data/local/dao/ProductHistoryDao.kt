package com.holamundo.agoralist.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.holamundo.agoralist.data.local.entity.ProductHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductHistoryDao {
    @Insert
    suspend fun insertHistory(history: ProductHistoryEntity)

    @Query("SELECT * FROM product_history WHERE user_id = :userId ORDER BY timestamp ASC")
    fun getAllHistoryByUserId(userId: Long): Flow<List<ProductHistoryEntity>>

    @Query("SELECT * FROM product_history WHERE user_id = :userId AND LOWER(name) = LOWER(:name) AND category = :category AND LOWER(unit_name) = LOWER(:unitName) ORDER BY timestamp ASC")
    fun getHistoryForProduct(userId: Long, name: String, category: String, unitName: String): Flow<List<ProductHistoryEntity>>

    @Query("SELECT DISTINCT name, category, unit_name FROM product_history WHERE user_id = :userId")
    fun getUniqueHistoricalProducts(userId: Long): Flow<List<HistoricalProduct>>
}

data class HistoricalProduct(
    val name: String,
    val category: String,
    val unit_name: String
)
