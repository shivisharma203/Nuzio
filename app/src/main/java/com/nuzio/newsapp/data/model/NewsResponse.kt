
package com.nuzio.newsapp.data.model
import com.squareup.moshi.Json

/*
data class NewsResponse(
    @SerializedName("status") val status: String,
    @SerializedName("feed") val feed: Feed,
    @SerializedName("items") val items: List<NewsItemEntity>
)

data class Feed(
    @SerializedName("url") val url: String,
    @SerializedName("title") val title: String,
    @SerializedName("link") val link: String,
    @SerializedName("author") val author: String,
    @SerializedName("description") val description: String,
    @SerializedName("image") val image: String
)

data class NewsItem(
    @SerializedName("title") val title: String,
    @SerializedName("pubDate") val pubDate: String,
    @SerializedName("link") val link: String,
    @SerializedName("author") val author: String,
    @SerializedName("thumbnail") val thumbnail: String,
    @SerializedName("description") val description: String,
    @SerializedName("content") val content: String,
    @SerializedName("enclosure") val enclosure: Enclosure,
    @SerializedName("categories") val categories: List<String>
)

data class Enclosure(
    @SerializedName("link") val link: String
)*/

data class NewsResponse(
    @field:Json(name = "status")
    val status: String,

    @field:Json(name = "feed")
    val feed: Feed,

    @field:Json(name = "items")
    val items: List<NewsItem>
)

data class Feed(
    @field:Json(name = "url")
    val url: String,

    @field:Json(name = "title")
    val title: String
)

data class NewsItem(
    @field:Json(name = "title")
    val title: String,

    @field:Json(name = "link")
    val link: String,

    @field:Json(name = "thumbnail")
    val thumbnail: String?,

    @field:Json(name = "pubDate")
    val pubDate: String
)