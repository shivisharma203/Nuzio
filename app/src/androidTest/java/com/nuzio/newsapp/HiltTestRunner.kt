package com.nuzio.newsapp

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

/**
 * Custom test runner for Hilt-enabled instrumented tests.
 *
 * This test runner extends AndroidJUnitRunner to override the application
 * instantiation logic, providing HiltTestApplication instead of the production
 * NuzioNewsApplication during test execution. This substitution enables Hilt
 * to generate test-specific dependency injection components that remain isolated
 * from production application initialization logic.
 *
 * The test runner must be registered in the build configuration as the
 * instrumentation test runner to ensure all Hilt tests execute within the
 * proper test application context.
 */
class HiltTestRunner : AndroidJUnitRunner() {

    override fun newApplication(
        classLoader: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(classLoader, HiltTestApplication::class.java.name, context)
    }
}