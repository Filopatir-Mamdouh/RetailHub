package com.iti4.retailhub.features.productSearch.viewmodel

import com.iti4.retailhub.datastorage.Repository
import com.iti4.retailhub.datastorage.network.ApiState
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Test


class ProductSearchViewModelTest{

    private val reposatory:Repository=mockk{
        coEvery {
            getProducts(any())
        }returns mockk()
    }
    private val viewModel=ProductSearchViewModel(reposatory)

    @Test
    fun `when send query for search succes state is applied `(){
        viewModel.searchProducts("")
        assert(viewModel.searchList.value is ApiState.Success<*>)
    }
}