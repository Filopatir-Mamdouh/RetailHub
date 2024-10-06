package com.iti4.retailhub.logic

import com.iti4.retailhub.R
import com.iti4.retailhub.constants.CategoriesName

fun String.toImg(category: CategoriesName?) : Int{
    return when {
        this == "SHOES" && category == CategoriesName.MEN -> R.drawable.man_shoes
        this == "SHOES" && category == CategoriesName.WOMEN -> R.drawable.women_shoes
        this == "SHOES" && category == CategoriesName.KIDS -> R.drawable.kid_shoes
        this == "T-SHIRTS" && category == CategoriesName.MEN -> R.drawable.men_cloths
        this == "ACCESSORIES" && category == CategoriesName.WOMEN -> R.drawable.women_accesories
        this == "ACCESSORIES" && category == CategoriesName.MEN -> R.drawable.men_accesories
        else -> R.drawable.ic_launcher_foreground
    }
}