package com.nuzio.newsapp.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.runner.AndroidJUnit4
import com.nuzio.newsapp.data.local.entity.NewsArticleEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NewsDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var newsDao: NewsDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        newsDao = database.newsDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndRetrieveArticle() = runTest {
        val article = createTestArticle("test-1")

        newsDao.insertArticle(article)
        val retrieved = newsDao.getArticleById("test-1")

        assertNotNull(retrieved)
        assertEquals(article.title, retrieved?.title)
        assertEquals(article.url, retrieved?.url)
    }

    @Test
    fun insertMultipleArticlesAndRetrieveAll() = runTest {
        val articles = listOf(
            createTestArticle("test-1"),
            createTestArticle("test-2"),
            createTestArticle("test-3")
        )

        newsDao.insertNews(articles)
        val allArticles = newsDao.getAllNews()

        assertEquals(3, allArticles.size)
    }

    @Test
    fun clearAllNewsDeletesAllArticles() = runTest {
        newsDao.insertNews(listOf(
            createTestArticle("test-1"),
            createTestArticle("test-2")
        ))

        newsDao.clearAllNews()
        val count = newsDao.getArticleCount()

        assertEquals(0, count)
    }

    @Test
    fun replaceArticleOnConflict() = runTest {
        val article1 = createTestArticle("test-1").copy(title = "Original Title")
        val article2 = createTestArticle("test-1").copy(title = "Updated Title")

        newsDao.insertArticle(article1)
        newsDao.insertArticle(article2)

        val retrieved = newsDao.getArticleById("test-1")
        assertEquals("Updated Title", retrieved?.title)
        assertEquals(1, newsDao.getArticleCount())
    }

    @Test
    fun getArticlesCachedAfterFiltersCorrectly() = runTest {
        val now = System.currentTimeMillis()
        val old = createTestArticle("old").copy(cachedAt = now - 10000)
        val recent = createTestArticle("recent").copy(cachedAt = now)

        newsDao.insertNews(listOf(old, recent))

        val recentArticles = newsDao.getArticlesCachedAfter(now - 5000)

        assertEquals(1, recentArticles.size)
        assertEquals("recent", recentArticles[0].id)
    }

    private fun createTestArticle(id: String) = NewsArticleEntity(
        id = id,
        sourceId = "test-source",
        sourceName = "Test Source",
        author = "Test Author",
        title = "Test Title $id",
        description = "Test description",
        url = "https://test.com/$id",
        urlToImage = null,
        publishedAt = "2024-01-01T00:00:00Z",
        content = "Test content",
        cachedAt = System.currentTimeMillis()
    )
}