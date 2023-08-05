package com.example.nuberjam.ui.main.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nuberjam.data.Repository
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: Repository) : ViewModel() {
    fun saveLoginState(state: Boolean) {
        viewModelScope.launch {
            repository.saveLoginState(state)
        }
    }

    fun clearAccountState() {
        viewModelScope.launch {
            repository.clearAccountState()
        }
    }

    fun getAccountState() = repository.getAccountState()
}