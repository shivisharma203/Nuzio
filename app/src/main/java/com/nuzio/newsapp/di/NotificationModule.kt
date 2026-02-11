package com.nuzio.newsapp.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.messaging.FirebaseMessaging
import com.nuzio.newsapp.core.datastore.NotificationPreferencesDataStore
import com.nuzio.newsapp.data.local.AppDatabase
import com.nuzio.newsapp.data.local.dao.NotificationDao
import com.nuzio.newsapp.data.remote.FcmTokenManager
import com.nuzio.newsapp.data.repository.NotificationRepositoryImpl
import com.nuzio.newsapp.domain.repository.NotificationRepository

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for notification dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {
    

    
    /**
     * Provide NotificationDao
     */
    @Provides
    @Singleton
    fun provideNotificationDao(database: AppDatabase): NotificationDao {
        return database.notificationDao()
    }
    
    /**
     * Provide NotificationPreferencesDataStore
     */
    @Provides
    @Singleton
    fun provideNotificationPreferencesDataStore(
        @ApplicationContext context: Context
    ): NotificationPreferencesDataStore {
        return NotificationPreferencesDataStore(context)
    }
    
    /**
     * Provide FirebaseMessaging instance
     */
    @Provides
    @Singleton
    fun provideFirebaseMessaging(): FirebaseMessaging {
        return FirebaseMessaging.getInstance()
    }
    
    /**
     * Provide FcmTokenManager
     */
    @Provides
    @Singleton
    fun provideFcmTokenManager(
        firebaseMessaging: FirebaseMessaging
    ): FcmTokenManager {
        return FcmTokenManager(firebaseMessaging)
    }
    
    /**
     * Provide NotificationRepository
     */
    @Provides
    @Singleton
    fun provideNotificationRepository(
        notificationDao: NotificationDao,
        preferencesDataStore: NotificationPreferencesDataStore,
        fcmTokenManager: FcmTokenManager
    ): NotificationRepository {
        return NotificationRepositoryImpl(
            notificationDao = notificationDao,
            preferencesDataStore = preferencesDataStore,
            fcmTokenManager = fcmTokenManager
        )
    }
}
