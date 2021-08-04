package com.github.zharovvv.reflection

import com.github.zharovvv.lambda.Person
import kotlin.reflect.*
import kotlin.reflect.full.memberProperties

/**
 * Свойство верхнего уровня представлены экземплярами интерфейса KProperty0, метод get которого не имеет аргументов.
 */
var counter = 0

fun foo(x: Int) = println(x)

fun main() {
    val person = Person("Alice", 21)
    val kClass: KClass<Person> = person.javaClass.kotlin    //Узнаем класс объекта во время выполнения.
    println(kClass.simpleName)
    kClass.memberProperties.forEach { println(it) }

    val function1: Function1<Int, Unit> = ::foo //::foo возвращает KFunction1<Int, Unit>
    val lambdaFunction1: (Int) -> Unit = ::foo
    val kFunction: KFunction<Unit> = ::foo

    /**
     * Такие типы как KFunction1 - __синтетические типы__, генерируемые компилятором, и вы не найдете их объявлений в
     * пакете kotlin.reflect. Это означает возможность использовать интерфейс для функций с любым количеством параметров.
     * Приём с использованием синтетических типов уменьшает размер kotlin-runtime.jar и помогает избежать искусственных
     * ограничений на возможное количество параметров функций.
     */
    val concreteKFunction: KFunction1<Int, Unit> = ::foo
    kFunction.call(100500)  //вызов функции с использованием механизма рефлексии
    concreteKFunction.invoke(100500)
    concreteKFunction(123)

    val kProperty: KMutableProperty0<Int> = ::counter
    kProperty.setter.call(21)
    println(kProperty.get())

    /**
     * Свойство-член класса представлено экземпляром KProperty1, который имеет метод get с одним аргументом.
     * Чтобы получить значение такого свойства, нужно передать в аргументе экземпляр объекта, владеющего свойством.
     */
    val memberProperty: KProperty1<Person, String> = Person::name
    println(memberProperty.get(person))
}

inline fun <reified T> KAnnotatedElement.findAnnotation(): T? {
    return annotations.filterIsInstance<T>().firstOrNull()
}
