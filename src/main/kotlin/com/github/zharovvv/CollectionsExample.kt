@file:JvmName("CollectionsExample")

package com.github.zharovvv

fun main(args: Array<String>) {
    println(varargFun("secondString", "thirdString"))
    println("Kotlin " plus1 "in Action")
}

/**
 * "*" - оператор распаковки. Распаковывает содержимое массива.
 * Позволяет объединить в одном вызове значения из массива и несколько фиксированных значений.
 *
 * "vararg" - ключевое слово - аналог "..." в Java.
 */
fun varargFun(vararg strings: String): List<String> {
    return listOf("firstString", *strings)
}

/**
 * Пример инфиксного и обычного вызовов функции to
 */
@Suppress("ReplaceToWithInfixForm")
fun infixCallExample(): Map<Int, String> {
    val pair: Pair<Int, String> = 4 to "four"
    val (number, name) = pair //инициализация сразу двух переменных (мультидекларация/ destruction declaration)
    println("$number $name")
    return mapOf(
        1 to "one", //вызов функции to с использованием инфиксной нотации
        2.to("two"),    //вызов функции to обычным способом
        3.to("three"),
        pair
    )
}

/**
 * Инфиксную форму вызова можно применять к обычным методам и к функциям-расширениям,
 * имеющим один обязательный параметр. Чтобы разрешить вызовы функций в инфиксной нотаци, в её объявлении нужно добавить
 * модификатор infix.
 */
infix fun String.plus1(other: String): String {
    return this + other
}