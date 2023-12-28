package com.example.nuberjam.utils.extensions

import com.example.nuberjam.data.model.Account
import com.example.nuberjam.data.source.remote.request.AccountRequest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

fun Account.toAccountRequest(photoFile: File?) =
    AccountRequest(
        name = name.ifEmpty { null }?.toRequestBody("text/plain".toMediaType()),
        password = password.ifEmpty { null }?.toRequestBody("text/plain".toMediaType()),
        photo = photoFile?.toMultipartBody("accountPhoto")
    )