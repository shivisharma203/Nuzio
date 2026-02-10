package com.nuzio.newsapp.data.remote.dto

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class MappersTest {

    @Test
    fun `ArticleDto toDomain creates valid NewsArticle`() {
        val dto = ArticleDto(
            source = SourceDto(id = "test-source", name = "Test Source"),
            author = "Test Author",
            title = "Test Title",
            description = "Test Description",
            url = "https://test.com",
            urlToImage = "https://test.com/image.jpg",
            publishedAt = "2024-01-01T00:00:00Z",
            content = "Test Content"
        )

        val article = dto.toDomain()

        assertNotNull(article.id)
        assertEquals("Test Source", article.source.name)
        assertEquals("test-source", article.source.id)
        assertEquals("Test Author", article.author)
        assertEquals("Test Title", article.title)
        assertEquals("Test Description", article.description)
        assertEquals("https://test.com", article.url)
        assertEquals("https://test.com/image.jpg", article.urlToImage)
        assertEquals("2024-01-01T00:00:00Z", article.publishedAt)
        assertEquals("Test Content", article.content)
    }

    @Test
    fun `ArticleDto with null fields creates valid NewsArticle`() {
        val dto = ArticleDto(
            source = SourceDto(id = null, name = "Test Source"),
            author = null,
            title = "Test Title",
            description = null,
            url = "https://test.com",
            urlToImage = null,
            publishedAt = "2024-01-01T00:00:00Z",
            content = null
        )

        val article = dto.toDomain()

        assertNotNull(article.id)
        assertEquals("Test Source", article.source.name)
        assertEquals(null, article.source.id)
        assertEquals(null, article.author)
        assertEquals(null, article.description)
    }

    @Test
    fun `NewsResponseDto toDomain converts all articles`() {
        val dto = NewsResponseDto(
            status = "ok",
            totalResults = 2,
            articles = listOf(
                ArticleDto(
                    source = SourceDto(id = "1", name = "Source 1"),
                    author = "Author 1",
                    title = "Title 1",
                    description = "Description 1",
                    url = "https://test1.com",
                    urlToImage = null,
                    publishedAt = "2024-01-01T00:00:00Z",
                    content = "Content 1"
                ),
                ArticleDto(
                    source = SourceDto(id = "2", name = "Source 2"),
                    author = "Author 2",
                    title = "Title 2",
                    description = "Description 2",
                    url = "https://test2.com",
                    urlToImage = null,
                    publishedAt = "2024-01-02T00:00:00Z",
                    content = "Content 2"
                )
            )
        )

        val articles = dto.toDomain()

        assertEquals(2, articles.size)
        assertEquals("Title 1", articles[0].title)
        assertEquals("Title 2", articles[1].title)
    }

    @Test
    fun `List ArticleDto toDomain handles empty list`() {
        val dtos = emptyList<ArticleDto>()

        val articles = dtos.toDomain()

        assertEquals(0, articles.size)
    }
}