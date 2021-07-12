package com.github.zharovvv

import other.directory.exampleFunction

fun main(args: Array<String>) {
    exampleFunction(" ")
    val list = listOf(1, 2, 3)
    println(list.joinToString(separator = "; ", prefix = "(", postfix = ")"))

    println("kotlin".lastChar())
    println("kotlin".lastChar)
}

