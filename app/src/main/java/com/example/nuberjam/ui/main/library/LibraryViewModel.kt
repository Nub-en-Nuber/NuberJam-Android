package com.example.nuberjam.ui.main.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nuberjam.data.Repository
import com.example.nuberjam.ui.customview.CustomSnackbar
import com.example.nuberjam.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _snackbarState = MutableLiveData<Event<CustomSnackbar.SnackbarState>>()
    val snackbarState: LiveData<Event<CustomSnackbar.SnackbarState>> = _snackbarState

    fun setSnackbar(message: String, state: Int, length: Int = CustomSnackbar.LENGTH_LONG) {
        _snackbarState.value = Event(CustomSnackbar.SnackbarState(message, state, length))
    }

    fun getAccountState() = repository.getAccountState()

    fun readAllPlaylist() = repository.readAllPlaylist()

    fun addPlaylist(playlistName: String, accountId: Int) =
        repository.addPlaylist(playlistName, accountId)
}