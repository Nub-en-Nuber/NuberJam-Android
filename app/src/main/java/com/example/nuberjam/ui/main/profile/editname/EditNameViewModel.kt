package com.example.nuberjam.ui.main.profile.editname

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.nuberjam.data.Repository
import com.example.nuberjam.data.Result
import com.example.nuberjam.data.source.remote.request.AccountRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class EditNameViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    private val _editNameState = MutableStateFlow<Result<Boolean>?>(null)
    val editNameState = _editNameState.asStateFlow()

    fun updateAccount(request: AccountRequest) {
        viewModelScope.launch {
            repository.updateAccount(request).collect { result ->
                _editNameState.value = result
            }
        }
    }

    fun saveAccountState(name: String) {
        runBlocking {
            val account = repository.getAccountState().asFlow().first()
            repository.saveAccountState(account.copy(name = name))
        }
    }
}