package com.nuzio.newsapp.features.news.list

import androidx.lifecycle.viewModelScope
import com.nuzio.newsapp.core.network.Resource
import com.nuzio.newsapp.core.ui.BaseViewModel
import com.nuzio.newsapp.domain.model.NewsArticle
import com.nuzio.newsapp.domain.usecase.GetTopHeadlinesUseCase
import com.nuzio.newsapp.domain.usecase.SearchNewsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel managing news list screen state with section-based navigation.
 *
 * Coordinates between use cases and UI state, managing section switching,
 * search functionality, and refresh operations. Each section maintains
 * independent loading states for optimized user experience.
 */
@HiltViewModel
class NewsListViewModel @Inject constructor(
    private val getTopHeadlinesUseCase: GetTopHeadlinesUseCase,
    private val searchNewsUseCase: SearchNewsUseCase
) : BaseViewModel<NewsListUiState>(NewsListUiState()) {

    private var searchJob: Job? = null
    private val sectionCache = mutableMapOf<NewsSection, List<NewsArticle>>()

    /**
     * Handles all user events from the UI.
     */
    fun onEvent(event: NewsListEvent) {
        Timber.d("🎯 News List Event: ${event::class.simpleName}")

        when (event) {
            is NewsListEvent.LoadNews -> {
                loadTopHeadlines()
            }

            is NewsListEvent.Refresh -> {
                refreshCurrentSection()
            }

            is NewsListEvent.Retry -> {
                setState { copy(errorMessage = null) }
                loadTopHeadlines()
            }

            is NewsListEvent.SectionChanged -> {
                handleSectionChange(event.section)
            }

            is NewsListEvent.Search -> {
                handleSearch(event.query)
            }

            is NewsListEvent.ClearSearch -> {
                handleClearSearch()
            }

            is NewsListEvent.ArticleClick -> {
                Timber.d("📄 Article clicked: ${event.article.title}")
                // Navigation handled in screen composable
            }

            is NewsListEvent.BookmarkArticle -> {
                // TODO: Implement bookmark functionality in future iteration
                Timber.d("🔖 Bookmark requested: ${event.article.title}")
            }
        }
    }

    /**
     * Loads top headlines for the current section.
     */
    private fun loadTopHeadlines() {
        val currentSection = currentState().currentSection

        // Check if section is already cached
        val cachedArticles = sectionCache[currentSection]
        if (cachedArticles != null) {
            Timber.d("💾 Loading ${currentSection.displayName} from memory cache")
            setState {
                copy(
                    articles = cachedArticles,
                    isLoading = false,
                    errorMessage = null,
                    isEmpty = cachedArticles.isEmpty()
                )
            }
            return
        }

        setState {
            copy(
                isLoading = true,
                errorMessage = null,
                loadingSections = loadingSections + currentSection
            )
        }

        launchState {
            when (val result = getTopHeadlinesUseCase(section = currentSection)) {
                is Resource.Success -> {
                    Timber.d("✅ Successfully loaded ${result.data.size} headlines for ${currentSection.displayName}")

                    // Cache in memory for quick section switching
                    sectionCache[currentSection] = result.data

                    setState {
                        copy(
                            articles = result.data,
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = null,
                            isEmpty = result.data.isEmpty(),
                            loadingSections = loadingSections - currentSection
                        )
                    }
                }

                is Resource.Error -> {
                    Timber.e(result.exception, "❌ Failed to load headlines for ${currentSection.displayName}")
                    setState {
                        copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = result.message,
                            loadingSections = loadingSections - currentSection
                        )
                    }
                }

                is Resource.Loading -> {
                    // Already handled in initial state update
                }
            }
        }
    }

    /**
     * Refreshes articles for the current section (pull-to-refresh).
     */
    private fun refreshCurrentSection() {
        val currentSection = currentState().currentSection

        // Clear memory cache for this section to force fresh fetch
        sectionCache.remove(currentSection)

        setState {
            copy(
                isRefreshing = true,
                errorMessage = null
            )
        }

        launchState {
            when (val result = getTopHeadlinesUseCase(section = currentSection)) {
                is Resource.Success -> {
                    Timber.d("✅ Successfully refreshed ${currentSection.displayName}")
                    sectionCache[currentSection] = result.data

                    setState {
                        copy(
                            articles = result.data,
                            isRefreshing = false,
                            errorMessage = null,
                            isEmpty = result.data.isEmpty()
                        )
                    }
                }

                is Resource.Error -> {
                    Timber.e(result.exception, "❌ Failed to refresh ${currentSection.displayName}")
                    setState {
                        copy(
                            isRefreshing = false,
                            errorMessage = result.message
                        )
                    }
                }

                is Resource.Loading -> {
                    // Already handled
                }
            }
        }
    }

    /**
     * Handles section tab change.
     */
    private fun handleSectionChange(newSection: NewsSection) {
        if (newSection == currentState().currentSection) {
            Timber.d("🔄 Section ${newSection.displayName} already selected")
            return
        }

        Timber.d("📑 Switching to section: ${newSection.displayName}")

        // Update current section
        setState { copy(currentSection = newSection) }

        // Load articles for new section
        loadTopHeadlines()
    }

    /**
     * Handles search with debouncing (500ms delay).
     */
    private fun handleSearch(query: String) {
        // Cancel previous search job
        searchJob?.cancel()

        setState {
            copy(
                searchQuery = query,
                isSearchActive = query.isNotBlank()
            )
        }

        if (query.isBlank()) {
            // Restore current section articles
            setState {
                copy(
                    articles = sectionCache[currentSection] ?: emptyList(),
                    isLoading = false,
                    errorMessage = null
                )
            }
            return
        }

        // Debounce search - wait 500ms before executing
        searchJob = viewModelScope.launch {
            delay(500)
            executeSearch(query)
        }
    }

    /**
     * Executes the search query.
     */
    private fun executeSearch(query: String) {
        setState { copy(isLoading = true, errorMessage = null) }

        launchState {
            when (val result = searchNewsUseCase(query = query)) {
                is Resource.Success -> {
                    Timber.d("✅ Search completed: ${result.data.size} results for '$query'")
                    setState {
                        copy(
                            articles = result.data,
                            isLoading = false,
                            errorMessage = null,
                            isEmpty = result.data.isEmpty()
                        )
                    }
                }

                is Resource.Error -> {
                    Timber.e(result.exception, "❌ Search failed for '$query'")
                    setState {
                        copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }

                is Resource.Loading -> {
                    // Already handled
                }
            }
        }
    }

    /**
     * Clears search and returns to section view.
     */
    private fun handleClearSearch() {
        searchJob?.cancel()

        setState {
            copy(
                searchQuery = "",
                isSearchActive = false,
                articles = sectionCache[currentSection] ?: emptyList(),
                errorMessage = null
            )
        }

        // Reload current section if cache is empty
        if (sectionCache[currentState().currentSection] == null) {
            loadTopHeadlines()
        }
    }
}