package com.example.nuberjam.utils

object FormValidation {
    fun isUsernameValid(text: String): Boolean {
        return text.isNotEmpty() && !text.contains(" ")
    }
}