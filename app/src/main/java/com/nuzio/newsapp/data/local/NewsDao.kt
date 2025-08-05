
package com.nuzio.newsapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nuzio.newsapp.data.entities.NewsItemEntity


@Dao
interface NewsDao {
    @Query("SELECT * FROM news_items")
    suspend fun getAllNews(): List<NewsItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(news: List<NewsItemEntity>)

    @Query("DELETE FROM news_items")
    suspend fun clearAll()
}
