package com.example.nuberjam.utils

import com.example.nuberjam.data.model.Account
import com.example.nuberjam.data.model.Album
import com.example.nuberjam.data.model.Artist
import com.example.nuberjam.data.model.Music
import com.example.nuberjam.data.source.remote.response.AccountItem
import com.example.nuberjam.data.source.remote.response.AlbumItem
import com.example.nuberjam.data.source.remote.response.MusicArtistItem
import com.example.nuberjam.data.source.remote.response.MusicItem

object Mapping {
    fun accountItemToAccount(data: AccountItem): Account = Account(
        id = data.accountId,
        name = data.accountName,
        username = data.accountUsername,
        email = data.accountEmail,
        password = data.accountPassword,
        photo = data.accountPhoto
    )

    fun albumItemToAlbum(data: AlbumItem): Album = Album(
        id = data.albumId,
        name = data.albumName,
        photo = data.albumPhoto,
        music = if (data.music.isEmpty()) null else musicItemsToMusics(data.music)
    )

    fun musicItemsToMusics(data: List<MusicItem>): List<Music> {
        return data.map { musicData ->
            Music(
                playlistId = musicData.playlistDetailId,
                id = musicData.musicId,
                name = musicData.musicName,
                duration = musicData.musicDuration.toInt(),
                file = musicData.musicFile,
                artist = artistItemsToArtists(musicData.musicArtist),
                isFavorite = musicData.musicIsFavorite
            )
        }
    }

    fun artistItemsToArtists(data: List<MusicArtistItem>): List<Artist> {
        return data.map { artistData ->
            Artist(
                id = artistData.artistId, name = artistData.artistName
            )
        }
    }

    fun albumItemsToAlbums(data: List<AlbumItem>): List<Album> {
        return data.map { albumItem ->
            Album(
                id = albumItem.albumId,
                name = albumItem.albumName,
                photo = albumItem.albumPhoto,
                music = musicItemsToMusics(albumItem.music)
            )
        }
    }
}