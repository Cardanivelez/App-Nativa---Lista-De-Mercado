package com.holamundo.agoralist.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.holamundo.agoralist.data.local.dao.ProductDao
import com.holamundo.agoralist.data.local.dao.ProductHistoryDao
import com.holamundo.agoralist.data.local.dao.ShoppingListDao
import com.holamundo.agoralist.data.local.dao.UserDao
import com.holamundo.agoralist.data.local.entity.ProductEntity
import com.holamundo.agoralist.data.local.entity.ProductHistoryEntity
import com.holamundo.agoralist.data.local.entity.ShoppingListEntity
import com.holamundo.agoralist.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class, ShoppingListEntity::class, ProductEntity::class, ProductHistoryEntity::class],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun shoppingListDao(): ShoppingListDao
    abstract fun productDao(): ProductDao
    abstract fun productHistoryDao(): ProductHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cartmate_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
