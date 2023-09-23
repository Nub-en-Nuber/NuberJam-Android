package com.example.nuberjam.ui.main.library

import androidx.lifecycle.ViewModel
import com.example.nuberjam.data.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    fun getAccountState() = repository.getAccountState()

    fun readAllPlaylist(accountId: Int) = repository.readAllPlaylist(accountId)

}