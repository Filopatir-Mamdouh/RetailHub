package com.iti4.retailhub.features.login_and_signup.viewmodel

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.iti4.retailhub.GetCustomerFavoritesQuery
import com.iti4.retailhub.datastorage.IRepository
import com.iti4.retailhub.datastorage.network.ApiState
import com.iti4.retailhub.features.favorits.viewmodel.FavoritesViewModel
import com.iti4.retailhub.userauthuntication.AuthState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock


class UserAuthunticationViewModelTest{
    private lateinit var viewModel: UserAuthunticationViewModel
    private val repository = mockk<IRepository>()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = UserAuthunticationViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getFavorites emits loading and success state`() = runTest {
        coEvery { repository.createUserWithEmailAndPassword(any(),any()) } returns AuthResult?

        var collectedState = mutableListOf<AuthState>()

        val completionSignal = CompletableDeferred<Unit>()

        val job = launch {
            viewModel.authState.collect {
                collectedState.add(it)
                completionSignal.complete(Unit)
            }
        }

        viewModel.createUserWithEmailAndPassword("firstName", "lastName", "email", "password")
        testDispatcher.scheduler.advanceUntilIdle()

        completionSignal.await()

        assertTrue(collectedState[0] is AuthState.Loading)
        assertThat(collectedState[1], `is`(instanceOf(AuthState.Success::class.java)))

        val result = collectedState[1] as AuthState.Success
        val retrivedData = result.user
        assertThat(retrivedData, notNullValue())
        job.cancel()
    }
}