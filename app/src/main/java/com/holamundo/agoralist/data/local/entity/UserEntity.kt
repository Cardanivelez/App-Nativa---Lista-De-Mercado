package com.holamundo.agoralist.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "email")
    val email: String,
    @ColumnInfo(name = "password")
    val password: String,
    /** Ruta local absoluta (`File.absolutePath`) o URI legible por Coil tras elegir imagen. */
    @ColumnInfo(name = "profile_image_uri")
    val profileImageUri: String? = null
)
