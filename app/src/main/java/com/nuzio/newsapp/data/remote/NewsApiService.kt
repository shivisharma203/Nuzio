
package com.nuzio.newsapp.data.remote

import com.nuzio.newsapp.data.model.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
        @GET("v1/api.json")
        suspend fun getNews(
            @Query("rss_url") rssUrl: String = "http://www.abc.net.au/news/feed/51120/rss.xml"
        ): NewsResponse

}
