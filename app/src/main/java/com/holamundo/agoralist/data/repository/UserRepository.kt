package com.holamundo.agoralist.data.repository

import android.content.Context
import android.net.Uri
import com.holamundo.agoralist.data.local.dao.UserDao
import com.holamundo.agoralist.data.local.entity.UserEntity
import com.holamundo.agoralist.data.session.UserSession
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

class UserRepository(
    private val userDao: UserDao,
    private val appContext: Context
) {
    suspend fun getUserById(id: Long): UserEntity? = userDao.getUserById(id)

    suspend fun getUserByEmail(email: String): UserEntity? = userDao.getUserByEmail(email)

    suspend fun insertUser(user: UserEntity): Long = userDao.insertUser(user)

    suspend fun updateUser(user: UserEntity) = userDao.updateUser(user)

    suspend fun deleteUser(user: UserEntity) = userDao.deleteUser(user)

    val currentUser: StateFlow<UserEntity?> = UserSession.currentUser

    suspend fun registerUser(
        name: String,
        email: String,
        password: String
    ): Result<Long> {
        val normalizedEmail = email.trim().lowercase()
        if (normalizedEmail.isBlank() || password.isBlank() || name.trim().isBlank()) {
            return Result.failure(IllegalArgumentException("Todos los campos son obligatorios"))
        }
        val existingUser = userDao.getUserByEmail(normalizedEmail)
        if (existingUser != null) {
            return Result.failure(IllegalStateException("El correo ya está registrado"))
        }
        val id = userDao.insertUser(
            UserEntity(
                name = name.trim(),
                email = normalizedEmail,
                password = password
            )
        )
        return Result.success(id)
    }

    suspend fun login(email: String, password: String): Result<UserEntity> {
        val normalizedEmail = email.trim().lowercase()
        val user = userDao.getUserByEmail(normalizedEmail)
            ?: return Result.failure(IllegalArgumentException("Correo o contraseña incorrectos"))
        if (user.password != password) {
            return Result.failure(IllegalArgumentException("Correo o contraseña incorrectos"))
        }
        UserSession.setCurrentUser(user)
        return Result.success(user)
    }

    suspend fun updateProfile(
        userId: Long,
        name: String,
        email: String
    ): Result<UserEntity> {
        val normalizedName = name.trim()
        val normalizedEmail = email.trim().lowercase()
        if (normalizedName.isBlank() || normalizedEmail.isBlank()) {
            return Result.failure(IllegalArgumentException("Nombre y correo son obligatorios"))
        }

        val currentUser = userDao.getUserById(userId)
            ?: return Result.failure(IllegalStateException("Usuario no encontrado"))

        val existingUser = userDao.getUserByEmail(normalizedEmail)
        if (existingUser != null && existingUser.id != userId) {
            return Result.failure(IllegalStateException("El correo ya está registrado"))
        }

        val updatedUser = currentUser.copy(
            name = normalizedName,
            email = normalizedEmail
        )
        userDao.updateUser(updatedUser)
        UserSession.setCurrentUser(updatedUser)
        return Result.success(updatedUser)
    }

    /**
     * Persiste la foto de perfil: copia el contenido a almacenamiento interno y guarda la ruta en Room
     * para que sobreviva a cierre de sesión, navegación y reinicio de la app.
     * [sourceUriString] null o vacío borra la imagen guardada.
     */
    suspend fun updateProfileImageFromPicker(
        userId: Long,
        sourceUriString: String?
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val user = userDao.getUserById(userId)
            ?: return@withContext Result.failure(IllegalStateException("Usuario no encontrado"))

        if (sourceUriString.isNullOrBlank()) {
            deleteStoredProfileImageFile(user.profileImageUri)
            val cleared = user.copy(profileImageUri = null)
            userDao.updateUser(cleared)
            if (UserSession.currentUser.value?.id == userId) {
                UserSession.setCurrentUser(cleared)
            }
            return@withContext Result.success(Unit)
        }

        runCatching {
            val sourceUri = Uri.parse(sourceUriString)
            val dest = File(appContext.filesDir, "profile_images").apply { mkdirs() }
                .resolve("user_${userId}.jpg")
            appContext.contentResolver.openInputStream(sourceUri)?.use { input ->
                dest.outputStream().use { output -> input.copyTo(output) }
            } ?: error("No se pudo leer la imagen")

            val oldPath = user.profileImageUri
            if (oldPath != null && oldPath != dest.absolutePath) {
                deleteStoredProfileImageFile(oldPath)
            }

            val updated = user.copy(profileImageUri = dest.absolutePath)
            userDao.updateUser(updated)
            if (UserSession.currentUser.value?.id == userId) {
                UserSession.setCurrentUser(updated)
            }
        }.fold(
            onSuccess = { Result.success(Unit) },
            onFailure = { Result.failure(it) }
        )
    }

    private fun deleteStoredProfileImageFile(path: String?) {
        if (path.isNullOrBlank()) return
        runCatching {
            val f = File(path)
            val expectedDir = File(appContext.filesDir, "profile_images").canonicalFile
            if (f.exists() && f.parentFile?.canonicalFile == expectedDir) {
                f.delete()
            }
        }
    }

    fun logout() {
        UserSession.clear()
    }

    suspend fun changePassword(
        userId: Long,
        newPassword: String
    ): Result<Unit> {
        val normalizedPassword = newPassword.trim()
        if (normalizedPassword.isBlank()) {
            return Result.failure(IllegalArgumentException("La contraseña no puede estar vacía"))
        }
        val currentUser = userDao.getUserById(userId)
            ?: return Result.failure(IllegalStateException("Usuario no encontrado"))
        val updatedUser = currentUser.copy(password = normalizedPassword)
        userDao.updateUser(updatedUser)
        UserSession.setCurrentUser(updatedUser)
        return Result.success(Unit)
    }
}
