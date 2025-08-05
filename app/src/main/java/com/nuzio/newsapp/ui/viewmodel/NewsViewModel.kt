
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
sealed class UiState {
    object Loading : UiState()
    data class Success(val newsList: List<NewsItem>) : UiState()
    data class Error(val message: String) : UiState()
    object Empty : UiState()
}

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        refreshNews()
    }
    fun refreshNews() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val news = repository.getNews()
                if (news.isEmpty()) {
                    _uiState.value = UiState.Empty
                } else {
                    _uiState.value = UiState.Success(news)
                }
            } catch (e: Exception) {
                val message = when (e) {
                    is java.net.UnknownHostException -> "No internet connection."
                    else -> e.localizedMessage ?: "An unexpected error occurred."
                }
                _uiState.value = UiState.Error(message)
            }
        }
    }
}
