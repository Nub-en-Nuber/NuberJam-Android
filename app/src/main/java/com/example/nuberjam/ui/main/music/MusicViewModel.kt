package com.example.nuberjam.ui.main.music

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.nuberjam.data.Repository
import com.example.nuberjam.data.Result
import com.example.nuberjam.data.model.Music
import com.example.nuberjam.ui.customview.CustomSnackbar
import com.example.nuberjam.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class MusicViewModel @Inject constructor(
    private val repository: Repository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val accountId: Int by lazy {
        val account = runBlocking {
            repository.getAccountState().asFlow().first()
        }
        account.id
    }

    val musicId: Int by lazy {
        val args = MusicFragmentArgs.fromSavedStateHandle(savedStateHandle)
        args.musicId
    }

    var music: Music? = null

    private val _musicState = MutableStateFlow<Result<Music>?>(null)
    val musicState: StateFlow<Result<Music>?> = _musicState.asStateFlow()

    private val _snackbarState = MutableLiveData<Event<CustomSnackbar.SnackbarState>>()
    val snackbarState: LiveData<Event<CustomSnackbar.SnackbarState>> = _snackbarState

    init {
        readDetailMusic()
    }

    fun setSnackbar(message: String, state: Int, length: Int = CustomSnackbar.LENGTH_LONG) {
        _snackbarState.value = Event(CustomSnackbar.SnackbarState(message, state, length))
    }

    private fun readDetailMusic() {
        viewModelScope.launch {
            repository.readDetailMusic(accountId, musicId).asFlow().collect { result ->
                _musicState.value = result
            }
        }
    }
}