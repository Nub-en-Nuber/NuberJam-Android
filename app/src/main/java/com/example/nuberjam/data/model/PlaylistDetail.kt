package com.example.nuberjam.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaylistDetail(
    val info : Playlist,
    val music : List<Music>,
) : Parcelable