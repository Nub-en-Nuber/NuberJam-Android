package com.example.nuberjam.utils

object Helper {
    fun isValidEmail(text: String): Boolean {
        return text.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches()
    }
}