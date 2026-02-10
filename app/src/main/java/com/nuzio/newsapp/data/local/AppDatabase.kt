package com.nuzio.newsapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nuzio.newsapp.data.local.entity.NewsArticleEntity

/**
 * Room database for the Nuzio application.
 *
 * Defines the database configuration including entities, version number,
 * and provides access to DAOs for data operations.
 */
@Database(
    entities = [NewsArticleEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Provides access to news article database operations.
     */
    abstract fun newsDao(): NewsDao
}