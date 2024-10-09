package com.iti4.retailhub.logic

import java.text.NumberFormat
import java.util.Locale

fun extractNumbersFromString(input: String): String {
    val regex = "\\d+".toRegex()
    val matchResult = regex.find(input)
    return matchResult?.value ?: ""
}

fun Double.toTwoDecimalPlaces(locale: Locale = Locale.US): String {
    val numberFormat = NumberFormat.getInstance(locale)
    val parsedNumber = numberFormat.parse(this.toString())?.toDouble() ?: throw NumberFormatException("Cannot parse: $this")
    return String.format(locale, "%.2f", parsedNumber)
}


