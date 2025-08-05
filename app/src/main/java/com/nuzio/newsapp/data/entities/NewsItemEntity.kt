
package com.nuzio.newsapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "news_items")
data class NewsItemEntity(
  @PrimaryKey val link: String,
  val title: String,
  val thumbnail: String?,
  val pubDate: String
)
