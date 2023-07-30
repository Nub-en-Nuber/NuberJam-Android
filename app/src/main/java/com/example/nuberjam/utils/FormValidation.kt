package com.example.nuberjam.utils

object FormValidation {
    fun isUsernameValid(text: String): Boolean {
        return text.isNotEmpty() && !text.contains(" ")
    }

    fun isEmailValid(text: String): Boolean {
        return text.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches()
    }
}