package com.example.nuberjam.data.source.remote.request

import com.google.gson.annotations.SerializedName

data class FavoriteRequest(
    @field:SerializedName("musicId")
    val musicId: Int,

    @field:SerializedName("accountId")
    val accountId: Int
)