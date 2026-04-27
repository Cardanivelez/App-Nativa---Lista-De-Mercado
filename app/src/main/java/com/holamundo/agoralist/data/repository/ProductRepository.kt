package com.holamundo.agoralist.data.repository

import com.holamundo.agoralist.data.local.dao.ProductDao
import com.holamundo.agoralist.data.local.dao.ProductHistoryDao
import com.holamundo.agoralist.data.local.entity.ProductEntity
import com.holamundo.agoralist.data.local.entity.ProductHistoryEntity
import kotlinx.coroutines.flow.Flow

class ProductRepository(
    private val productDao: ProductDao,
    private val productHistoryDao: ProductHistoryDao
) {
    fun getProductsByListId(listId: Long): Flow<List<ProductEntity>> =
        productDao.getProductsByListId(listId)

    suspend fun getProductsByListIdOnce(listId: Long): List<ProductEntity> =
        productDao.getProductsByListIdOnce(listId)

    suspend fun getProductById(productId: Long): ProductEntity? =
        productDao.getProductById(productId)

    fun getProductFlowById(productId: Long): Flow<ProductEntity?> =
        productDao.getProductFlowById(productId)

    suspend fun insertProduct(product: ProductEntity): Long =
        productDao.insertProduct(product)

    suspend fun updateProduct(product: ProductEntity) =
        productDao.updateProduct(product)

    suspend fun deleteProduct(product: ProductEntity) =
        productDao.deleteProduct(product)

    suspend fun updateCheckedState(productId: Long, checked: Boolean) =
        productDao.updateCheckedState(productId, checked)

    suspend fun uncheckAllProducts(listId: Long) =
        productDao.uncheckAllProducts(listId)

    // History methods
    suspend fun insertHistory(history: ProductHistoryEntity) =
        productHistoryDao.insertHistory(history)

    fun getUniqueHistoricalProducts(userId: Long) =
        productHistoryDao.getUniqueHistoricalProducts(userId)

    fun getHistoryForProduct(userId: Long, name: String, category: String, unitName: String) =
        productHistoryDao.getHistoryForProduct(userId, name, category, unitName)
}
