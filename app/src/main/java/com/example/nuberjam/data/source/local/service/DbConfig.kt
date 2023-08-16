package com.example.nuberjam.data.source.local.service

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.nuberjam.data.source.local.entity.RecentFormSearch
import com.example.nuberjam.data.source.local.entity.RecentMusicSearch

@Database(entities = [RecentFormSearch::class, RecentMusicSearch::class], version = 1, exportSchema = false)
abstract class DbConfig : RoomDatabase() {
    abstract fun dbDao(): DbDao
}