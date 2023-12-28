package com.example.nuberjam.ui.main.library.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nuberjam.data.Repository
import com.example.nuberjam.data.Result
import com.example.nuberjam.data.model.Album
import com.example.nuberjam.data.model.Music
import com.example.nuberjam.ui.customview.CustomSnackbar
import com.example.nuberjam.utils.BundleKeys
import com.example.nuberjam.utils.Event
import com.example.nuberjam.utils.LibraryDetailType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailLibraryViewModel @Inject constructor(
    private val repository: Repository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _favoriteState = MutableStateFlow<Result<List<Music>>?>(null)
    val favoriteState = _favoriteState.asStateFlow()

    private val _albumState = MutableStateFlow<Result<Album>?>(null)
    val albumState = _albumState.asStateFlow()

    private val _addDeleteFavoriteState = MutableStateFlow<Result<Boolean>?>(null)
    val addDeleteFavoriteState = _addDeleteFavoriteState.asStateFlow()

    private val _snackbarState = MutableLiveData<Event<CustomSnackbar.SnackbarState>>()
    val snackbarState: LiveData<Event<CustomSnackbar.SnackbarState>> = _snackbarState

    var libraryViewType =
        savedStateHandle[BundleKeys.LIBRARY_VIEW_TYPE_KEY] ?: LibraryDetailType.Album

    var playlistId = savedStateHandle[BundleKeys.PLAYLIST_ID_KEY] ?: 0

    var albumId = savedStateHandle[BundleKeys.ALBUM_ID_KEY] ?: 0

    init {
        when (libraryViewType) {
            LibraryDetailType.Favorite -> {
                getFavoriteData()
            }

            LibraryDetailType.Playlist -> {
                // TODO: Call playlist API here
            }

            LibraryDetailType.Album -> {
                getAlbumData()
            }
        }
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

    fun getAlbumData() {
        viewModelScope.launch {
            repository.readDetailAlbum(albumId).collect {
                _albumState.value = it
            }
        }
    }

    fun updateFavoriteData(musicId: Int, isInsert: Boolean) {
        viewModelScope.launch {
            repository.addDeleteFavorite(musicId, isInsert).collect {
                _addDeleteFavoriteState.value = it
            }
        }
    }
}