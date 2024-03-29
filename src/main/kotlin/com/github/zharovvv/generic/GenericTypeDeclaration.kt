package com.github.zharovvv.generic

import java.util.*

/**
 * Типовые параметары можно объявлять для методов классов или интерфейсов, функций верхнего уровня и функций-расщирений.
 *
 * Пример объявления типового параметра в функции верхнего уровня.
 */
fun <T> someGenericTypeFun(list: List<T>): List<T> {
    //do something
    return list
}

/**
 * Объявление обобщенного свойства-расширения.
 *
 * Обычные свойства (не являющиеся расширениями) не могут иметь типовых параметров - нельзя сохранить несколько значений
 * разных типов в свойстве класса, а поэтому не имеет смысла объвлять свойства обобщенного типа, не являющиеся расширениями.
 */
val <T> List<T>.penultimate: T
    get() {
        return this[size - 2]
    }

/**
 * Ошибка компиляции:
 * Type parameter of a property must be used in its receiver type
 */
//val <T> genericProperty: T = TODO()

/**
 * После объвления обобщенного класса или интерфейса объвленные типовые параметры можно использовать в теле интерфейса
 * /класса, как любые другие типы.
 *
 * Пример объвления обобщенного интерфейса
 */
interface GenericInterface<T> {
    val generic: T
}

open class GenericClass<T>(override val generic: T) : GenericInterface<T> {
}

/**
 * Number - __верхняя__ граница для типового параметра T.
 * Когда какой-то тип определяется как _верхняя граница_ для типового параметра, в качестве соответствующих типов
 * аргуметов должен указваться либо именно этот тип, либо его подтипы.
 */
fun <T : Number> List<T>.sum(): T {
    TODO()
}

/**
 * Определение нескольких ограничений для типового параметра.
 * (В Java это выглядело бы так: <T extends CharSequence & Appendable>
 */
fun <T> ensureTrailingPeriod(seq: T) where T : CharSequence, T : Appendable {
    if (!seq.endsWith('.')) {
        seq.append('.')
    }
}

class Processor<T> {    //Типовой параметр без верхней границы на деле имеет верхнюю границу Any?.
    fun process(value: T) {
        value?.hashCode()
    }
}

/**
 * Обощенные типы стираются во время выполнения. То есть экземпляр обобщенного клааса не хранит информацию
 * о типовых аргументах, использованных для создания этого экземпляра.
 *
 * Kotlin не позволяет использовать обобщенных типов без типовых аргументов. Соответственно, возникает вопрос: как проверить,
 * что значение является списком, а не множеством или каким-то другим объектом? Это можно сделать при помощи проекций.
 * ```
 * if (value is List<*>) {...}
 * ```
 * Типовые параметры _встраиваемых_ функций могут __овеществляться__ - то есть во время выполнения можно ссылаться на
 * фактические типовые аргументы. Для этого необходимо отметить типовой параметр как овеществляемый (reified).
 */
inline fun <reified T> isA(value: Any) = value is T     //Отметим, что такую функцию нельзя будет вызвать из Java.

inline fun <reified T> loadService(): ServiceLoader<T> {
    return ServiceLoader.load(T::class.java)
}

class SomeService

fun main() {
    val items = listOf("one", 2, "three")
    /**
     * Каждый раз когда в программе встречается вызов функции с овеществляемым типовым параметром, компилятор точно
     * знает, какой тип используется в качестве типового аргумента для данного конкретного вызова. Соответственно,
     * компилятор может сгенерировать байт-код, который ссылается на конкретный класс, указанный в типовом аргументе.
     * И затем вставить этот байт-код в точку вызова (так как функция встраиваемая).
     */
    println(items.filterIsInstance<String>())
    val serviceLoaderImpl = loadService<SomeService>()
}
