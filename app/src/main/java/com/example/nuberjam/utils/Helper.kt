package com.example.nuberjam.utils

import com.example.nuberjam.data.model.Artist

object Helper {

    fun concatenateArtist(artists: List<Artist>) = artists.joinToString(", ") { artist ->
        artist.name
    }

    fun displayDuration(duration: Int): String {
        val hour = duration / 3600
        val minute = (duration % 3600) / 60
        val second = duration % 60
        val fixSecond = if (second < 10) "0${second}" else second

        var durations = "${minute}:${fixSecond}"
        if (hour > 0) durations = "$hour:$durations"
        return durations
    }
}