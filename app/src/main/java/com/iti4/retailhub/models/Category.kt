package com.iti4.retailhub.models

data class Category(
    val id:String,
    val title: String,
    val productTypes: List<String>
)