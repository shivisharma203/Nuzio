package com.nuzio.newsapp.domain.usecase

import com.nuzio.newsapp.core.network.Resource
import com.nuzio.newsapp.domain.model.NewsArticle
import com.nuzio.newsapp.domain.repository.NewsRepository
import com.nuzio.newsapp.features.news.list.NewsSection
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GetTopHeadlinesUseCaseTest {

 private lateinit var repository: NewsRepository
 private lateinit var useCase: GetTopHeadlinesUseCase

 @Before
 fun setup() {
  repository = mockk()
  useCase = GetTopHeadlinesUseCase(repository)
 }

 @Test
 fun `invoke with default parameters calls repository with Top Stories`() = runTest {
  val mockArticles = listOf(createMockArticle("1"))
  coEvery { repository.getTopHeadlines(any(), any()) } returns Resource.Success(mockArticles)

  useCase.invoke()

  coVerify { repository.getTopHeadlines(NewsSection.TOP_STORIES, "us") }
 }

 @Test
 fun `invoke with specific section passes correct parameter to repository`() = runTest {
  val mockArticles = listOf(createMockArticle("1"))
  coEvery { repository.getTopHeadlines(any(), any()) } returns Resource.Success(mockArticles)

  useCase.invoke(section = NewsSection.TECHNOLOGY, country = "gb")

  coVerify { repository.getTopHeadlines(NewsSection.TECHNOLOGY, "gb") }
 }

 @Test
 fun `invoke returns success when repository succeeds`() = runTest {
  val mockArticles = listOf(createMockArticle("1"), createMockArticle("2"))
  coEvery { repository.getTopHeadlines(any(), any()) } returns Resource.Success(mockArticles)

  val result = useCase.invoke(section = NewsSection.BUSINESS)

  assertTrue(result is Resource.Success)
  assertEquals(2, (result as Resource.Success).data.size)
 }

 @Test
 fun `invoke returns error when repository fails`() = runTest {
  val exception = Exception("Network error")
  coEvery { repository.getTopHeadlines(any(), any()) } returns Resource.Error(exception, "Failed")

  val result = useCase.invoke(section = NewsSection.SPORTS)

  assertTrue(result is Resource.Error)
  assertEquals("Failed", (result as Resource.Error).message)
 }

 private fun createMockArticle(id: String) = NewsArticle(
  id = id,
  source = NewsArticle.Source(id = "test", name = "Test Source"),
  author = "Test Author",
  title = "Test Title $id",
  description = "Test Description",
  url = "https://test.com/$id",
  urlToImage = null,
  publishedAt = "2024-01-01T00:00:00Z",
  content = "Test Content"
 )
}