package com.nuzio.newsapp.features.news.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.nuzio.newsapp.core.network.Resource
import com.nuzio.newsapp.domain.model.NewsArticle
import com.nuzio.newsapp.domain.usecase.GetTopHeadlinesUseCase
import com.nuzio.newsapp.domain.usecase.SearchNewsUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NewsListViewModelTest {

 @get:Rule
 val instantExecutorRule = InstantTaskExecutorRule()

 private val testDispatcher = StandardTestDispatcher()

 private lateinit var getTopHeadlinesUseCase: GetTopHeadlinesUseCase
 private lateinit var searchNewsUseCase: SearchNewsUseCase
 private lateinit var viewModel: NewsListViewModel

 @Before
 fun setup() {
  Dispatchers.setMain(testDispatcher)
  getTopHeadlinesUseCase = mockk()
  searchNewsUseCase = mockk()

  coEvery { getTopHeadlinesUseCase(any(), any()) } returns Resource.Success(emptyList())

  viewModel = NewsListViewModel(getTopHeadlinesUseCase, searchNewsUseCase)
 }

 @After
 fun teardown() {
  Dispatchers.resetMain()
 }

 @Test
 fun `initial state is correct`() = runTest {
  viewModel.uiState.test {
   val state = awaitItem()
   assertEquals(emptyList<NewsArticle>(), state.articles)
   assertFalse(state.isLoading)
   assertFalse(state.isRefreshing)
   assertEquals(null, state.errorMessage)
   assertFalse(state.isEmpty)
  }
 }

 @Test
 fun `LoadNews event triggers use case and updates state`() = runTest {
  val mockArticles = listOf(createMockArticle("1"))
  coEvery { getTopHeadlinesUseCase(any(), any()) } returns Resource.Success(mockArticles)

  viewModel.onEvent(NewsListEvent.LoadNews)
  testDispatcher.scheduler.advanceUntilIdle()

  viewModel.uiState.test {
   val state = awaitItem()
   assertEquals(1, state.articles.size)
   assertFalse(state.isLoading)
   assertEquals(null, state.errorMessage)
  }
 }

 @Test
 fun `Refresh event sets isRefreshing state`() = runTest {
  val mockArticles = listOf(createMockArticle("1"))
  coEvery { getTopHeadlinesUseCase(any(), any()) } returns Resource.Success(mockArticles)

  viewModel.onEvent(NewsListEvent.Refresh)

  viewModel.uiState.test {
   val state = awaitItem()
   assertFalse(state.isRefreshing)
  }

  testDispatcher.scheduler.advanceUntilIdle()
 }

 @Test
 fun `Search event with query triggers search use case`() = runTest {
  val mockArticles = listOf(createMockArticle("1"))
  coEvery { searchNewsUseCase(any(), any(), any()) } returns Resource.Success(mockArticles)

  viewModel.onEvent(NewsListEvent.Search("bitcoin"))
  testDispatcher.scheduler.advanceUntilIdle()

  coVerify { searchNewsUseCase("bitcoin", any(), any()) }
 }

 @Test
 fun `Error from use case updates state with error message`() = runTest {
  coEvery { getTopHeadlinesUseCase(any(), any()) } returns
          Resource.Error(Exception(), "Network error")

  viewModel.onEvent(NewsListEvent.LoadNews)
  testDispatcher.scheduler.advanceUntilIdle()

  viewModel.uiState.test {
   val state = awaitItem()
   assertEquals("Network error", state.errorMessage)
   assertFalse(state.isLoading)
  }
 }

 @Test
 fun `Empty result sets isEmpty state`() = runTest {
  coEvery { getTopHeadlinesUseCase(any(), any()) } returns Resource.Success(emptyList())

  viewModel.onEvent(NewsListEvent.LoadNews)
  testDispatcher.scheduler.advanceUntilIdle()

  viewModel.uiState.test {
   val state = awaitItem()
   assertTrue(state.isEmpty)
   assertEquals(0, state.articles.size)
  }
 }

 private fun createMockArticle(id: String) = NewsArticle(
  id = id,
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