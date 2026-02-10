package com.nuzio.newsapp.data.remote

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for AuthInterceptor validating API key injection behavior.
 *
 * These tests verify that the interceptor correctly adds authentication credentials
 * to outgoing requests while preserving existing query parameters and request headers.
 * The tests execute on the JVM without Android dependencies, providing rapid feedback
 * during development and ensuring that authentication logic remains consistent across
 * all network operations.
 */
class AuthInterceptorTest {

    private lateinit var interceptor: AuthInterceptor
    private lateinit var chain: Interceptor.Chain
    private lateinit var mockResponse: Response

    @Before
    fun setup() {
        interceptor = AuthInterceptor()
        chain = mockk(relaxed = true)
        mockResponse = mockk(relaxed = true)

        every { chain.proceed(any()) } returns mockResponse
    }

    @Test
    fun interceptAddsApiKeyToRequestUrl() {
        val originalRequest = Request.Builder()
            .url("https://newsapi.org/v2/top-headlines")
            .build()

        every { chain.request() } returns originalRequest

        interceptor.intercept(chain)

        verify {
            chain.proceed(match { request ->
                val apiKey = request.url.queryParameter("apiKey")
                apiKey != null && apiKey.isNotEmpty()
            })
        }
    }

    @Test
    fun interceptPreservesExistingQueryParameters() {
        val originalRequest = Request.Builder()
            .url("https://newsapi.org/v2/top-headlines?country=us&category=technology")
            .build()

        every { chain.request() } returns originalRequest

        interceptor.intercept(chain)

        verify {
            chain.proceed(match { request ->
                request.url.queryParameter("country") == "us" &&
                        request.url.queryParameter("category") == "technology" &&
                        request.url.queryParameter("apiKey") != null
            })
        }
    }

    @Test
    fun interceptAddsApiKeyToSearchEndpoint() {
        val originalRequest = Request.Builder()
            .url("https://newsapi.org/v2/everything?q=bitcoin")
            .build()

        every { chain.request() } returns originalRequest

        interceptor.intercept(chain)

        verify {
            chain.proceed(match { request ->
                request.url.queryParameter("q") == "bitcoin" &&
                        request.url.queryParameter("apiKey") != null
            })
        }
    }

    @Test
    fun interceptPreservesRequestMethod() {
        val originalRequest = Request.Builder()
            .url("https://newsapi.org/v2/top-headlines")
            .get()
            .build()

        every { chain.request() } returns originalRequest

        interceptor.intercept(chain)

        verify {
            chain.proceed(match { request ->
                request.method == "GET"
            })
        }
    }

    @Test
    fun interceptPreservesRequestHeaders() {
        val originalRequest = Request.Builder()
            .url("https://newsapi.org/v2/top-headlines")
            .addHeader("Accept", "application/json")
            .addHeader("User-Agent", "Nuzio/1.0")
            .build()

        every { chain.request() } returns originalRequest

        interceptor.intercept(chain)

        verify {
            chain.proceed(match { request ->
                request.header("Accept") == "application/json" &&
                        request.header("User-Agent") == "Nuzio/1.0"
            })
        }
    }

    @Test
    fun interceptReturnsResponseFromChain() {
        val originalRequest = Request.Builder()
            .url("https://newsapi.org/v2/top-headlines")
            .build()

        every { chain.request() } returns originalRequest

        val response = interceptor.intercept(chain)

        assertSame(mockResponse, response)
    }

    @Test
    fun interceptHandlesUrlsWithFragments() {
        val originalRequest = Request.Builder()
            .url("https://newsapi.org/v2/top-headlines#section")
            .build()

        every { chain.request() } returns originalRequest

        interceptor.intercept(chain)

        verify {
            chain.proceed(match { request ->
                request.url.fragment == "section" &&
                        request.url.queryParameter("apiKey") != null
            })
        }
    }

    @Test
    fun interceptAddsApiKeyOnlyOnce() {
        val originalRequest = Request.Builder()
            .url("https://newsapi.org/v2/top-headlines")
            .build()

        every { chain.request() } returns originalRequest

        interceptor.intercept(chain)

        verify {
            chain.proceed(match { request ->
                val allApiKeys = request.url.queryParameterNames.count { it == "apiKey" }
                allApiKeys == 1
            })
        }
    }
}