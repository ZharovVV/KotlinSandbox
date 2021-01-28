package com.github.zharovvv.classes

/**
 * [Expr] - запечатанный класс.
 * Запечатанный класс в качестве супер-класса ограничивает возможность создания подклассов.
 * Все прямые подклассы должны быть объявлены в теле запечатанного класса,
 * либо в одном файле с запечатанным классом (начиная с Kotlin 1.1).
 * Модификатор sealed означает, что класс по умолчанию открыт (opened).
 *
 *
 * Java:
 *
 */
sealed class Expr {
    class Num(val value: Int) : Expr()
    class Sum(val left: Expr, val right: Expr) : Expr()
}

fun eval(e: Expr): Int =
    when (e) {
        is Expr.Num -> e.value
        is Expr.Sum -> eval(e.left) + eval(e.right)
        is OtherNum -> e.otherValue
        //Нет необходимости определять ветку по умолчанию
    }

/**
 * Начиная с Kotlin 1.1.
 */
class OtherNum(val otherValue: Int) : Expr()