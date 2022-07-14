package com.github.zharovvv.lambda

import com.github.zharovvv.classes.DataClassExample

/**
 * Пример использования функции c получателем
 * public inline fun <T, R> with(receiver: T, block: T.() -> R): R
 *
 * Лямбда-выражение - это способ определения поведения, похожий на обычную функцию.
 * Лямбда-выражение с получателем - это способ определения поведения, аналогичный функции-расширению.
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

    //------Пример функции с получателем типа T.(R) -> U (в сравнении)
    "Пупа1".somethingBlockWork_It {
        it.doSomething(" Лупа1")
    }

    "Пупа2".somethingBlockWork_This {
        doSomething(" Лупа2")
    }

    "Пупа3".somethingBlockWork_SuperMegaThis {
        println(it) //Пупа3
        doSomething(" Лупа3")   //Пупа3 Лупа3
    }
}

interface SomeInterface<T> {
    fun doSomething(value: T)
}

fun <T> T.somethingBlockWork_It(block: (SomeInterface<T>) -> Unit) {
    val extValue = this
    val someInterfaceImpl = object : SomeInterface<T> {
        override fun doSomething(value: T) {
            println(extValue.toString() + value.toString())
        }
    }
    block.invoke(someInterfaceImpl)
}

/**
 * Лямбда-выражение с получателем типа A.() -> C
 */
fun <T> T.somethingBlockWork_This(block: SomeInterface<T>.() -> Unit) {
    val extValue = this
    val someInterfaceImpl = object : SomeInterface<T> {
        override fun doSomething(value: T) {
            println(extValue.toString() + value.toString())
        }
    }
    someInterfaceImpl.block() // are same block.invoke(someInterfaceImpl)
}

/**
 * Лямбда-выражение с получателем типа A.(B) -> C
 */
fun <T> T.somethingBlockWork_SuperMegaThis(block: SomeInterface<T>.(value: T) -> Unit) {
    val extValue = this
    val someInterfaceImpl = object : SomeInterface<T> {
        override fun doSomething(value: T) {
            println(extValue.toString() + value.toString())
        }
    }
    someInterfaceImpl.block(extValue) //are same block.invoke(someInterfaceImpl, extValue)
}



