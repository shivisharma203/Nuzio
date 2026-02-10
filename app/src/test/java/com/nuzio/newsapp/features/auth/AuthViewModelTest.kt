package com.nuzio.newsapp.features.auth

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.google.firebase.auth.FirebaseAuth
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

 @get:Rule
 val instantExecutorRule = InstantTaskExecutorRule()

 private val testDispatcher = StandardTestDispatcher()

 private lateinit var application: Application
 private lateinit var firebaseAuth: FirebaseAuth
 private lateinit var viewModel: AuthViewModel

 @Before
 fun setup() {
  Dispatchers.setMain(testDispatcher)

  // Mock Android Application context
  application = mockk(relaxed = true)
  every { application.getString(any()) } returns "mock_client_id"

  // Mock FirebaseAuth static getInstance
  firebaseAuth = mockk(relaxed = true)
  every { firebaseAuth.currentUser } returns null
  every { firebaseAuth.addAuthStateListener(any()) } just Runs
  every { firebaseAuth.removeAuthStateListener(any()) } just Runs

  mockkStatic(FirebaseAuth::class)
  every { FirebaseAuth.getInstance() } returns firebaseAuth

  viewModel = AuthViewModel(application)
 }

 @After
 fun teardown() {
  Dispatchers.resetMain()
  unmockkAll()
 }

 @Test
 fun `initial state has empty credentials`() = runTest {
  viewModel.uiState.test {
   val state = awaitItem()
   assertEquals("", state.email)
   assertEquals("", state.password)
   assertTrue(state.isLoginMode)
   assertFalse(state.isLoading)
   assertFalse(state.isAuthenticated)
   cancelAndIgnoreRemainingEvents()
  }
 }

 @Test
 fun `EmailChanged event updates email in state`() = runTest {
  viewModel.onEvent(AuthEvent.EmailChanged("test@example.com"))
  testDispatcher.scheduler.advanceUntilIdle()

  viewModel.uiState.test {
   val state = awaitItem()
   assertEquals("test@example.com", state.email)
   cancelAndIgnoreRemainingEvents()
  }
 }

 @Test
 fun `PasswordChanged event updates password in state`() = runTest {
  viewModel.onEvent(AuthEvent.PasswordChanged("password123"))
  testDispatcher.scheduler.advanceUntilIdle()

  viewModel.uiState.test {
   val state = awaitItem()
   assertEquals("password123", state.password)
   cancelAndIgnoreRemainingEvents()
  }
 }

 @Test
 fun `ToggleAuthMode event switches between login and signup`() = runTest {
  val initialState = viewModel.uiState.value
  assertTrue(initialState.isLoginMode)

  viewModel.onEvent(AuthEvent.ToggleAuthMode)
  testDispatcher.scheduler.advanceUntilIdle()

  val toggledState = viewModel.uiState.value
  assertFalse(toggledState.isLoginMode)
 }

 @Test
 fun `isFormValid returns false when email is blank`() = runTest {
  viewModel.onEvent(AuthEvent.EmailChanged(""))
  viewModel.onEvent(AuthEvent.PasswordChanged("password"))
  testDispatcher.scheduler.advanceUntilIdle()

  val state = viewModel.uiState.value
  assertFalse(state.isFormValid())
 }

 @Test
 fun `isFormValid returns false when password is blank`() = runTest {
  viewModel.onEvent(AuthEvent.EmailChanged("test@example.com"))
  viewModel.onEvent(AuthEvent.PasswordChanged(""))
  testDispatcher.scheduler.advanceUntilIdle()

  val state = viewModel.uiState.value
  assertFalse(state.isFormValid())
 }

 @Test
 fun `isFormValid returns true when both fields filled`() = runTest {
  viewModel.onEvent(AuthEvent.EmailChanged("test@example.com"))
  viewModel.onEvent(AuthEvent.PasswordChanged("password123"))
  testDispatcher.scheduler.advanceUntilIdle()

  val state = viewModel.uiState.value
  assertTrue(state.isFormValid())
 }
}