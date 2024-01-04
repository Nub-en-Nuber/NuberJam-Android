package com.example.nuberjam.data.source.remote.request

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody

data class PlaylistRequest(
    @SerializedName("accountId")
    val accountId: RequestBody,

    @SerializedName("playlistName")
    val name: RequestBody? = null,

    @SerializedName("playlistPhoto")
    val photo: MultipartBody.Part? = null
)