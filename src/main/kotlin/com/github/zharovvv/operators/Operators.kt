package com.github.zharovvv.operators

/**
 * Некоторые особенности языка Java тесно связаны с определенными классами в стандартной библиотеке.
 * Например, объекты, реализуюзие интерфейс Iterable, можно использовать в циклах for, а объекты, реализуюзие
 * интерфейс AutoCloseable, можно использовать в инструкциях try-with-resources.
 *
 * В Kotlin есть похожие особенности, но они связаны не с определенными типами, а с функциями, имеющими специальные
 * имена. Например, если ваш класс определяет метод со специальным именем plus, то к экземплярам этого класса может
 * применяться оператор +. Такой подход в Kotlin называется _соглашениями_.
 *
 * Вместо опоры на типы, как это принято в Java, в Kotlin действует принцип следования соглашениям, потому что это
 * позволяет разработчикам адаптировать существующие Java-классы к требованиям возможностей языка Kotlin.
 * Множество интерфейсов, реализованных Java-классами, фиксировано, и Kotlin не может изменять существующих классов,
 * чтобы добавить в них реализацию дополнительных интерфейсов. С другой стороны, благодаря механизму функций-расширений
 * есть возможность добавлять новые методы в классы.
 */
fun main() {
    val point1 = Point(1, 2)
    val point2 = Point(3, 4)
    val point3 = Point(0, -1)
    val resultPoint = point1 + point2 - point3 // point1.plus(point2).minus(point3)
    println(resultPoint * 3.0) //resultPoint.times(3.0D)
    println(3.0 * resultPoint) //PointExtension.times(3.0D, resultPoint)
    println(-resultPoint)   //resultPoint.unaryMinus()
    println(point1 > point2)    //point1.compareTo(point2) > 0
    println(point2 <= point3)   //point2.compareTo(point3) <= 0
    var varPoint = point1
    /**
     * Оператор a += b может быть преобразован в вызов функции plus или plusAssign:
     * - a = a.plus(b)
     * - a.plusAssign(b)
     *
     * Если определены и применимы обе функции, компилятор сообщит об ошибке.
     * Исправить проблему проще всего заменой оператора обычным вызовом функции.
     * Также можно заменить var на val, чтобы операция a = a.plus(b) оказалась недопустимой в текущем контексте.
     * Но лучше изначально проектировать классы непротиворечивыми: не добавлять сразу две функции plus и plusAssign.
     * Если класс неизменяемый - добавляем только plus.
     * Если класс изменяемый (например, Builder) - добавляем только plusAssign.
     */
    varPoint += point3
    //point3 += point2 // - Ошибка компиляции
    println(point1[0])  //point1.get(0)

    //---------Вызов Java-кода в Kotlin-----------
    val pointJava1 = PointJava(1, 4)
    val pointJava2 = PointJava(3, 4)
    val resultPointJava = pointJava1 + pointJava2   // вызов функции plus, определенной в java-классе
    println(resultPointJava * 4.0)  // вызов функции times, определенной в java-классе
    var varPointJava = pointJava1
    varPointJava += pointJava2
    //--------------------------------------------

    /**
     * Стандартная библиотека Kotlin поддерживет оба подхода для коллекций.
     * Операторы + и - всегда возвращают новую коллекцию.
     * Операторы += и -= работают с изменяемыми коллекциями, модифицируя их на месте (будет вызван plusAssign),
     * а для неизменяемых коллекций возвращает копию с модификациями (будет вызван plus и операция присвоения).
     */
    val mutableList = mutableListOf<Int>()
    //Добавление элемента в mutableList.
    //Если бы пременная mutableList была var, то возникла бы ошибка компиляции.
    mutableList += 3 //CollectionsKt.plusAssign(3)
    //Создание новой коллекции на основе двух других (возвращается неизменяемая коллекция).
    var newImmutableList = mutableList + listOf(
        4,
        5
    ) //List newImmutableList = CollectionsKt.plus((Collection)mutableList, (Iterable)CollectionsKt.listOf(new Integer[]{4, 5})
    //Присвоение newImmutableList копии с модификацией.
    @Suppress("SuspiciousCollectionReassignment")
    newImmutableList += 2 //newImmutableList = CollectionsKt.plus((Collection)newImmutableList, 2);
    println(newImmutableList)

    val rectangle = Rectangle(Point(10, 20), Point(50, 50))
    println(Point(20, 30) in rectangle) //true rectangle.contains(Point(20,30))
    println(Point(5, 5) in rectangle) //false

    /**
     * Мультидекларации (destructuring declarations) позволяют распаковать единое составное значение и использовать
     * его для инициализации нескольких переменных.
     */
    val (x, y) = point1 //компилируется в
    //int x = point1.component1();
    //int y = point1.component2();
    println(x)  //1

    //Пример использования мультидекларации в цикле (Обход словаря):
    val map = mapOf("Oracle" to "Java", "JetBrains" to "Kotlin")
    for ((key, value) in map) {
        println("$key -> $value")
    }
    //Эквивалентно:
    for (entry in map.entries) {
        val key = entry.component1()
        val value = entry.component2()
        // ...
    }
}
