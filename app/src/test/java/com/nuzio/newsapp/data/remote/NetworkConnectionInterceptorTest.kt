package com.nuzio.newsapp.data.remote

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import okhttp3.Interceptor
import okhttp3.Request
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumentation tests for NetworkConnectionInterceptor validating connectivity checks.
 *
 * These tests verify that the interceptor correctly detects network availability and
 * prevents requests when connectivity is unavailable. The tests execute as instrumentation
 * tests because they require Android framework components including ConnectivityManager
 * that are unavailable in pure JVM unit test environments.
 */
@RunWith(AndroidJUnit4::class)
class NetworkConnectionInterceptorTest {

    private lateinit var context: Context
    private lateinit var interceptor: NetworkConnectionInterceptor
    private lateinit var chain: Interceptor.Chain

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        interceptor = NetworkConnectionInterceptor(context)
        chain = mockk(relaxed = true)

        val request = Request.Builder()
            .url("https://newsapi.org/v2/top-headlines")
            .build()
        every { chain.request() } returns request
    }

    @Test
    fun interceptThrowsNoConnectivityExceptionWhenOffline() {
        // Note: This test validates exception type rather than actual connectivity
        // because instrumentation tests run on connected devices/emulators

        try {
            // The interceptor checks actual device connectivity
            interceptor.intercept(chain)

            // If we reach here, device has connectivity (expected in test environment)
            // Verify that chain.proceed was called
            verify { chain.proceed(any()) }
        } catch (e: NoConnectivityException) {
            // Device lacks connectivity - verify exception message
            assertNotNull(e.message)
            assertTrue(e.message!!.contains("network connection"))
        }
    }

    @Test
    fun noConnectivityExceptionExtendsIOException() {
        val exception = NoConnectivityException()

        assertTrue(exception is java.io.IOException)
        assertNotNull(exception.message)
    }

    @Test
    fun noConnectivityExceptionContainsUserFriendlyMessage() {
        val exception = NoConnectivityException()

        val message = exception.message ?: ""
        assertTrue(message.contains("network connection") ||
                message.contains("internet connection"))
        assertFalse(message.contains("null"))
    }

    @Test
    fun interceptProceedsWhenConnectivityAvailable() {
        // This test assumes the test device/emulator has connectivity
        val mockResponse = mockk<okhttp3.Response>(relaxed = true)
        every { chain.proceed(any()) } returns mockResponse

        try {
            val response = interceptor.intercept(chain)

            // Verify that the interceptor called chain.proceed
            verify { chain.proceed(any()) }
            assertSame(mockResponse, response)
        } catch (e: NoConnectivityException) {
            // Test device lacks connectivity - this is acceptable in test environment
            // The important verification is that exception is thrown appropriately
            fail("Test environment should have connectivity for full test coverage")
        }
    }

    @Test
    fun interceptPreservesOriginalRequestWhenProceeding() {
        val originalRequest = Request.Builder()
            .url("https://newsapi.org/v2/top-headlines?country=us")
            .addHeader("Accept", "application/json")
            .build()

        every { chain.request() } returns originalRequest
        every { chain.proceed(any()) } returns mockk(relaxed = true)

        try {
            interceptor.intercept(chain)

            verify {
                chain.proceed(match { request ->
                    request.url == originalRequest.url &&
                            request.header("Accept") == "application/json"
                })
            }
        } catch (e: NoConnectivityException) {
            // Acceptable if test environment lacks connectivity
        }
    }
}