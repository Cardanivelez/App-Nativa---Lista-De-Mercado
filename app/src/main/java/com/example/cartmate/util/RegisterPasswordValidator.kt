package com.example.cartmate.util

/**
 * Reglas de contraseña para registro: mínimo 4 caracteres, al menos una letra y un número.
 */
object RegisterPasswordValidator {
    fun isValid(password: String): Boolean {
        if (password.length < 4) return false
        if (!password.any { it.isLetter() }) return false
        if (!password.any { it.isDigit() }) return false
        return true
    }
}
