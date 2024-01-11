package com.example.nuberjam.ui.main.library.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nuberjam.data.Repository
import com.example.nuberjam.data.Result
import com.example.nuberjam.utils.BundleKeys.MUSIC_ID_KEY
import com.example.nuberjam.utils.BundleKeys.PLAYLIST_DETAIL_ID_KEY
import com.example.nuberjam.utils.BundleKeys.PLAYLIST_ID_KEY
import com.example.nuberjam.utils.BundleKeys.PLAYLIST_NAME_KEY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdatePlaylistViewModel @Inject constructor(
    private val repository: Repository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var name = savedStateHandle.get<String>(PLAYLIST_NAME_KEY) ?: ""
    val playlistId = savedStateHandle.get<Int>(PLAYLIST_ID_KEY)
    val musicId = savedStateHandle.get<Int>(MUSIC_ID_KEY)
    val playlistDetailId = savedStateHandle.get<Int>(PLAYLIST_DETAIL_ID_KEY)

    var selectedPlaylistId = 0

    private val _updatePlaylistState = MutableStateFlow<Result<Boolean>?>(null)
    val updatePlaylistState = _updatePlaylistState.asStateFlow()

    private val _deletePlaylistState = MutableStateFlow<Result<Boolean>?>(null)
    val deletePlaylistState = _deletePlaylistState.asStateFlow()

    private val _deleteMusicFromPlaylist = MutableStateFlow<Result<Boolean>?>(null)
    val deleteMusicFromPlaylistState = _deleteMusicFromPlaylist.asStateFlow()

    private val _checkMusicInPlaylistState = MutableStateFlow<Result<Boolean>?>(null)
    val checkMusicInPlaylistState = _checkMusicInPlaylistState.asStateFlow()

    private val _addMusicToPlaylistState = MutableStateFlow<Result<Boolean>?>(null)
    val addMusicToPlaylistState = _addMusicToPlaylistState.asStateFlow()

    fun updatePlaylist() {
        if (playlistId != null) {
            viewModelScope.launch {
                repository.updatePlaylist(playlistId, playlistName = name).collect { result ->
                    _updatePlaylistState.value = result
                }
            }
        }
    }

    fun deletePlaylist() {
        if (playlistId != null) {
            viewModelScope.launch {
                repository.deletePlaylist(playlistId).collect { result ->
                    _deletePlaylistState.value = result
                }
            }
        }
    }

    fun searchPlaylist(query: String = "") = repository.readAllPlaylist(query)

    fun deleteMusicFromPlaylist() {
        if (playlistDetailId != null) {
            viewModelScope.launch {
                repository.deleteMusicFromPlaylist(playlistDetailId).collect { result ->
                    _deleteMusicFromPlaylist.value = result
                }
            }
        }
    }

    fun checkMusicIsExist() {
        if (musicId != null) {
            viewModelScope.launch {
                repository.checkMusicIsExist(selectedPlaylistId, musicId).collect { result ->
                    _checkMusicInPlaylistState.value = result
                }
            }
        }
    }

    fun addMusicToPlaylist() {
        if (musicId != null) {
            viewModelScope.launch {
                repository.addMusicToPlaylist(selectedPlaylistId, musicId).collect { result ->
                    _addMusicToPlaylistState.value = result
                }
            }
        }
    }
}