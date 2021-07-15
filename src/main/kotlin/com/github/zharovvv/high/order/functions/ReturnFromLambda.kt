package com.github.zharovvv.high.order.functions

import com.github.zharovvv.lambda.Person

/**
 * # Возвраты из лямбда выражений
 *
 * Пример нелокального возврата.
 */
fun lookForAlice(people: List<Person>) {
    people.forEach { person: Person ->
        if (person.name == "Alice") {
            println("Found!")
            return  //Нелокальный возврат (возможен благодаря тому, что forEach является встраиваемой функцией)
        }
    }
    println("Alice is not found")
}

/**
 * Возврат из лямбда-выражения с помощью меток (Локальный возврат).
 *
 * Локальный возврат прерывает работу лямбда-выражения и продолжает выполнение инструкции, следующей сразу за вызовом
 * лямбда-выражения.
 */
fun lookForAlice_(people: List<Person>) {
    people.forEach label@{ person: Person ->
        if (person.name == "Alice") {
            println("Found!")
            return@label    //Локальный возврат
        }
        println("${person.name} is not Alice")
    }
    println("Alice might be somewhere")//Эта строка выводится всегда

    /**
     * Те же правила использования меток относятся к выражению this.
     * По аналогии с метками для выражений return метку для this можно задать явно
     * или воспользоваться именем вмещающей функции.
     */
    val str: String = StringBuilder().apply sb@{
        listOf(1, 2, 3).apply {
            this@sb.append(this.toString())
        }
    }.toString()

//    people.forEach { person: Person ->
//        if (person.name == "Alice") {
//            println("Found!")
//            return@forEach    //Локальный возврат
//        }
//    }
//    println("Alice might be somewhere")
}

/**
 * Синтаксис локального возврата избыточно многословен и может вводить в заблуждение, если лямбда-выражение содержит
 * несколько выражений return.
 *
 * Анонимные функции: по умолчанию возврат выполняется локально.
 */
fun lookForAlice__(people: List<Person>) {
    people.forEach(fun(person) {
        if (person.name == "Alice") {
            println("Found!")
            return  //Локальный возврат (выход из анонимной функции)
        }
        println("${person.name} is not Alice")
    })
}

/**
 * Анонимная функция - другая синтаксическая форма лямбда-выражения.
 */
val anonymousFun: (intVal: Int) -> String = fun(intVal): String {
    return intVal.toString()
}

fun main() {
    val people = listOf(
        Person("John", 21),
        Person("Alice", 18),
        Person("Bob", 15)
    )
    lookForAlice(people)
    //Found!

    println()

    lookForAlice_(people)
    //John is not Alice
    //Found!
    //Bob is not Alice
    //Alice might be somewhere

    println()

    lookForAlice__(people)
    //John is not Alice
    //Found!
    //Bob is not Alice
}