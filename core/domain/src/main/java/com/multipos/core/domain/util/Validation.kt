package com.multipos.core.domain.util

object Validation {
    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return emailRegex.matches(email)
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 8 &&
               password.any { it.isUpperCase() } &&
               password.any { it.isLowerCase() } &&
               password.any { it.isDigit() }
    }

    fun isValidPhone(phone: String): Boolean {
        val phoneRegex = "^[+]?[0-9]{10,15}$".toRegex()
        return phoneRegex.matches(phone.replace("\\s".toRegex(), ""))
    }
}