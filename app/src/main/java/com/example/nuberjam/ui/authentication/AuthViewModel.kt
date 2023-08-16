package com.example.nuberjam.ui.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nuberjam.data.Repository
import com.example.nuberjam.data.model.Account
import com.example.nuberjam.ui.customview.CustomSnackbar
import com.example.nuberjam.utils.Event
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

    private val _snackbarState = MutableLiveData<Event<CustomSnackbar.SnackbarState>>()
    val snackbarState: LiveData<Event<CustomSnackbar.SnackbarState>> = _snackbarState

    fun setSnackbar(message: String, state: Int, length: Int = CustomSnackbar.LENGTH_LONG) {
        _snackbarState.value = Event(CustomSnackbar.SnackbarState(message, state, length))
    }

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