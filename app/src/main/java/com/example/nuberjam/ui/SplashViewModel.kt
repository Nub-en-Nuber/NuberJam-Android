package com.example.nuberjam.ui

import androidx.lifecycle.ViewModel
import com.example.nuberjam.data.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    var isLoading = true
    fun getLoginState() = repository.getLoginState()
}