package com.example.nuberjam.ui.main.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nuberjam.data.Repository
import com.example.nuberjam.data.Result
import com.example.nuberjam.data.model.Account
import com.example.nuberjam.ui.main.profile.editname.EditNameDialogFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateAccountViewModel @Inject constructor(
    private val repository: Repository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var name = savedStateHandle.get<String>(EditNameDialogFragment.ARG_NAME_KEY) ?: ""

    private val _updateAccountState = MutableStateFlow<Result<Boolean>?>(null)
    val updateAccountState = _updateAccountState.asStateFlow()

    fun updateAccount(account: Account) {
        viewModelScope.launch {
            repository.updateAccount(account).collect { result ->
                _updateAccountState.value = result
            }
        }
    }
}