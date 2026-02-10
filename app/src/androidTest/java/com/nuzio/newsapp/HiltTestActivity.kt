package com.nuzio.newsapp

import androidx.activity.ComponentActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * Test activity for Hilt-enabled Compose UI tests.
 *
 * This activity serves as the host for Compose content during instrumented tests
 * that require Hilt dependency injection. The @AndroidEntryPoint annotation
 * triggers Hilt's code generation to create the necessary component implementations
 * that enable ViewModel injection within test scenarios.
 *
 * The activity resides in the debug source set to ensure it remains available
 * during test execution while excluding it from production release builds.
 */
@AndroidEntryPoint
class HiltTestActivity : ComponentActivity()