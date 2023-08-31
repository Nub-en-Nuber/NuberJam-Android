package com.example.nuberjam.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Music(
    val playlistId: Int?,
    val id : Int?,
    val name : String?,
    val duration: String?,
    val file : String?,
    val artist: List<Artist>?,
    val isFavorite: Boolean?
): Parcelable