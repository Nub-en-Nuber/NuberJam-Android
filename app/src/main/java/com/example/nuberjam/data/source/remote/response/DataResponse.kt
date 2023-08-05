package com.example.nuberjam.data.source.remote.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class DataResponse(

    @field:SerializedName("data")
    val data: Data? = null,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("status")
    val status: Int
) : Parcelable

@Parcelize
data class AccountItem(

    @field:SerializedName("accountPhoto")
    val accountPhoto: String,

    @field:SerializedName("accountId")
    val accountId: Int,

    @field:SerializedName("accountEmail")
    val accountEmail: String,

    @field:SerializedName("accountName")
    val accountName: String,

    @field:SerializedName("accountUsername")
    val accountUsername: String,

    @field:SerializedName("accountPassword")
    val accountPassword: String
) : Parcelable

@Parcelize
data class PlaylistItem(

    @field:SerializedName("playlistId")
    val playlistId: Int,

    @field:SerializedName("playlistPhoto")
    val playlistPhoto: String,

    @field:SerializedName("playlistName")
    val playlistName: String
) : Parcelable

@Parcelize
data class Data(

    @field:SerializedName("playlist")
    val playlist: List<PlaylistItem>,

    @field:SerializedName("album")
    val album: List<AlbumItem>,

    @field:SerializedName("account")
    val account: List<AccountItem>
) : Parcelable

@Parcelize
data class MusicItem(

    @field:SerializedName("musicDuration")
    val musicDuration: Int,

    @field:SerializedName("musicId")
    val musicId: Int,

    @field:SerializedName("musicIsFavorite")
    val musicIsFavorite: Boolean,

    @field:SerializedName("playlistDetailId")
    val playlistDetailId: Int? = null,

    @field:SerializedName("musicArtist")
    val musicArtist: List<MusicArtistItem>,

    @field:SerializedName("musicFile")
    val musicFile: String,

    @field:SerializedName("musicName")
    val musicName: String
) : Parcelable

@Parcelize
data class AlbumItem(

    @field:SerializedName("albumName")
    val albumName: String,

    @field:SerializedName("music")
    val music: List<MusicItem>,

    @field:SerializedName("albumPhoto")
    val albumPhoto: String,

    @field:SerializedName("albumId")
    val albumId: Int
) : Parcelable

@Parcelize
data class MusicArtistItem(

    @field:SerializedName("artistId")
    val artistId: Int,

    @field:SerializedName("artistName")
    val artistName: String
) : Parcelable
