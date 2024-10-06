package com.iti4.retailhub.datastorage.reviews

import com.iti4.retailhub.models.Review

interface ReviewsDataStoreInterface {
    fun getAllReviews(reviewsNumbers:Int): List<Review>
    fun addReview(review: Review)
}