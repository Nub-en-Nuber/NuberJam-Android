package com.example.nuberjam.data.source.remote.request

import com.example.nuberjam.utils.extension.toMultipartBody
import com.google.gson.annotations.SerializedName
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

data class AccountRequest(
    @SerializedName("accountName")
    val name: RequestBody? = null,

    @SerializedName("accountUsername")
    val username: RequestBody? = null,

    @SerializedName("accountEmail")
    val email: RequestBody? = null,

    @SerializedName("accountPassword")
    val password: RequestBody? = null,

    @SerializedName("accountPhoto")
    val photo: MultipartBody.Part? = null,
) {
    companion object {
        fun createFromRaw(
            name: String? = null,
            username: String? = null,
            email: String? = null,
            password: String? = null,
            photo: File? = null
        ): AccountRequest {
            val nameRequestBody = name?.toRequestBody("text/plain".toMediaType())
            val usernameRequestBody = username?.toRequestBody("text/plain".toMediaType())
            val emailRequestBody = email?.toRequestBody("text/plain".toMediaType())
            val passwordRequestBody = password?.toRequestBody("text/plain".toMediaType())
            val photoRequestBody = photo?.toMultipartBody("photo")
            return AccountRequest(
                nameRequestBody,
                usernameRequestBody,
                emailRequestBody,
                passwordRequestBody,
                photoRequestBody
            )
        }
    }
}