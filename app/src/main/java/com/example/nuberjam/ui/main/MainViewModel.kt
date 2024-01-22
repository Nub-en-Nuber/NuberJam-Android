package com.example.nuberjam.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.nuberjam.data.Repository
import com.example.nuberjam.data.Result
import com.example.nuberjam.data.model.CurrentMusic
import com.example.nuberjam.data.model.Music
import com.example.nuberjam.ui.customview.CustomSnackbar
import com.example.nuberjam.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    val accountId by lazy {
        runBlocking {
            repository.getAccountState().asFlow().first().id
        }
    }

    var currentMusic: CurrentMusic? = null

    private val _musicState = MutableStateFlow<Result<Music>?>(null)
    val musicState: Flow<Result<Music>?> = _musicState.asStateFlow()

    init {
        getMusicState()
    }

    private val _snackbarState = MutableLiveData<Event<CustomSnackbar.SnackbarState>>()
    val snackbarState: LiveData<Event<CustomSnackbar.SnackbarState>> = _snackbarState

    fun setSnackbar(message: String, state: Int, length: Int = CustomSnackbar.LENGTH_LONG) {
        _snackbarState.value = Event(CustomSnackbar.SnackbarState(message, state, length))
    }

    private fun getMusicState() {
        viewModelScope.launch {
            repository.getCurrentMusic().collect {
                currentMusic = it
                refreshMusicState()
            }
        }
    }

    fun refreshMusicState() {
        viewModelScope.launch {
            if (currentMusic != null) {
                repository.readDetailMusic(accountId, currentMusic!!.id).asFlow().collect {
                    _musicState.value = it
                }
            }
        }
    }
}