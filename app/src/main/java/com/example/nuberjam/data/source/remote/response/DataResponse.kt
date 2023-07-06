package com.example.nuberjam.data.source.remote.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class DataResponse(

    @field:SerializedName("data")
    val data: Data? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: Int? = null
) : Parcelable

@Parcelize
data class AccountItem(

    @field:SerializedName("accountPhoto")
    val accountPhoto: String? = null,

    @field:SerializedName("accountId")
    val accountId: Int? = null,

    @field:SerializedName("accountEmail")
    val accountEmail: String? = null,

    @field:SerializedName("accountName")
    val accountName: String? = null,

    @field:SerializedName("accountUsername")
    val accountUsername: String? = null,

    @field:SerializedName("accountPassword")
    val accountPassword: String? = null
) : Parcelable

@Parcelize
data class PlaylistItem(

    @field:SerializedName("playlistId")
    val playlistId: Int? = null,

    @field:SerializedName("playlistPhoto")
    val playlistPhoto: String? = null,

    @field:SerializedName("playlistName")
    val playlistName: String? = null
) : Parcelable

@Parcelize
data class Data(

    @field:SerializedName("playlist")
    val playlist: List<PlaylistItem?>? = null,

    @field:SerializedName("album")
    val album: List<AlbumItem?>? = null,

    @field:SerializedName("account")
    val account: List<AccountItem?>? = null
) : Parcelable

@Parcelize
data class MusicItem(

    @field:SerializedName("musicDuration")
    val musicDuration: Int? = null,

    @field:SerializedName("musicId")
    val musicId: Int? = null,

    @field:SerializedName("musicIsFavorite")
    val musicIsFavorite: Boolean? = null,

    @field:SerializedName("playlistDetailId")
    val playlistDetailId: Int? = null,

    @field:SerializedName("musicArtist")
    val musicArtist: List<MusicArtistItem?>? = null,

    @field:SerializedName("musicFile")
    val musicFile: String? = null,

    @field:SerializedName("musicName")
    val musicName: String? = null
) : Parcelable

@Parcelize
data class AlbumItem(

    @field:SerializedName("albumName")
    val albumName: String? = null,

    @field:SerializedName("music")
    val music: List<MusicItem?>? = null,

    @field:SerializedName("albumPhoto")
    val albumPhoto: String? = null,

    @field:SerializedName("albumId")
    val albumId: Int? = null
) : Parcelable

@Parcelize
data class MusicArtistItem(

    @field:SerializedName("artistId")
    val artistId: Int? = null,

    @field:SerializedName("artistName")
    val artistName: String? = null
) : Parcelable
