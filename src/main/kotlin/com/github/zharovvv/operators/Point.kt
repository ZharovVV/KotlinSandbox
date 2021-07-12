package com.github.zharovvv.operators

import kotlin.math.pow

data class Point(val x: Int, val y: Int) {

    private val vectorModule: Double = (x.toDouble().pow(2) + y.toDouble().pow(2))
        .pow(0.5)

    operator fun plus(other: Point): Point {
        return Point(this.x + other.x, this.y + other.y)
    }

    operator fun minus(other: Point): Point {
        return Point(this.x - other.x, this.y - other.y)
    }

    /**
     * Операторы в Kotlin не поддерживают _коммутативность_ (перемену операндов местами) по умолчанию.
     * Если необходимо дать пользователям возможность использовать выражения вида 1.5 * point в дополнение к point * 1.5,
     * следует определить отдельный оператор (например через функцию-расширение).
     */
    operator fun times(scale: Double): Point {
        return Point((x * scale).toInt(), (y * scale).toInt())
    }

    operator fun unaryMinus(): Point {
        return Point(-x, -y)
    }

    //Можно было также объявить класс реализующим интерфейс Comparable.
    //Все Java-классы, реализующие интерфейс Comparable, можно сравнивать в коде на Kotlin с использованием краткого
    //синтаксиса операторов.
    operator fun compareTo(other: Point) : Int {
        if (this.vectorModule > other.vectorModule) {
            return 1
        }
        if (this.vectorModule < other.vectorModule) {
            return -1
        }
        return 0
    }
}


