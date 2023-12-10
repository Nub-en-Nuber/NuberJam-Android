package com.example.nuberjam.ui.main.profile.editphoto

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nuberjam.data.Repository
import com.example.nuberjam.data.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EditPhotoViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    var currentImageUri: Uri? = null

    private val _updatePhotoState = MutableStateFlow<Result<Boolean>?>(null)
    val updatePhotoState = _updatePhotoState.asStateFlow()

    fun updateAccount(photoFile: File) {
        viewModelScope.launch {
            repository.updateAccount(photoFile = photoFile).collect { result ->
                _updatePhotoState.value = result
            }
        }
    }
}