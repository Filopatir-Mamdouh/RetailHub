package com.iti4.retailhub.features.home

import androidx.lifecycle.ViewModel
import com.iti4.retailhub.datastorage.IRepository
import com.iti4.retailhub.datastorage.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: IRepository): ViewModel() {
    // TODO: Implement the ViewModel
}