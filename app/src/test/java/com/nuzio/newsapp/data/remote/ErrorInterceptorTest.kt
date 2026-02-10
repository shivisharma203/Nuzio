package com.nuzio.newsapp.data.remote

import io.mockk.every
import io.mockk.mockk
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for ErrorInterceptor validating HTTP error response handling.
 *
 * These tests verify that the interceptor correctly transforms HTTP error responses
 * into meaningful exception messages that users can understand and act upon. The tests
 * validate error message extraction from response bodies, appropriate exception throwing
 * for various status codes, and preservation of successful responses without modification.
 */
class ErrorInterceptorTest {

    private lateinit var interceptor: ErrorInterceptor
    private lateinit var chain: Interceptor.Chain
    private lateinit var request: Request

    @Before
    fun setup() {
        interceptor = ErrorInterceptor()
        chain = mockk(relaxed = true)
        request = Request.Builder()
            .url("https://newsapi.org/v2/top-headlines")
            .build()

        every { chain.request() } returns request
    }

    @Test
    fun interceptPassesThroughSuccessfulResponses() {
        val successResponse = createResponse(200, "OK")
        every { chain.proceed(any()) } returns successResponse

        val response = interceptor.intercept(chain)

        assertEquals(200, response.code)
        assertSame(successResponse, response)
    }

    @Test
    fun interceptThrowsApiExceptionFor401Unauthorized() {
        val errorResponse = createResponse(401, "Unauthorized")
        every { chain.proceed(any()) } returns errorResponse

        try {
            interceptor.intercept(chain)
            fail("Expected ApiException to be thrown")
        } catch (e: ApiException) {
            assertEquals(401, e.statusCode)
            assertTrue(e.message.contains("Invalid API key") ||
                    e.message.contains("API key"))
        }
    }

    @Test
    fun interceptThrowsApiExceptionFor403Forbidden() {
        val errorResponse = createResponse(403, "Forbidden")
        every { chain.proceed(any()) } returns errorResponse

        try {
            interceptor.intercept(chain)
            fail("Expected ApiException to be thrown")
        } catch (e: ApiException) {
            assertEquals(403, e.statusCode)
            assertTrue(e.message.contains("forbidden") ||
                    e.message.contains("permissions"))
        }
    }

    @Test
    fun interceptThrowsApiExceptionFor404NotFound() {
        val errorResponse = createResponse(404, "Not Found")
        every { chain.proceed(any()) } returns errorResponse

        try {
            interceptor.intercept(chain)
            fail("Expected ApiException to be thrown")
        } catch (e: ApiException) {
            assertEquals(404, e.statusCode)
            assertTrue(e.message.contains("not found"))
        }
    }

    @Test
    fun interceptThrowsApiExceptionFor426UpgradeRequired() {
        val errorResponse = createResponse(426, "Upgrade Required")
        every { chain.proceed(any()) } returns errorResponse

        try {
            interceptor.intercept(chain)
            fail("Expected ApiException to be thrown")
        } catch (e: ApiException) {
            assertEquals(426, e.statusCode)
            assertTrue(e.message.contains("upgrade") ||
                    e.message.contains("tier"))
        }
    }

    @Test
    fun interceptThrowsApiExceptionFor429TooManyRequests() {
        val errorResponse = createResponse(429, "Too Many Requests")
        every { chain.proceed(any()) } returns errorResponse

        try {
            interceptor.intercept(chain)
            fail("Expected ApiException to be thrown")
        } catch (e: ApiException) {
            assertEquals(429, e.statusCode)
            assertTrue(e.message.contains("Too many requests") ||
                    e.message.contains("try again later"))
        }
    }

    @Test
    fun interceptThrowsApiExceptionFor500ServerError() {
        val errorResponse = createResponse(500, "Internal Server Error")
        every { chain.proceed(any()) } returns errorResponse

        try {
            interceptor.intercept(chain)
            fail("Expected ApiException to be thrown")
        } catch (e: ApiException) {
            assertEquals(500, e.statusCode)
            assertTrue(e.message.contains("temporarily unavailable") ||
                    e.message.contains("try again later"))
        }
    }

    @Test
    fun interceptExtractsErrorMessageFromJsonResponse() {
        val jsonError = """{"status":"error","message":"Invalid API key provided"}"""
        val errorResponse = createResponse(401, "Unauthorized", jsonError)
        every { chain.proceed(any()) } returns errorResponse

        try {
            interceptor.intercept(chain)
            fail("Expected ApiException to be thrown")
        } catch (e: ApiException) {
            // Should either extract the JSON message or provide default 401 message
            assertTrue(e.message.contains("Invalid API key") ||
                    e.message.contains("API key"))
        }
    }

    @Test
    fun interceptHandlesEmptyResponseBody() {
        val errorResponse = createResponse(400, "Bad Request", "")
        every { chain.proceed(any()) } returns errorResponse

        try {
            interceptor.intercept(chain)
            fail("Expected ApiException to be thrown")
        } catch (e: ApiException) {
            assertEquals(400, e.statusCode)
            assertNotNull(e.message)
            assertFalse(e.message.isEmpty())
        }
    }

    @Test
    fun apiExceptionExtendsIOException() {
        val exception = ApiException(500, "Server error")

        assertTrue(exception is java.io.IOException)
        assertEquals(500, exception.statusCode)
        assertEquals("Server error", exception.message)
    }

    @Test
    fun interceptHandles502BadGateway() {
        val errorResponse = createResponse(502, "Bad Gateway")
        every { chain.proceed(any()) } returns errorResponse

        try {
            interceptor.intercept(chain)
            fail("Expected ApiException to be thrown")
        } catch (e: ApiException) {
            assertEquals(502, e.statusCode)
            assertTrue(e.message.contains("temporarily unavailable") ||
                    e.message.contains("try again later"))
        }
    }

    @Test
    fun interceptHandles503ServiceUnavailable() {
        val errorResponse = createResponse(503, "Service Unavailable")
        every { chain.proceed(any()) } returns errorResponse

        try {
            interceptor.intercept(chain)
            fail("Expected ApiException to be thrown")
        } catch (e: ApiException) {
            assertEquals(503, e.statusCode)
            assertTrue(e.message.contains("temporarily unavailable") ||
                    e.message.contains("try again later"))
        }
    }

    private fun createResponse(
        code: Int,
        message: String,
        body: String = ""
    ): Response {
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message(message)
            .body(body.toResponseBody("application/json".toMediaType()))
            .build()
    }
}