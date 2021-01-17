package com.github.zharovvv

val kotlinLogo = """| //
                   .|//
                   .|/ \"""

val dollar = """${'$'}99"""

fun main(args: Array<String>) {
    splitExample()
    println(kotlinLogo.trimMargin(marginPrefix = "."))   //удаляет префикс и отступы в каждой строке.
    //marginPrefix - префикс, отмечающий конец отступа.
    println()
    println(dollar)
}

fun splitExample() {
    val str = "1111.2222-3333"
    println(str.split(".", "-"))    //[1111, 2222, 3333]
    println(str.split("\\.|-".toRegex()))    //аналогично, но с использованием регулярного выражения
}