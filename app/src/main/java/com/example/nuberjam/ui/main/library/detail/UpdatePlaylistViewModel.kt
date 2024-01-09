package com.example.nuberjam.ui.main.library.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nuberjam.data.Repository
import com.example.nuberjam.data.Result
import com.example.nuberjam.utils.BundleKeys.PLAYLIST_ID_KEY
import com.example.nuberjam.utils.BundleKeys.PLAYLIST_NAME_KEY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdatePlaylistViewModel @Inject constructor(
    private val repository: Repository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var name = savedStateHandle.get<String>(PLAYLIST_NAME_KEY) ?: ""
    val id = savedStateHandle.get<Int>(PLAYLIST_ID_KEY)

    private val _updatePlaylistState = MutableStateFlow<Result<Boolean>?>(null)
    val updatePlaylistState = _updatePlaylistState.asStateFlow()

    private val _deletePlaylistState = MutableStateFlow<Result<Boolean>?>(null)
    val deletePlaylistState = _deletePlaylistState.asStateFlow()

    fun updatePlaylist() {
        if (id != null) {
            viewModelScope.launch {
                repository.updatePlaylist(id!!, playlistName = name).collect { result ->
                    _updatePlaylistState.value = result
                }
            }
        }
    }

    fun deletePlaylist() {
        if (id != null) {
            viewModelScope.launch {
                repository.deletePlaylist(id).collect { result ->
                    _deletePlaylistState.value = result
                }
            }
        }
    }
}