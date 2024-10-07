package com.iti4.retailhub.logic

fun extractNumbersFromString(input: String): String {
    val regex = "\\d+".toRegex()
    val matchResult = regex.find(input)
    return matchResult?.value ?: ""
}
