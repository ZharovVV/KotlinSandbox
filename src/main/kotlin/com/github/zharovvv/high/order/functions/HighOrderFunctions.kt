package com.github.zharovvv.high.order.functions

/**
 * # Функции высшего порядка
 * High-order functions - функции, принимающие другие функции в аргументах и/или возвращающие их.
 * В Kotlin функции могут быть представлены как обычные значения, в виде лямбда-выражений или ссылок на функции.
 */
fun main() {
    //Объявление функции без объявления типа функции за счет механизма автоматического определения типов в Kotlin
    val sum1 = { x: Int, y: Int -> x + y }
    val action1 = { println(42) }
    //С объявлением типа функции
    //(Int, Int) -> Int - Тип функции
    //(Типы параметров) -> Возвращаемый тип
    val sum2: (Int, Int) -> Int = { x, y -> x + y }
    val action2: () -> Unit = { println(42) }
    val canReturnNull: (Int, Int) -> Int? = { _: Int, _: Int -> null }
    val funOrNull: ((Int, Int) -> Int)? = null

    /**
     * Определение типа функции может включать именованные параметры.
     * Имена, указанные в определении, можно использовать как имена аргументов лямда-выражений или изменять их.
     * Имена параметров не влияют на работу механизма контроля типов. Объявляя лямбда-выражение, вы не обязаны
     * использовать те же имена параметров, что указаны в объявлении типа функции. Но имена улучшают читаемость и
     * могут использоваться в IDE для автодополнения кода.
     */
    val funWithNames: (x: Int, y: Int) -> Int = { x, y -> x * y }

    twoAndThree(sum1)
    twoAndThree { x, y -> x * y - x }

    val expeditedShippingCostCalculator: (order: Order) -> Double = getShippingCostCalculator(Delivery.EXPEDITED)
    println("Shipping cost for EXPEDITED delivery and order with 3 items: ${expeditedShippingCostCalculator(Order(3))}")
}

