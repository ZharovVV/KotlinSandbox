package com.github.zharovvv

fun main(args: Array<String>) {
    iteration()
}

private fun iteration() {
    val oneToTen: IntRange = 1..10  //Диапазоны в Kotlin являются закрытыми [1,10]
    val oneTo30: IntRange = 1.rangeTo(30)
    val oneToNine: IntRange = 1 until 10 //Полузакрытый диапазон [1,10)
    for (i in oneToNine) {
        println(i)
    }

    //обход прогрессии от 100 до 1 с шагом 2
    for (i in 100 downTo 1 step 2) {
        println(i)
    }

    val stringList = listOf("1", "2", "3")
    //обход коллекции
    for (str in stringList) {
        println(str)
    }
    //обход коллекции с сохранением индекса
    for ((index, str) in stringList.withIndex()) {
        println("$index : $str")
    }
}