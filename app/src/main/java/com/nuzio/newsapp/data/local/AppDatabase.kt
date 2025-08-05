package com.nuzio.newsapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nuzio.newsapp.data.entities.NewsItemEntity

@Database(entities = [NewsItemEntity::class], version = 1, exportSchema = false)

abstract class AppDatabase : RoomDatabase() {
    abstract fun newsDao(): NewsDao
}