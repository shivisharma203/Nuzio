package com.nuzio.newsapp.di

import com.nuzio.newsapp.data.repository.NewsRepositoryImpl
import com.nuzio.newsapp.data.repository.PreferencesRepositoryImpl
import com.nuzio.newsapp.domain.repository.NewsRepository
import com.nuzio.newsapp.domain.repository.PreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindNewsRepository(
        impl: NewsRepositoryImpl
    ): NewsRepository

    @Binds
    @Singleton
    abstract fun bindPreferencesRepository(
        impl: PreferencesRepositoryImpl
    ): PreferencesRepository
}