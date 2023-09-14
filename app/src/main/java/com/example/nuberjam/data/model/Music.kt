package com.example.nuberjam.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Music(
    val playlistId: Int?,
    val id: Int?,
    val name: String?,
    val duration: Int?,
    val file: String?,
    val artist: List<Artist>?,
    val isFavorite: Boolean?,
    val albumId: Int?,
    val albumPhoto: String?,
    val albumName: String?
) : Parcelable