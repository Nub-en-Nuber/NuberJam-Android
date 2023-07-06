package com.example.nuberjam.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_music")
class RecentMusicSearch(
    @field:ColumnInfo(name = "id")
    @field:PrimaryKey(autoGenerate = true)
    val id: Int,

    @field:ColumnInfo(name = "musicId")
    val musicId: Int,

    @field:ColumnInfo(name = "musicName")
    val musicName: String,

    @field:ColumnInfo(name = "musicDuration")
    val musicDuration: Int,

    @field:ColumnInfo(name = "musicFile")
    val musicFile: String,

    @field:ColumnInfo(name = "musicArtists")
    val musicArtists: String,

    @field:ColumnInfo(name = "albumId")
    val albumId: Int,

    @field:ColumnInfo(name = "albumName")
    val albumName: String,

    @field:ColumnInfo(name = "albumPhoto")
    val albumPhoto: String,
)