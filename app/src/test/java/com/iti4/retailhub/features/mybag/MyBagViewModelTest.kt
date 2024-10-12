package com.iti4.retailhub.features.mybag

import com.iti4.retailhub.datastorage.IRepository
import com.iti4.retailhub.datastorage.network.ApiState
import com.iti4.retailhub.models.CartProduct
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MyBagViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: MyBagViewModel
    private val repository =  mockk<IRepository>()


    @Before
    fun setup() {
        every { repository.getUserShopLocalId() } returns ""
        Dispatchers.setMain(testDispatcher)
        viewModel = MyBagViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when fetching my bag products, it (returns list) AND (api state is success)`() = runTest {
        // init repo function
        coEvery { repository.getMyBagProducts(any()) } returns flow {
            emit(
                listOf(
                    mockk<CartProduct>(),
                    mockk<CartProduct>()
                )
            )
        }
        // to collect states of flow
        var collectedState = mutableListOf<ApiState>()
        // to block until collection happens
        val completionSignal = CompletableDeferred<Unit>()

        val job = launch {
            viewModel.myBagProductsState.collect {
                collectedState.add(it)
                if (it is ApiState.Success<*>)
                    completionSignal.complete(Unit)
            }
        }

        viewModel.getMyBagProducts()
        testDispatcher.scheduler.advanceUntilIdle()
        // wait before assertion
        completionSignal.await()

        assertTrue(collectedState[0] is ApiState.Loading)

        assertThat(collectedState[1], `is`(instanceOf(ApiState.Success::class.java)))

        val result = collectedState[1] as ApiState.Success<*>
        val retrivedData = result.data as List<CartProduct>
        assertThat(retrivedData.size, `is`(2))
        job.cancel()  // Clean up
    }
}