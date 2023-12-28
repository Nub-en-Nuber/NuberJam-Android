package com.example.nuberjam.utils.extensions

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

fun <T> LifecycleOwner.collectLifecycleFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
    this.lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect(collect)
        }
    }
}

fun File.toMultipartBody(field: String): MultipartBody.Part {
    val requestImageFile = this.asRequestBody("image/*".toMediaType())
    return MultipartBody.Part.createFormData(
        field,
        this.name,
        requestImageFile
    )
}
