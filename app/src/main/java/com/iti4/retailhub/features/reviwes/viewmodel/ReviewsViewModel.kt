package com.iti4.retailhub.features.reviwes.viewmodel

import androidx.lifecycle.ViewModel
import com.iti4.retailhub.datastorage.IRepository
import com.iti4.retailhub.models.Review
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReviewsViewModel @Inject constructor(private val repository: IRepository): ViewModel() {
    fun getAllReviews(reviewsNumbers:Int): List<Review> {
        return repository.getAllReviews(reviewsNumbers)
    }
}