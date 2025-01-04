package com.anynetwork.app.utils

fun String.isValidEmail(): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()
    return this.matches(emailRegex)
}

fun String.isValidPhone(): Boolean {
    val phoneRegex = "^\\+?[0-9]{1,3}?[-.\\s]?\\(?[0-9]{1,4}?\\)?[-.\\s]?[0-9]{1,4}[-.\\s]?[0-9]{1,9}\$".toRegex()
    return this.matches(phoneRegex)
}