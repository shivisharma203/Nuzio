package com.nuzio.newsapp.data.repository

import com.nuzio.newsapp.core.network.Resource
import com.nuzio.newsapp.data.local.NewsDao
import com.nuzio.newsapp.data.local.entity.NewsArticleEntity
import com.nuzio.newsapp.data.remote.NewsApiService
import com.nuzio.newsapp.data.remote.dto.ArticleDto
import com.nuzio.newsapp.data.remote.dto.NewsResponseDto
import com.nuzio.newsapp.data.remote.dto.SourceDto
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class NewsRepositoryImplTest {

 private lateinit var newsApi: NewsApiService
 private lateinit var newsDao: NewsDao
 private lateinit var repository: NewsRepositoryImpl

 @Before
 fun setup() {
  newsApi = mockk()
  newsDao = mockk(relaxed = true)
  repository = NewsRepositoryImpl(newsApi, newsDao)
 }

 @Test
 fun `getTopHeadlines returns success when API call succeeds`() = runTest {
  val mockResponse = NewsResponseDto(
   status = "ok",
   totalResults = 1,
   articles = listOf(createMockArticleDto())
  )
  coEvery { newsApi.getTopHeadlinesDto(any(), any()) } returns mockResponse
  coEvery { newsDao.getAllNews() } returns emptyList()

  val result = repository.getTopHeadlines("us", null)

  assertTrue(result is Resource.Success)
  assertEquals(1, (result as Resource.Success).data.size)
  coVerify { newsDao.clearAllNews() }
  coVerify { newsDao.insertNews(any()) }
 }

 @Test
 fun `getTopHeadlines returns cached data when network fails and cache exists`() = runTest {
  coEvery { newsApi.getTopHeadlinesDto(any(), any()) } throws Exception("Network error")
  coEvery { newsDao.getAllNews() } returns listOf(createMockEntity())

  val result = repository.getTopHeadlines("us", null)

  assertTrue(result is Resource.Success)
  assertEquals(1, (result as Resource.Success).data.size)
 }

 @Test
 fun `getTopHeadlines returns error when network fails and no cache exists`() = runTest {
  coEvery { newsApi.getTopHeadlinesDto(any(), any()) } throws Exception("Network error")
  coEvery { newsDao.getAllNews() } returns emptyList()

  val result = repository.getTopHeadlines("us", null)

  assertTrue(result is Resource.Error)
 }

 @Test
 fun `searchNews returns success when API call succeeds`() = runTest {
  val mockResponse = NewsResponseDto(
   status = "ok",
   totalResults = 1,
   articles = listOf(createMockArticleDto())
  )
  coEvery { newsApi.searchNewsDto(any(), any(), any()) } returns mockResponse

  val result = repository.searchNews("bitcoin", "en", "publishedAt")

  assertTrue(result is Resource.Success)
  assertEquals(1, (result as Resource.Success).data.size)
 }

 @Test
 fun `getTopHeadlines caches articles after successful API call`() = runTest {
  val mockResponse = NewsResponseDto(
   status = "ok",
   totalResults = 1,
   articles = listOf(createMockArticleDto())
  )
  coEvery { newsApi.getTopHeadlinesDto(any(), any()) } returns mockResponse
  coEvery { newsDao.getAllNews() } returns emptyList()

  repository.getTopHeadlines("us", null)

  coVerify { newsDao.clearAllNews() }
  coVerify { newsDao.insertNews(match { it.size == 1 }) }
 }

 private fun createMockArticleDto() = ArticleDto(
  source = SourceDto(id = "test", name = "Test Source"),
  author = "Test Author",
  title = "Test Title",
  description = "Test Description",
  url = "https://test.com",
  urlToImage = null,
  publishedAt = "2024-01-01T00:00:00Z",
  content = "Test Content"
 )

 private fun createMockEntity() = NewsArticleEntity(
  id = "test-1",
  sourceId = "test",
  sourceName = "Test Source",
  author = "Test Author",
  title = "Test Title",
  description = "Test Description",
  url = "https://test.com",
  urlToImage = null,
  publishedAt = "2024-01-01T00:00:00Z",
  content = "Test Content"
 )
}