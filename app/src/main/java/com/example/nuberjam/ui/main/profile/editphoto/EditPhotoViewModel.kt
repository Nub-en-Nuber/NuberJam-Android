package com.example.nuberjam.ui.main.profile.editphoto

import android.net.Uri
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
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EditPhotoViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    var currentImageUri: Uri? = null

    private val _updatePhotoState = MutableStateFlow<Result<Boolean>?>(null)
    val updatePhotoState = _updatePhotoState.asStateFlow()

    private val _updatePlaylistPhotoState = MutableStateFlow<Result<Boolean>?>(null)
    val updatePlaylistPhotoState = _updatePlaylistPhotoState.asStateFlow()

    private val _snackbarState = MutableLiveData<Event<CustomSnackbar.SnackbarState>>()
    val snackbarState: LiveData<Event<CustomSnackbar.SnackbarState>> = _snackbarState

    fun updateAccount(photoFile: File) {
        viewModelScope.launch {
            repository.updateAccount(photoFile = photoFile).collect { result ->
                _updatePhotoState.value = result
            }
        }
    }

    fun updatePlaylist(playlistId: Int, photoFile: File) {
        viewModelScope.launch {
            repository.updatePlaylist(playlistId = playlistId, photoFile = photoFile).collect { result ->
                _updatePlaylistPhotoState.value = result
            }
        }
    }

    fun setSnackbar(message: String, state: Int, length: Int = CustomSnackbar.LENGTH_LONG) {
        _snackbarState.value = Event(CustomSnackbar.SnackbarState(message, state, length))
    }
}