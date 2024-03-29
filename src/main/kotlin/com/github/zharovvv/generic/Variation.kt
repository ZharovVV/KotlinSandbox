package com.github.zharovvv.generic

/**
 * # Вариантность
 * Обобщенный класс называют __инвариантным__ по типовому парамтеру, если для любых разных типов A и B Класс<A> не
 * является подтипом или супертипом Класс<B>. Пример инвариантного класса - [MutableList].
 * String является подтипом Any, но MutableList<String> не является подтипом MutableList<Any>.
 * Пример:
 * ```
 *  fun addAnswer(list: MutableList<Any>) {
 *      list.add(42)
 *  }
 *
 *  val strings = mutableListOf("abc", "def")
 *  addAnswer(strings)  //Если это строка скомпилируется
 *  strings.maxByOrNull { it.length }//в этой строке будет исключение Integer cannot be cast to String во время выполнения
 * ```
 * В Java все классы инвариантны (хотя конкретные декларации, использующие эти классы, могут не быть инвариантными).
 *
 * __Ковариантный класс__ - это обобщеннный класс, для которого верно следующе: если А подтип В, то Класс<A> подтип
 * Класс<B>. Пример ковариантного класса - [List].
 * В Kotlin, чтобы объявить класс ковариантным по некоторому типовому параметру, нужно добавить ключевое слово __out__
 * перед именем типового параметра.
 * Чтобы гарантировать безопасность типов _ковариантный класс_ может использоваться только в так назваемых
 * исходящих(out) позициях: то есть класс может производить значения типа T, но не потреблять их.
 *
 * __Контрвариантность__ - понятие обратное _ковариантности_.
 * __Контрвариантный класс__ - это обобщенный класс (возьмем для примера Consumer<T>), для которого верно следущее:
 * Consumer<A> - это подтип Consumer<B>, B - это подтип A.
 * В Kotlin, чтобы объявить класс контрвариантным по некоторому типовому параметру, нужно добавить ключевое слово __in__
 * перед именем типового параметра.
 */
fun main() {
    val listString = mutableListOf("abc", "def")
    val listAny = mutableListOf<Any>()
    copyData<Any>(listString, listAny)
    copyData_<String>(listString, listAny)
}

/**
 * Ключевое слово "out" в параметре типа класса требует, чтобы все методы,
 * использующие T, указывали его только в исходящей позиции.
 *
 * Обратим внимание, что параметры конструктора не находятся ни во входящей, ни в исходящей позиции.
 * Однако если параметры консруктора объявлены с помощью ключевых слов val и/или var, то вместе с ними объявляются
 * методы чтения и записи для изменяемых свойств. Поэтому параметр типа оказывается в исходящей позиции для неизменяемых
 * свойств и в обеих позициях для изменяемых.
 *
 * Правила позиции охватывают только видимое извне (public, protected, internal) API класса.
 */
interface Transformer<T> {
    /**
     * Тип параметра функции называют "входящей" позицией, а тип возвращаемого значения - "исходящей".
     */
    fun transform(t: T): T
}

/**
 * В Kotlin есть поддержка вариантности в месте объявления (в объявлениях классов).
 * В Java есть только поддержка вариантности в месте использования.
 *
 * Пример объявления вариантности в месте использования в Kotlin.
 * В данном случае мы объявлем _проекции типа_.
 * source - не просто список MutableList, а его проекция (с ограниченными возможностями; допускается только вызов методов,
 * возвращающих обобщенный параметр типа).
 */
fun <T> copyData(source: MutableList<out T>, destination: MutableList<T>) {
    for (item in source) {
        destination.add(item)
    }
}

/**
 * В данном случае destination - проекция, допускающая только вызов методов, принимающих обобщенный параметр типа.
 */
fun <T> copyData_(source: MutableList<T>, destination: MutableList<in T>) {
    for (item in source) {
        destination.add(item)
    }
}