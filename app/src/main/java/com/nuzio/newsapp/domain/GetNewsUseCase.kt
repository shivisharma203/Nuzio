
package com.nuzio.newsapp.domain

import com.nuzio.newsapp.data.NewsRepository
import javax.inject.Inject

class GetNewsUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    // TODO: Implement method to get news articles
}
