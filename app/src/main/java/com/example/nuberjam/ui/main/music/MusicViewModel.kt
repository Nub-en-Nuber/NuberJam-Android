package com.example.nuberjam.ui.main.music

import androidx.lifecycle.ViewModel
import com.example.nuberjam.data.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MusicViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    var musicId = 0

    fun getAccountState() = repository.getAccountState()

    fun readDetailMusic(accountId: Int) = repository.readDetailMusic(accountId, musicId)
}