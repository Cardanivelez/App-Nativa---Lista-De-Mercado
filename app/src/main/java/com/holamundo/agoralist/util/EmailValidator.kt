package com.holamundo.agoralist.util

/**
 * Formato `local@dominio.tld` según
 * `^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$`.
 */
object EmailValidator {
    private val pattern = Regex("""^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$""")

    fun isValid(email: String): Boolean = pattern.matches(email.trim())
}
