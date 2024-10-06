package com.iti4.retailhub.features.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti4.retailhub.datastorage.IRepository
import com.iti4.retailhub.datastorage.network.ApiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(private val repository: IRepository) : ViewModel() {
    private val dispatcher = Dispatchers.IO

    private val _paymentIntentResponse = MutableStateFlow<ApiState>(ApiState.Loading)
    val paymentIntentResponse = _paymentIntentResponse.onStart { }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ApiState.Loading)


}



