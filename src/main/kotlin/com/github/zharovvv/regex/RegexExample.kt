package com.github.zharovvv.regex

fun main() {
    val testString = "У вас есть 100500.56 RUB. А также 256.00 USD. 100500.00 RUBLEY"
    val regex = "(?<amount>\\d+\\.\\d{2})\\s(?<isoCode>${Currency.entries.joinToString(separator = "|")})\\b".toRegex()
    val result = testString.replace(regex) { matchResult ->
        val isoCode = matchResult.groups["isoCode"]?.value
        val symbol = Currency.entries.first { it.isoCode.equals(isoCode, ignoreCase = true) }.symbol
        matchResult.groups["amount"]?.value + " " + symbol
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