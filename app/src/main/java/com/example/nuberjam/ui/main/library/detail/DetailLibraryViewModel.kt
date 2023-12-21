package com.example.nuberjam.ui.main.library.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nuberjam.data.Repository
import com.example.nuberjam.data.Result
import com.example.nuberjam.data.model.Music
import com.example.nuberjam.ui.customview.CustomSnackbar
import com.example.nuberjam.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailLibraryViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _favoriteState = MutableStateFlow<Result<List<Music>>?>(null)
    val favoriteState = _favoriteState.asStateFlow()

    private val _snackbarState = MutableLiveData<Event<CustomSnackbar.SnackbarState>>()
    val snackbarState: LiveData<Event<CustomSnackbar.SnackbarState>> = _snackbarState

    init {
        getFavoriteData()
    }

    fun setSnackbar(message: String, state: Int, length: Int = CustomSnackbar.LENGTH_LONG) {
        _snackbarState.value = Event(CustomSnackbar.SnackbarState(message, state, length))
    }

    fun getFavoriteData() {
        viewModelScope.launch {
            repository.readAllFavorite().collect {
                _favoriteState.value = it
            }
        }
    }
}