package com.example.nuberjam.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Playlist(
    val id : Int,
    val name : String,
    val photo : String
) : Parcelable