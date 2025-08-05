package com.nuzio.newsapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nuzio.newsapp.data.NewsRepository
import com.nuzio.newsapp.data.model.NewsItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Represents the UI state of the news screen.
 */
sealed class UiState {
    /** Represents a loading state when data is being fetched. */
    object Loading : UiState()

    /** Represents the successful loading of a non-empty list of news items. */
    data class Success(val newsList: List<NewsItem>) : UiState()

    /** Represents an error state with a message describing the failure. */
    data class Error(val message: String) : UiState()

    /** Represents the state when no news items are available. */
    object Empty : UiState()
}

/**
 * ViewModel responsible for fetching and exposing news data to the UI.
 *
 * @property repository The repository that provides news data.
 */
@HiltViewModel
class NewsViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {

    // Backing mutable state flow for UI state
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)

    /** Immutable UI state exposed to observers */
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        refreshNews()
    }

    /**
     * Fetches the latest news from the repository and updates [uiState].
     * Handles loading, success, empty, and error states.
     */
    fun refreshNews() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val news = repository.getNews()
                _uiState.value = if (news.isEmpty()) UiState.Empty else UiState.Success(news)
            } catch (e: Exception) {
                val message = when (e) {
                    is java.net.UnknownHostException -> "No internet connection."
                    else -> e.localizedMessage ?: "An unexpected error occurred."
                }
                // TODO: Add logging here if desired
                _uiState.value = UiState.Error(message)
            }
        }
    }
}
