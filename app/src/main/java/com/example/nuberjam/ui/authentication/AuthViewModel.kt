package com.example.nuberjam.ui.authentication

import androidx.lifecycle.ViewModel
import com.example.nuberjam.data.Repository

class AuthViewModel(private val repository: Repository) : ViewModel() {
    fun makeLogin(usernameOrEmail: String, password: String) =
        repository.makeLogin(usernameOrEmail, password)
}