package com.holamundo.agoralist.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.holamundo.agoralist.data.local.entity.ShoppingListEntity
import com.holamundo.agoralist.data.local.model.ShoppingListWithProductCount
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListDao {
    @Query(
        """
        SELECT sl.id, sl.name, sl.icon, sl.user_id,
               COUNT(p.id) AS product_count,
               CASE
                   WHEN COUNT(p.id) = 0 THEN 0
                   WHEN COUNT(p.id) = SUM(CASE WHEN p.is_checked != 0 THEN 1 ELSE 0 END) THEN 1
                   ELSE 0
               END AS is_completed
        FROM shopping_lists sl
        LEFT JOIN products p ON p.list_id = sl.id
        WHERE sl.user_id = :userId
        GROUP BY sl.id, sl.name, sl.icon, sl.user_id
        ORDER BY sl.id ASC
        """
    )
    fun getListsWithProductCountByUserId(userId: Long): Flow<List<ShoppingListWithProductCount>>

    @Query("SELECT * FROM shopping_lists WHERE id = :listId LIMIT 1")
    suspend fun getListById(listId: Long): ShoppingListEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(shoppingList: ShoppingListEntity): Long

    @Update
    suspend fun updateList(shoppingList: ShoppingListEntity)

    @Delete
    suspend fun deleteList(shoppingList: ShoppingListEntity)
}
