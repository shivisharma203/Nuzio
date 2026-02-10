package com.nuzio.newsapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nuzio.newsapp.data.local.entity.NewsArticleEntity

@Database(
    entities = [NewsArticleEntity::class],
    version = 3, // Increment version from 2 to 3
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun newsDao(): NewsDao

    companion object {
        const val DATABASE_NAME = "nuzio_database"

        /**
         * Migration from version 2 to 3: Add section column.
         */
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add section column with default value "TOP_STORIES"
                database.execSQL(
                    "ALTER TABLE news_articles ADD COLUMN section TEXT NOT NULL DEFAULT 'TOP_STORIES'"
                )
            }
        }
    }
}