package com.iti4.retailhub.constants

enum class CategoriesName(val id: String) {
    MEN("gid://shopify/Collection/285524000810"),
    WOMEN("gid://shopify/Collection/285524033578"),
    KIDS("gid://shopify/Collection/285524066346");
    companion object {
        fun fromId(id: String): CategoriesName? {
            return CategoriesName.entries.find { it.id == id }
        }
    }
}