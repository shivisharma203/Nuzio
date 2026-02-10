package com.nuzio.newsapp.domain.usecase

import com.nuzio.newsapp.core.network.Resource
import com.nuzio.newsapp.domain.model.NewsArticle
import com.nuzio.newsapp.domain.repository.NewsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SearchNewsUseCaseTest {

 private lateinit var repository: NewsRepository
 private lateinit var useCase: SearchNewsUseCase

 @Before
 fun setup() {
  repository = mockk()
  useCase = SearchNewsUseCase(repository)
 }

 @Test
 fun `invoke with valid query calls repository`() = runTest {
  val mockArticles = listOf(createMockArticle())
  coEvery { repository.searchNews(any(), any(), any()) } returns Resource.Success(mockArticles)

  useCase.invoke(query = "bitcoin")

  coVerify { repository.searchNews("bitcoin", "en", "publishedAt") }
 }

 @Test
 fun `invoke with blank query returns error`() = runTest {
  val result = useCase.invoke(query = "")

  assertTrue(result is Resource.Error)
  assertEquals("Please enter a search term", (result as Resource.Error).message)
 }

 @Test
 fun `invoke with custom language passes correct parameter`() = runTest {
  val mockArticles = listOf(createMockArticle())
  coEvery { repository.searchNews(any(), any(), any()) } returns Resource.Success(mockArticles)

  useCase.invoke(query = "news", language = "fr")

  coVerify { repository.searchNews("news", "fr", "publishedAt") }
 }

 @Test
 fun `invoke returns success when repository succeeds`() = runTest {
  val mockArticles = listOf(createMockArticle())
  coEvery { repository.searchNews(any(), any(), any()) } returns Resource.Success(mockArticles)

  val result = useCase.invoke(query = "test")

  assertTrue(result is Resource.Success)
  assertEquals(1, (result as Resource.Success).data.size)
 }

 private fun createMockArticle() = NewsArticle(
  id = "1",
  source = NewsArticle.Source(id = "test", name = "Test Source"),
  author = "Test Author",
  title = "Test Title",
  description = "Test Description",
  url = "https://test.com",
  urlToImage = null,
  publishedAt = "2024-01-01T00:00:00Z",
  content = "Test Content"
 )
}