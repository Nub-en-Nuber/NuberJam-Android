package com.example.nuberjam.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Album(
    val id : Int?,
    val name : String?,
    val photo : String?,
    val music : List<Music>?
) : Parcelable