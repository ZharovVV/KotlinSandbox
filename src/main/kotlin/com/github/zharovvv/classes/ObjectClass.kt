package com.github.zharovvv.classes

object ObjectClass {

    var value: String = "unknown"
}

fun main() {
    val instance = ObjectClass
    println(instance.value)
}