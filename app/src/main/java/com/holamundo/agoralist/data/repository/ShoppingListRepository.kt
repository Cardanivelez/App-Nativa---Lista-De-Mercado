package com.holamundo.agoralist.data.repository

import com.holamundo.agoralist.data.local.dao.ShoppingListDao
import com.holamundo.agoralist.data.local.entity.ShoppingListEntity
import com.holamundo.agoralist.data.local.model.ShoppingListWithProductCount
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
