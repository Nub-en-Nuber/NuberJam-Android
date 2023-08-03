package com.example.nuberjam.ui.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nuberjam.data.Repository
import com.example.nuberjam.data.model.Account
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: Repository) : ViewModel() {
    var formName = ""
    var formNameValid = false

    var formUsername = ""
    var formUsernameValid = false

    var formEmail = ""
    var formEmailValid = false

    var formPassword = ""
    var formPasswordValid = false

    var formConfirmPassword = ""
    var formConfirmPasswordValid = false


    fun makeLogin(usernameOrEmail: String, password: String) =
        repository.makeLogin(usernameOrEmail, password)

    fun getAccountData(usernameOrEmail: String) = repository.getAccountData(usernameOrEmail)

    fun saveLoginState(state: Boolean) {
        viewModelScope.launch {
            repository.saveLoginState(state)
        }
    }

    fun saveAccountState(account: Account) {
        viewModelScope.launch {
            repository.saveAccountState(account)
        }
    }

    fun makeRegister(account: Account) = repository.addAccount(account)

    fun checkUsernameExist(username: String) = repository.checkUsernameExist(username)

    fun checkEmailExist(email: String) = repository.checkEmailExist(email)

    fun checkFormRegisterValid(): Boolean =
        formNameValid && formUsernameValid && formEmailValid && formPasswordValid && formConfirmPasswordValid
}