package com.example.nuberjam.ui.main.music

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
class MusicViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    var accountId: Int? = null
    var musicId: Int? = null
    var currentPlayingMusicId: Int? = null

    private val _snackbarState = MutableLiveData<Event<CustomSnackbar.SnackbarState>>()
    val snackbarState: LiveData<Event<CustomSnackbar.SnackbarState>> = _snackbarState

    private val _addDeleteFavoriteState = MutableStateFlow<Result<Boolean>?>(null)
    val addDeleteFavoriteState = _addDeleteFavoriteState.asStateFlow()

    fun setSnackbar(message: String, state: Int, length: Int = CustomSnackbar.LENGTH_LONG) {
        _snackbarState.value = Event(CustomSnackbar.SnackbarState(message, state, length))
    }

    fun getAccountState() = repository.getAccountState()

    fun readDetailMusic(): LiveData<Result<Music>>? {
        return if (accountId != null && musicId != null)
            repository.readDetailMusic(accountId!!, musicId!!)
        else
            null
    }

    fun saveCurrentMusic() {
        viewModelScope.launch {
            musicId?.let { repository.saveCurrentMusic(it) }
        }
    }

    fun getCurrentMusic() = repository.getCurrentMusic()

    fun isMusicIdSameCurrentPlaying(): Boolean {
        return if (currentPlayingMusicId != null) musicId == currentPlayingMusicId
        else false
    }

    fun updateFavoriteData(isInsert: Boolean) {
        viewModelScope.launch {
            repository.addDeleteFavorite(musicId ?: 0, isInsert).collect {
                _addDeleteFavoriteState.value = it
            }
        }
    }
}