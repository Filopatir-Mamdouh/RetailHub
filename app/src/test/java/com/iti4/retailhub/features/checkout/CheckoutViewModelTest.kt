package com.iti4.retailhub.features.checkout

import com.iti4.retailhub.GetAddressesDefaultIdQuery
import com.iti4.retailhub.GetCustomerByIdQuery
import com.iti4.retailhub.datastorage.IRepository
import com.iti4.retailhub.datastorage.network.ApiState
import com.iti4.retailhub.models.CartProduct
import com.iti4.retailhub.models.CustomerAddressV2
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
class CheckoutViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: CheckoutViewModel
    private val repository = mockk<IRepository>()


    @Before
    fun setup() {
        every { repository.getUserShopLocalId() } returns ""
        Dispatchers.setMain(testDispatcher)
        viewModel = CheckoutViewModel(repository)
    }


    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun `getCustomerData, valid customer data, emits success`() = runTest {
        val customer =
            GetCustomerByIdQuery.Customer("fname1", "fname2", "test@gmail.com", "123123123")
        coEvery { repository.getCustomerInfoById(any()) } returns flowOf(customer)
        val collectedState = mutableListOf<ApiState>()
        val completionSignal = CompletableDeferred<Unit>()
        val job = launch {
            viewModel.customerResponse.collect {
                collectedState.add(it)
                if (it is ApiState.Success<*>) completionSignal.complete(Unit)
            }
        }
        viewModel.getCustomerData()
        testDispatcher.scheduler.advanceUntilIdle()
        completionSignal.await()
        assert(collectedState[0] is ApiState.Loading)
        assertThat(collectedState[1], `is`(instanceOf(ApiState.Success::class.java)))

        val successData = collectedState[1] as ApiState.Success<*>
        val retrievedCustomer = successData.data as GetCustomerByIdQuery.Customer
        assertThat(retrievedCustomer.email, `is`("test@gmail.com"))
        job.cancel()

    }

    @Test
    fun `getCustomerData, invalid customer data, emits error`() = runTest {
        val customer = GetCustomerByIdQuery.Customer(null, null, null, null)

        coEvery { repository.getCustomerInfoById(any()) } returns flowOf(customer)
        val collectedState = mutableListOf<ApiState>()
        val completionSignal = CompletableDeferred<Unit>()
        val job = launch {
            viewModel.customerResponse.collect {
                println("THE IT IS " + it)
                collectedState.add(it)
                if (it is ApiState.Error) completionSignal.complete(Unit)
            }
        }
        assertTrue(collectedState.isEmpty())
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(collectedState[0] is ApiState.Loading)

        viewModel.getCustomerData()
        testDispatcher.scheduler.advanceUntilIdle()
        completionSignal.await()
        assertThat(collectedState[1], `is`(instanceOf(ApiState.Error::class.java)))
        job.cancel()

    }

    @Test
    fun `getCustomerData, network error ,  data, emits error`() = runTest {


        coEvery { repository.getCustomerInfoById(any()) } returns flow {
            throw Exception("Network Error")
        }
        val collectedState = mutableListOf<ApiState>()
        val completionSignal = CompletableDeferred<Unit>()
        val job = launch {
            viewModel.customerResponse.collect {
                collectedState.add(it)
                if (it is ApiState.Error) completionSignal.complete(Unit)
            }
        }
        viewModel.getCustomerData()
        testDispatcher.scheduler.advanceUntilIdle()
        completionSignal.await()
        assert(collectedState[0] is ApiState.Loading)
        assertThat(collectedState[1], `is`(instanceOf(ApiState.Error::class.java)))
        job.cancel()
    }

    @Test
    fun `getDefaultAddress, Valid Id,it (returns address) AND (api state is success)`() = runTest {
        coEvery { repository.getDefaultAddress(any()) } returns flowOf(mockk<GetAddressesDefaultIdQuery.Customer>())
        var collectedState = mutableListOf<ApiState>()
        val completionSignal = CompletableDeferred<Unit>()
        val job = launch {
            viewModel.addressesState.collect {
                collectedState.add(it)
                if (it is ApiState.Success<*>)
                    completionSignal.complete(Unit)
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
    fun `getDefaultAddress, Invalid Id,it (returns exception) AND (api state is error)`() =
        runTest {
            coEvery { repository.getDefaultAddress(any()) } returns
                    flow {
                        throw Exception("Network or data not found exception")
                    }
            var collectedState = mutableListOf<ApiState>()
            val completionSignal = CompletableDeferred<Unit>()
            val job = launch {
                viewModel.addressesState.collect {
                    collectedState.add(it)
                    if (it is ApiState.Error)
                        completionSignal.complete(Unit)
                }
            }

            viewModel.getDefaultAddress()
            testDispatcher.scheduler.advanceUntilIdle()
            completionSignal.await()
            assertTrue(collectedState[0] is ApiState.Loading)
            assertThat(collectedState[1], `is`(instanceOf(ApiState.Error::class.java)))

            job.cancel()
        }

    @Test
    fun `createCheckoutDraftOrder with discount, card payment, and checkout address emits success`() =
        runTest {

            val listOfCartProduct = listOf(
                mockk<CartProduct>(),
                mockk<CartProduct>(),
            )
            val isCard = true
            val checkoutAddress =
                CustomerAddressV2("Name", "Phone", "Address1", "Address2", "City", "Country")
            val checkoutDefaultAddress = mockk<GetAddressesDefaultIdQuery.DefaultAddress>()

            coEvery { repository.deleteMyBagItem(any()) } returns flowOf(Unit)  // Mock deletion behavior
            coEvery { repository.createCheckoutDraftOrder(any()) } returns flowOf(mockDraftOrder)  // Mock draft order creation
            coEvery { repository.emailCheckoutDraftOrder(any()) } returns flowOf(Unit)  // Mock email sending
            coEvery { repository.completeCheckoutDraftOrder(any()) } returns flowOf(DraftOrderCompleteResponse(...))  // Mock order completion
            coEvery { repository.markOrderAsPaid(any()) } returns flowOf(Unit)  // Mock order payment
            coEvery { repository.setCustomerUsedDiscounts(any(), any()) } returns flowOf(Unit)  // Mock discount usage




        }

}