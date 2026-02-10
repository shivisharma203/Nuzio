package com.nuzio.newsapp.di

import android.content.Context
import androidx.room.Room
import com.nuzio.newsapp.data.local.AppDatabase
import com.nuzio.newsapp.data.local.NewsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing database-related dependencies.
 *
 * This module configures Room database with proper initialization,
 * migration strategies, and DAO provision for repository access.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Provides the Room database instance.
     *
     * The database is created as a singleton to ensure only one instance
     * exists throughout the application lifecycle, preventing multiple
     * database connections and potential data inconsistencies.
     */
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "nuzio_database"
        )
            .fallbackToDestructiveMigration() // For development; use proper migrations in production
            .build()
    }

    /**
     * Provides NewsDao for news article database operations.
     *
     * The DAO is extracted from the database instance and provided
     * to repositories that need local data access.
     */
    @Provides
    @Singleton
    fun provideNewsDao(database: AppDatabase): NewsDao {
        return database.newsDao()
    }
}