package com.github.zharovvv.regex

fun main() {
    val testString = "У вас есть 100500.56 RUB. А также 256.00 USD."
    val regex = "(\\d+\\.\\d{2})\\s(${Currency.entries.joinToString(separator = "|")})".toRegex()
    val result = testString.replace(regex) { matchResult ->
        val isoCode = matchResult.groupValues[2]
        val symbol = Currency.entries.first { it.isoCode.equals(isoCode, ignoreCase = true) }.symbol
        matchResult.groupValues[1] + " " + symbol
    }
    println(result) //У вас есть 100500.56 ₽. А также 256.00 $.
}

enum class Currency(
    val isoCode: String,
    val symbol: String
) {
    RUB("RUB", "\u20bd"),
    USD("USD", "\$");
}