package com.github.zharovvv.lambda

import java.util.*
import java.util.stream.Collectors

data class Person(val name: String, val age: Int) {

}

fun main() {
    val persons = listOf(
        Person("John", 21),
        Person("Sara", 24),
        Person("Yuliy", 24)
    )

    val maxAge = persons.maxByOrNull(Person::age)?.age
    val name: String? = persons
        .find { person -> person.age == maxAge }
        ?.name
    println(name)

    val groupedList: Map<Int, List<Person>> = persons.groupBy { it.age }
    print(groupedList)

    val strings = listOf("abc", "def")
    /**
     * Функция flatMap сначала преобразует (отображает map) каждый элемент
     * в коллекцию, согласно функции, переданной в аргументе, а затем собирает (или уплощает flattens)
     * несколько коллекций в одну.
     */
    println(strings.flatMap { string -> string.toList() })

    /**
     * При использовании Последовательностей (Sequences) не создается промежуточных коллекций для хранения элементов,
     * а следовательно, для большого количества элементов производительность будет заметно лучше.
     *
     * Точка входа для выполнения отложенных операций в Kotlin - интерфейс Sequence.
     * Он представляет собой простую последовательность элементов, которые могут перечисляться один за другим.
     * Интерфейс Sequence определяет только один метод - iterator,
     * который используется для получения значений последовательности.
     *
     * Особенность интерфейса Sequence - способ реализации операций. Элементы последовательности вычисляются «лениво».
     *
     * Любую коллекцию можно преобразовать в последовательность, вызвав функцию-расширение asSequence.
     * Обратное преобразование выполняется вызовом функции toList.
     */
    val personName: List<String> = persons.asSequence()
        .map { person -> person.name }
        .filter { personName -> personName.startsWith("j", ignoreCase = true) }
        .toList()

}