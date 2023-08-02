package com.example.nuberjam.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Account(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val password: String,
    val photo: String
) : Parcelable