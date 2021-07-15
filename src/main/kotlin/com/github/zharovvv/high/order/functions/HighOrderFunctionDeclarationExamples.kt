package com.github.zharovvv.high.order.functions

/**
 * Переменная, имеющая тип функции это реализация интерфейса FunctionN
 * Каждый интерфейс определяет единственный метод invoke, который вызывает функцию.
 */
fun twoAndThree(
    operation: (Int, Int) -> Int //operation: Function2<Int, Int, Int>
    = { x, y -> x + y } //добавление значения по умолчанию для типа функции
) {
    val result = operation(2, 3)    //operation.invoke(2, 3)
    println("The result is $result")
}

fun threeAndFour(operationBlock: (() -> Unit)?) {  //operationBlock: Function0<Unit>
    if (operationBlock != null) {
        operationBlock()    //либо можно вызвать operationBlock?.invoke()
    }
}

/**
 * # Возврат функций из функций
 * Пример: расчет стоимости доставки в зависимости от выбранного транспорта
 */
enum class Delivery { STANDARD, EXPEDITED }
class Order(val itemCount: Int)

fun getShippingCostCalculator(delivery: Delivery): (order: Order) -> Double {
    return when (delivery) {
        Delivery.STANDARD -> { order -> 1.2 * order.itemCount }
        Delivery.EXPEDITED -> { order -> 6 + 2.1 * order.itemCount }
    }
}
