package com.example.nuberjam.utils

object FormValidation {
    const val KEY_MINIMAL_CHARACTER = "minimal_character"
    const val KEY_CONTAIN_NUMBER = "contain_number"
    const val KEY_CONTAIN_LOWER = "contain_lower"
    const val KEY_CONTAIN_UPPER = "contain_upper"

    fun isUsernameValid(text: String): Boolean {
        return text.isNotEmpty() && !text.contains(" ")
    }

    fun isEmailValid(text: String): Boolean {
        return text.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches()
    }

    fun isPasswordSame(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

    fun isPasswordValid(password: String): Map<String, Boolean> {
        return mapOf(
            KEY_MINIMAL_CHARACTER to (password.length >= 8),
            KEY_CONTAIN_NUMBER to password.contains("\\d".toRegex()),
            KEY_CONTAIN_LOWER to password.contains("[a-z]".toRegex()),
            KEY_CONTAIN_UPPER to password.contains("[A-Z]".toRegex())
        )
    }
}