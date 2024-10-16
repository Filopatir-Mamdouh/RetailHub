package com.iti4.retailhub.datastorage


import com.iti4.retailhub.CreateCustomerMutation
import com.iti4.retailhub.datastorage.network.RemoteDataSource
import com.iti4.retailhub.datastorage.network.RetrofitDataSource
import com.iti4.retailhub.datastorage.reviews.ReviewsDataStoreInterface
import com.iti4.retailhub.datastorage.userlocalprofiledata.UserLocalProfileData
import com.iti4.retailhub.type.CustomerInput
import com.iti4.retailhub.userauthuntication.UserAuthunticationInterface
import io.mockk.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class RepositoryTest {

    private lateinit var repository: Repository
    private val remoteDataSource: RemoteDataSource = mockk()
    val retrofitDataSource: RetrofitDataSource= mockk()
    val userAuthuntication: UserAuthunticationInterface= mockk()
    val UserLocalProfileData: UserLocalProfileData = mockk(relaxed = true)
    val reviewsDataStore: ReviewsDataStoreInterface= mockk()
    @Before
    fun setUp() {
        repository = Repository(remoteDataSource,retrofitDataSource,userAuthuntication,UserLocalProfileData,reviewsDataStore)
    }

    @Test
    fun `createUser success`() = runTest {
        val input = mockk<CustomerInput>()
        val customerCreate = mockk<CreateCustomerMutation.CustomerCreate>()

        coEvery { remoteDataSource.createUser(input) } returns flow {
            emit(customerCreate)
        }

        val result = repository.createUser(input)

        result.collect { response ->
            assertEquals(customerCreate, response)
        }

        // Verify that createUser was called on the remoteDataSource
        coVerify { remoteDataSource.createUser(input) }
    }

    @Test
    fun `createUser failure`() = runTest {

        val input = mockk<CustomerInput>()

        val exception = Exception("Failed to create user")
        coEvery { remoteDataSource.createUser(input) } returns flow {
            throw exception
        }

        try {
            repository.createUser(input).collect {

            }
            fail("Exception was expected")
        } catch (e: Exception) {
            assertEquals("Failed to create user", e.message)
        }

        // Verify that createUser was called on the remoteDataSource
        coVerify { remoteDataSource.createUser(input) }
    }

    @Test
    fun `addUserShopLocalId calls UserLocalProfileData addUserShopLocalId`() {
        // Given
        val shopId = "shop123"

        // When
        repository.addUserShopLocalId(shopId)

        // Then
        verify { UserLocalProfileData.addUserShopLocalId(shopId) }
    }

    @Test
    fun `getUserShopLocalId returns correct id`() {
        // Given
        val expectedShopId = "shop123"
        every { UserLocalProfileData.getUserShopLocalId() } returns expectedShopId

        // When
        val shopId = repository.getUserShopLocalId()

        // Then
        assertEquals(expectedShopId, shopId)
        verify { UserLocalProfileData.getUserShopLocalId() }
    }
    @Test
    fun `addUserShopLocalId should call UserLocalProfileData addUserShopLocalId with correct id`() {
        // Given
        val shopId = "shop123"

        // When
        repository.addUserShopLocalId(shopId)

        // Then
        verify { UserLocalProfileData.addUserShopLocalId(shopId) }
        confirmVerified(UserLocalProfileData)
    }

    @Test
    fun `addUserShopLocalId should handle null id gracefully`() {
        // Given
        val shopId: String? = null

        // When
        repository.addUserShopLocalId(shopId)

        // Then
        verify { UserLocalProfileData.addUserShopLocalId(shopId) }
        confirmVerified(UserLocalProfileData)
    }
}