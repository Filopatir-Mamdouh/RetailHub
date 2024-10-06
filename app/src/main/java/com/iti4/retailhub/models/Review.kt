package com.iti4.retailhub.models

// Review class to store individual review details
data class Review(
    val name: String,
    val rate: Float,
    val comment: String,
    val imageUrl: String
)
