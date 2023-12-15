package com.example.nuberjam.utils

import android.content.Context
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.example.nuberjam.R

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

    fun isPlaylistNameValid(text: String): Boolean {
        return text.length <= 28
    }

    fun checkPasswordRequirements(
        context: Context,
        password: String,
        minPassword: ImageView,
        containNumber: ImageView,
        containUpper: ImageView,
        containLower: ImageView,
    ): Boolean {
        var isPasswordValid = true

        val passwordRequirements = isPasswordValid(password)
        if (passwordRequirements[KEY_MINIMAL_CHARACTER] == true) {
            minPassword.setImageDrawable(
                ContextCompat.getDrawable(
                    context, R.drawable.ic_checklist_green
                )
            )
        } else {
            isPasswordValid = false
            minPassword.setImageDrawable(
                ContextCompat.getDrawable(
                    context, R.drawable.ic_clause_red
                )
            )
        }

        if (passwordRequirements[KEY_CONTAIN_NUMBER] == true) {
            containNumber.setImageDrawable(
                ContextCompat.getDrawable(
                    context, R.drawable.ic_checklist_green
                )
            )
        } else {
            isPasswordValid = false
            containNumber.setImageDrawable(
                ContextCompat.getDrawable(
                    context, R.drawable.ic_clause_red
                )
            )
        }

        if (passwordRequirements[KEY_CONTAIN_UPPER] == true) {
            containUpper.setImageDrawable(
                ContextCompat.getDrawable(
                    context, R.drawable.ic_checklist_green
                )
            )
        } else {
            isPasswordValid = false
            containUpper.setImageDrawable(
                ContextCompat.getDrawable(
                    context, R.drawable.ic_clause_red
                )
            )
        }

        if (passwordRequirements[KEY_CONTAIN_LOWER] == true) {
            containLower.setImageDrawable(
                ContextCompat.getDrawable(
                    context, R.drawable.ic_checklist_green
                )
            )
        } else {
            isPasswordValid = false
            containLower.setImageDrawable(
                ContextCompat.getDrawable(
                    context, R.drawable.ic_clause_red
                )
            )
        }

        return isPasswordValid
    }
}