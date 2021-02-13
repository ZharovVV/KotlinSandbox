package com.github.zharovvv.lambda

import com.github.zharovvv.classes.DataClassExample

/**
 * Пример использования функции c получателем
 * public inline fun <T, R> with(receiver: T, block: T.() -> R): R
 *
 * Лямбда-выражение - это способ определения поведения, похожий на обычную функцию.
 * Лямбда-выражение с получателем - это спосо определения поведения, аналогичный функции-расширению.
 */
fun alphabet(): String {
//    val result = StringBuilder()
//    for (letter in 'A'..'Z') {
//        result.append(letter)
//    }
//    result.append("\nNow I now the alphabet!")
//    return result.toString()
    return with(StringBuilder()) {
        for (letter in 'A'..'Z') {
            append(letter)
        }
        append("\nNow I now the alphabet!")
        toString()
    }
}

/**
 * Пример использования функции с получателем
 * public inline fun <T> T.apply(block: T.() -> Unit): T
 *
 * Классический пример её использования - создание экземпляра,
 * у которого нужно сразу инициализировать некоторые свойства.
 * В Java это обычно выполняется с помощью отдельного объекта Builder.
 */
fun alphabet2(): String = StringBuilder().apply {
    for (letter in 'A'..'Z') {
        append(letter)
    }
    append("\nNow I now the alphabet!")
}.toString()

fun main() {
    DataClassExample("1", "2", "3")
        .apply { four = "4etblre" }
        .also { println(it) }
        .let { println(it.four) }
}

