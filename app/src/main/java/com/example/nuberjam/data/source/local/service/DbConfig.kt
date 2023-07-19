package com.example.nuberjam.data.source.local.service

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.nuberjam.data.source.local.entity.RecentFormSearch
import com.example.nuberjam.data.source.local.entity.RecentMusicSearch
import com.example.nuberjam.utils.Constant

@Database(entities = [RecentFormSearch::class, RecentMusicSearch::class], version = 1)
abstract class DbConfig : RoomDatabase() {
    abstract fun dbDao(): DbDao

    companion object {
        @Volatile
        private var instance: DbConfig? = null
        fun getInstance(context: Context): DbConfig =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    DbConfig::class.java, Constant.DATABASE_NAME
                ).allowMainThreadQueries()
                    .build()
            }
    }
}