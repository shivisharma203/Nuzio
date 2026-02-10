package com.nuzio.newsapp.data.repository

import com.nuzio.newsapp.data.local.PreferencesDataSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for PreferencesRepositoryImpl validating preference management delegation.
 *
 * These tests verify that the repository correctly delegates all preference operations
 * to the data source without introducing additional logic or transformations. The tests
 * mock the PreferencesDataSource to isolate repository behavior from actual DataStore
 * persistence operations, enabling rapid unit testing of the repository coordination layer.
 */
class PreferencesRepositoryImplTest {

    private lateinit var dataSource: PreferencesDataSource
    private lateinit var repository: PreferencesRepositoryImpl

    @Before
    fun setup() {
        dataSource = mockk(relaxed = true)
        repository = PreferencesRepositoryImpl(dataSource)
    }

    @Test
    fun themeModeFlowDelegatesToDataSource() = runTest {
        every { dataSource.themeMode } returns flowOf("dark")

        val themeMode = repository.themeMode.first()

        assertEquals("dark", themeMode)
    }

    @Test
    fun notificationsEnabledFlowDelegatesToDataSource() = runTest {
        every { dataSource.notificationsEnabled } returns flowOf(true)

        val enabled = repository.notificationsEnabled.first()

        assertTrue(enabled)
    }

    @Test
    fun selectedCountryFlowDelegatesToDataSource() = runTest {
        every { dataSource.selectedCountry } returns flowOf("gb")

        val country = repository.selectedCountry.first()

        assertEquals("gb", country)
    }

    @Test
    fun selectedCategoryFlowDelegatesToDataSource() = runTest {
        every { dataSource.selectedCategory } returns flowOf("technology")

        val category = repository.selectedCategory.first()

        assertEquals("technology", category)
    }

    @Test
    fun onboardingCompletedFlowDelegatesToDataSource() = runTest {
        every { dataSource.onboardingCompleted } returns flowOf(true)

        val completed = repository.onboardingCompleted.first()

        assertTrue(completed)
    }

    @Test
    fun offlineModeEnabledFlowDelegatesToDataSource() = runTest {
        every { dataSource.offlineModeEnabled } returns flowOf(false)

        val enabled = repository.offlineModeEnabled.first()

        assertFalse(enabled)
    }

    @Test
    fun setThemeModeDelegatesToDataSource() = runTest {
        coEvery { dataSource.setThemeMode(any()) } returns Unit

        repository.setThemeMode("light")

        coVerify { dataSource.setThemeMode("light") }
    }

    @Test
    fun setNotificationsEnabledDelegatesToDataSource() = runTest {
        coEvery { dataSource.setNotificationsEnabled(any()) } returns Unit

        repository.setNotificationsEnabled(false)

        coVerify { dataSource.setNotificationsEnabled(false) }
    }

    @Test
    fun setSelectedCountryDelegatesToDataSource() = runTest {
        coEvery { dataSource.setSelectedCountry(any()) } returns Unit

        repository.setSelectedCountry("fr")

        coVerify { dataSource.setSelectedCountry("fr") }
    }

    @Test
    fun setSelectedCategoryDelegatesToDataSource() = runTest {
        coEvery { dataSource.setSelectedCategory(any()) } returns Unit

        repository.setSelectedCategory("sports")

        coVerify { dataSource.setSelectedCategory("sports") }
    }

    @Test
    fun setSelectedCategoryWithNullDelegatesToDataSource() = runTest {
        coEvery { dataSource.setSelectedCategory(null) } returns Unit

        repository.setSelectedCategory(null)

        coVerify { dataSource.setSelectedCategory(null) }
    }

    @Test
    fun setOnboardingCompletedDelegatesToDataSource() = runTest {
        coEvery { dataSource.setOnboardingCompleted() } returns Unit

        repository.setOnboardingCompleted()

        coVerify { dataSource.setOnboardingCompleted() }
    }

    @Test
    fun setOfflineModeEnabledDelegatesToDataSource() = runTest {
        coEvery { dataSource.setOfflineModeEnabled(any()) } returns Unit

        repository.setOfflineModeEnabled(true)

        coVerify { dataSource.setOfflineModeEnabled(true) }
    }

    @Test
    fun clearAllPreferencesDelegatesToDataSource() = runTest {
        coEvery { dataSource.clearAllPreferences() } returns Unit

        repository.clearAllPreferences()

        coVerify { dataSource.clearAllPreferences() }
    }

    @Test
    fun multipleDifferentPreferencesCanBeSetSequentially() = runTest {
        coEvery { dataSource.setThemeMode(any()) } returns Unit
        coEvery { dataSource.setNotificationsEnabled(any()) } returns Unit
        coEvery { dataSource.setSelectedCountry(any()) } returns Unit

        repository.setThemeMode("dark")
        repository.setNotificationsEnabled(true)
        repository.setSelectedCountry("us")

        coVerify { dataSource.setThemeMode("dark") }
        coVerify { dataSource.setNotificationsEnabled(true) }
        coVerify { dataSource.setSelectedCountry("us") }
    }
}