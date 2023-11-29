package com.example.nuberjam.ui.main.profile.editphoto

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nuberjam.data.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EditPhotoViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    var currentImageUri: Uri? = null
    var currentImageFile: File? = null
}