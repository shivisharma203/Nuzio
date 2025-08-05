package com.nuzio.newsapp.di

import android.content.Context
import androidx.room.Room
import com.nuzio.newsapp.data.remote.NewsApiService
import com.nuzio.newsapp.data.NewsRepository
import com.nuzio.newsapp.data.local.AppDatabase
import com.nuzio.newsapp.data.local.NewsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val BASE_URL = "https://api.rss2json.com/"

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideNewsApiService(retrofit: Retrofit): NewsApiService =
        retrofit.create(NewsApiService::class.java)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase =
        Room.databaseBuilder(appContext, AppDatabase::class.java, "news_db").build()

    @Provides
    fun provideNewsDao(db: AppDatabase): NewsDao = db.newsDao()

    @Provides
    @Singleton
    fun provideNewsRepository(api: NewsApiService, dao: NewsDao) =
        NewsRepository(api, dao)
}
