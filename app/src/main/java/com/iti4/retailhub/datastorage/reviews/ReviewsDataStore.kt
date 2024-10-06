package com.iti4.retailhub.datastorage.reviews

import android.util.Log
import com.iti4.retailhub.models.Review

class ReviewsDataStore:ReviewsDataStoreInterface {
        val reviews: MutableList<Review> = mutableListOf()

    private val positiveComments = listOf(
        "I love this product",
        "This product is amazing",
        "I would recommend this product",
        "This product is the best",
        "I'm so happy with this product",
        "This product exceeded my expectations",
        "I'm impressed with this product",
        "This product is top-notch",
        "I'm a big fan of this product",
        "This product is fantastic"
    )
    private val negativeComments = listOf(
        "I hate this product",
        "This product is terrible",
        "I would not recommend this product",
        "This product is the worst",
        "I'm so disappointed with this product",
        "This product did not meet my expectations",
        "I'm not impressed with this product",
        "This product is mediocre",
        "I'm not a fan of this product",
        "This product is okay, but not great"
    )

    private  val maleImageUrls = listOf(
        "https://randomuser.me/api/portraits/men/1.jpg",
        "https://randomuser.me/api/portraits/men/2.jpg",
        "https://randomuser.me/api/portraits/men/3.jpg",
        "https://randomuser.me/api/portraits/men/4.jpg",
        "https://randomuser.me/api/portraits/men/5.jpg"
    )
    private  val femaleImageUrls = listOf(
        "https://randomuser.me/api/portraits/women/1.jpg",
        " https://randomuser.me/api/portraits/women/2.jpg",
        "https://randomuser.me/api/portraits/women/3.jpg",
        "https://randomuser.me/api/portraits/women/4.jpg",
        "https://randomuser.me/api/portraits/women/5.jpg"
    )
    private val firstNames = listOf(
        "Ethan",
        "Liam",
        "Noah",
        "Lucas",
        "Mason",

        "Emma",
        "Olivia",
        "Ava",
        "Sophia",
        "Mia"
    )

    private  val lastNames = listOf(
        "Smith",
        "Johnson",
        "Williams",
        "Brown",
        "Davis",

        "Johnson",
        "Williams",
        "Brown",
        "Davis",
        "Miller"
    )
    init {
        Log.d("TAG", "init:ReviewsDataStore ")
            for (i in 1..50) {
                val rate = (1..5).random().toFloat()
                val comment = if (rate > 3.5) {
                    positiveComments.random()
                } else {
                    negativeComments.random()
                }
                val firstName= (0..9).random()
                var name = "${firstNames[firstName]} ${lastNames.random()}"
                var imageUrls = ""
                if (firstName in 0..4){
                    imageUrls = maleImageUrls.random()
                }else{
                    imageUrls = femaleImageUrls.random()
                }
                val review = Review(
                    name = name,
                    rate = rate,
                    comment = comment,
                    imageUrl = imageUrls
                )
                reviews.add(review)
            }
    }

       override fun addReview(review: Review) {
           reviews.add(review)
        }

    override  fun getAllReviews(reviewsNumbers:Int): List<Review> {
            return reviews.take(reviewsNumbers)
        }



}