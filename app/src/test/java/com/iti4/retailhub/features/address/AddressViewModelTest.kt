package com.iti4.retailhub.features.address

import com.iti4.retailhub.GetAddressesByIdQuery
import com.iti4.retailhub.GetAddressesDefaultIdQuery
import com.iti4.retailhub.datastorage.IRepository
import com.iti4.retailhub.datastorage.network.ApiState
import com.iti4.retailhub.features.checkout.CheckoutViewModel
import com.iti4.retailhub.features.mybag.viewmodel.MyBagViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
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
class AddressViewModelTest {


    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: AddressViewModel
    private val repository = mockk<IRepository>()


    @Before
    fun setup() {
        MockKAnnotations.init(this)
        every { repository.getUserShopLocalId() } returns "123"
        every { repository.getAddressesById("123") } returns flowOf(mockk<GetAddressesByIdQuery. Customer>())
        Dispatchers.setMain(testDispatcher)
        viewModel = AddressViewModel(repository)
    }


    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }



    @Test
    fun `getDefaultAddress, Valid Id, it returns address AND api state is success`() = runTest {
        // Mock valid response
        coEvery { repository.getDefaultAddress(any()) } returns flowOf(mockk<GetAddressesDefaultIdQuery.Customer>())

        val collectedState = mutableListOf<ApiState>()
        val completionSignal = CompletableDeferred<Unit>()

        val job = launch {
            viewModel.defaultAddressState.collect {
                collectedState.add(it)
                if (it is ApiState.Success<*>) {
                    completionSignal.complete(Unit)
                }
            }
        }

        viewModel.getDefaultAddress()
        testDispatcher.scheduler.advanceUntilIdle()
        completionSignal.await()

        assertTrue(collectedState[0] is ApiState.Loading)
        assertThat(collectedState[1], `is`(instanceOf(ApiState.Success::class.java)))
        val result = collectedState[1] as ApiState.Success<*>
        assertThat(result.data, `is`(instanceOf(GetAddressesDefaultIdQuery.Customer::class.java)))

        job.cancel()
    }

    @Test
    fun `getDefaultAddress, Invalid Id, it returns exception AND api state is error`() = runTest {
        // Mock exception response
        coEvery { repository.getDefaultAddress(any()) } returns flow {
            throw Exception("Network or data not found exception")
        }

        val collectedState = mutableListOf<ApiState>()
        val completionSignal = CompletableDeferred<Unit>()

        val job = launch {
            viewModel.defaultAddressState.collect {
                collectedState.add(it)
                if (it is ApiState.Error) {
                    completionSignal.complete(Unit)
                }
            }
        }

        viewModel.getDefaultAddress()
        testDispatcher.scheduler.advanceUntilIdle()
        completionSignal.await()

        assertTrue(collectedState[0] is ApiState.Loading)
        assertThat(collectedState[1], `is`(instanceOf(ApiState.Error::class.java)))

        job.cancel()
    }
}
