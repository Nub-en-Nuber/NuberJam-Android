package com.example.nuberjam.utils

import com.example.nuberjam.data.model.Account
import com.example.nuberjam.data.model.Album
import com.example.nuberjam.data.model.Artist
import com.example.nuberjam.data.model.Music
import com.example.nuberjam.data.model.Playlist
import com.example.nuberjam.data.source.remote.response.AccountItem
import com.example.nuberjam.data.source.remote.response.AlbumItem
import com.example.nuberjam.data.source.remote.response.DataResponse
import com.example.nuberjam.data.source.remote.response.MusicArtistItem
import com.example.nuberjam.data.source.remote.response.MusicItem
import com.example.nuberjam.data.source.remote.response.PlaylistItem
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

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
        music = musicItemToMusic(data.music, data)
    )

    fun musicItemToMusic(data: List<MusicItem>, album: AlbumItem): List<Music> {
        return data.map { musicData ->
            musicItemToMusic(musicData, album)
        }
    }

    fun musicItemToMusic(data: MusicItem, album: AlbumItem): Music = Music(
        playlistId = data.playlistDetailId,
        id = data.musicId,
        name = data.musicName,
        duration = data.musicDuration.toInt(),
        file = data.musicFile,
        artist = artistItemToArtist(data.musicArtist),
        isFavorite = data.musicIsFavorite,
        albumId = album.albumId,
        albumPhoto = album.albumPhoto,
        albumName = album.albumName
    )

    fun artistItemToArtist(data: List<MusicArtistItem>): List<Artist> {
        return data.map { artistData ->
            Artist(
                id = artistData.artistId, name = artistData.artistName
            )
        }
    }

    fun albumItemToAlbum(data: List<AlbumItem>): List<Album> {
        return data.map { albumItem ->
            albumItemToAlbum(albumItem)
        }
    }

    fun dataResponseToMusic(data: DataResponse): List<Music> {
        val listMusic = ArrayList<Music>()

        val listAlbumItem: List<AlbumItem> = data.data?.album ?: ArrayList()
        for (albumItem in listAlbumItem) {
            val listMusicItem = albumItem.music
            for (musicItem in listMusicItem) {
                listMusic.add(musicItemToMusic(musicItem, albumItem))
            }
        }

        return listMusic
    }

    fun playlistItemToPlaylist(data: List<PlaylistItem>): List<Playlist> {
        return data.map { playlistItem ->
            Playlist(
                id = playlistItem.playlistId,
                name = playlistItem.playlistName,
                photo = playlistItem.playlistPhoto
            )
        }
    }

    fun String.toRequestBodyType() = this.toRequestBody("text/plain".toMediaType())
}