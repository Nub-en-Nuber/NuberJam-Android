package com.example.nuberjam.data.source.remote.request

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody

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
)