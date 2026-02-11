package com.nuzio.newsapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nuzio.newsapp.data.local.entity.NewsArticleEntity
import com.nuzio.newsapp.data.local.dao.NotificationDao
import com.nuzio.newsapp.data.local.entity.NotificationEntity


@Database(
    entities = [NewsArticleEntity::class,
               NotificationEntity::class],
    version = 4, // ✅ Increment version from 3 to 4
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun newsDao(): NewsDao
    abstract fun notificationDao(): NotificationDao
    companion object {
        const val DATABASE_NAME = "nuzio_database"



        val MIGRATION_3_4 = object : Migration(3, 4)
        {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS notifications (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                message TEXT NOT NULL,
                timestamp INTEGER NOT NULL
            )
            """.trimIndent()
                )
            }
        }

    }
}