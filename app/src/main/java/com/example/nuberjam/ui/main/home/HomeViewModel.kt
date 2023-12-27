package com.example.nuberjam.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nuberjam.data.Repository
import com.example.nuberjam.data.Result
import com.example.nuberjam.ui.customview.CustomSnackbar
import com.example.nuberjam.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _snackbarState = MutableLiveData<Event<CustomSnackbar.SnackbarState>>()
    val snackbarState: LiveData<Event<CustomSnackbar.SnackbarState>> = _snackbarState

    private val _addDeleteFavoriteState = MutableStateFlow<Result<Boolean>?>(null)
    val addDeleteFavoriteState = _addDeleteFavoriteState.asStateFlow()

    init {
        _addDeleteFavoriteState.value = null
    }

    fun setSnackbar(message: String, state: Int, length: Int = CustomSnackbar.LENGTH_LONG) {
        _snackbarState.value = Event(CustomSnackbar.SnackbarState(message, state, length))
    }

    fun getAccountState() = repository.getAccountState()

    fun readAllMusic(accountId: Int) = repository.readAllMusic(accountId)

    fun readAllAlbum() = repository.readAllAlbum()

    fun updateFavoriteData(musicId: Int, isInsert: Boolean) {
        viewModelScope.launch {
            repository.addDeleteFavorite(musicId, isInsert).collect {
                _addDeleteFavoriteState.value = it
            }
        }
    }
}