package com.example.nuberjam.ui

import androidx.lifecycle.ViewModel
import com.example.nuberjam.data.Repository

class SplashViewModel (private val repository: Repository) : ViewModel() {
    fun getLoginState() = repository.getLoginState()
}