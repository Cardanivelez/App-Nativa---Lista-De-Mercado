package com.example.cartmate.data.repository

import com.example.cartmate.data.local.dao.ShoppingListDao
import com.example.cartmate.data.local.entity.ShoppingListEntity
import com.example.cartmate.data.local.model.ShoppingListWithProductCount
import kotlinx.coroutines.flow.Flow

class ShoppingListRepository(
    private val shoppingListDao: ShoppingListDao
) {
    fun getListsWithProductCountByUserId(userId: Long): Flow<List<ShoppingListWithProductCount>> =
        shoppingListDao.getListsWithProductCountByUserId(userId)

    suspend fun getListById(listId: Long): ShoppingListEntity? =
        shoppingListDao.getListById(listId)

    suspend fun insertList(shoppingList: ShoppingListEntity): Long =
        shoppingListDao.insertList(shoppingList)

    suspend fun updateList(shoppingList: ShoppingListEntity) =
        shoppingListDao.updateList(shoppingList)

    suspend fun deleteList(shoppingList: ShoppingListEntity) =
        shoppingListDao.deleteList(shoppingList)
}
