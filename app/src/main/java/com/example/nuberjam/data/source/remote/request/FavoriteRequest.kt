package com.example.nuberjam.data.source.remote.request

import com.google.gson.annotations.SerializedName
import okhttp3.RequestBody

data class FavoriteRequest(
    @field:SerializedName("musicId")
    val musicId: RequestBody,

    @field:SerializedName("accountId")
    val accountId: RequestBody
)