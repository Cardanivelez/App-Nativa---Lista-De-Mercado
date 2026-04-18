package com.example.cartmate.data.repository

import com.example.cartmate.data.local.dao.ProductDao
import com.example.cartmate.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

class ProductRepository(
    private val productDao: ProductDao
) {
    fun getProductsByListId(listId: Long): Flow<List<ProductEntity>> =
        productDao.getProductsByListId(listId)

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
}
