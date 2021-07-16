package com.github.zharovvv.high.order.functions

import java.util.*
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

/**
 * # Встраиваемые функции: устранение накладных расходов лямбда-выражений
 * Лямбда-выражения обычно компилируются в анонимные классы.
 * Но это означает, что каждый раз, когда используется лямбда-выражение создается дополнительный класс;
 * и если лямбда выражение хранит (захватывает) какие-то переменные, для каждого вызова создается новый объект.
 * Это влечет дополнительные накладные расходы, ухудшающие эффективность реализации с лямбда-выражениями по
 * сравнению с функцией, которая вуполняет тот же код непосредственно.
 *
 * Компилятор Kotlin может сгенерировать код, не уступающий по эффективности инструкциям Java.
 * Если отметить функцию модификатором __inline__, компилятор не будет генерировать вызов функции в месте её использования,
 * а просто вставит код её реализации.
 */
fun main() {
    val lockOwner = LockOwner(ReentrantLock())
    lockOwner.runUnderLock(lockOwner::someFun)
    readFirstIntFromScanner()
}

/**
 * Пример встроенной функции.
 * Тело этой функции будет вставлено в каждом месте вызова
 */
inline fun <T> synchronized(lock: Lock, action: () -> T): T {
    lock.lock()
    try {
        return action()
    } finally {
        lock.unlock()
    }
}

/**
 * Пример использования функции [synchronized].
 * Пример эквивалентного кода, который будет скомпилирован в тот же байт-код - [_foo_].
 * Обратим внимание, что встраивание применяется не только к реализации функции [synchronized], но и к телу лямбда
 * выражения { println("Action") }. Байт-код, сгенерированный для лямбда-выражения,
 * становится частью определения вызывающей функции и не заключается в анонимный класс, реализующий интрефейс функции.
 */
fun foo(lock: Lock) {
    println("Before sync")
    synchronized(lock) {
        println("Action")
    }
    println("After sync")
}

/**
 * Эквивалетный код, который будет скомпилирован в тот же байт-код что и у метода [foo].
 */
fun _foo_(lock: Lock) {
    println("Before sync")
    lock.lock()
    try {
        println("Action")
    } finally {
        lock.unlock()
    }
    println("After sync")
}

class LockOwner(private val lock: Lock) {
    /**
     * В данном случае код лямбда-выражения недоступен вточке вызова функции [synchronized], поэтому его нельзя
     * встроить. Встроится только тело функции [synchronized], а лямбда-выражение будет вызвано как обычно.
     */
    fun runUnderLock(body: () -> Unit) {
        synchronized(
            lock,
            body //функция body не встраиваится, потому что лямбда-выражение отсутствует в точке вызова
        )
    }

    fun someFun() {}
}

/**
 * В общем случае параметр можно встроить, если его вызывают непосредственно или передают как аргумент
 * другой встраиваемой функции.
 */
inline fun foo__(inlined: () -> Unit, noinline notInlined: () -> Unit) {
//    val inlinedFunc: () -> Unit = inlined // Ошибка при компиляции: "Illegal usage of inline-parameter"
    val nonInlinedFunc: () -> Unit =
        notInlined //Ошибки не будет, так как соответствующий параметр объявлен невстраиваемым
    inlined()
    notInlined()
    readFirstIntFromScanner()   //во встраиваемые функции нельзя добавить вызов Non-public методов.
    //Однако это ограничение можно обойти для internal функций, если добавить аннотацию @PublishedApi для метода.
}

/**
 * При применении к классу или члену с internal видимостью позволяет использовать его из public inline функций
 * и делает его effectively public.
 */
@PublishedApi
internal fun readFirstIntFromScanner(): Int {
    Scanner(System.`in`).use { scanner -> //Функиция-расширение для интерфейса Closeable [use] - эквивалент try-with-resources в Java
        return scanner.nextInt()    //используется нелокальный возврат, чтобы вернуть значение из функции readFirstIntFromScanner
    }
}

/**
 * Пример нелокального возврата.
 *
 * Обратим внимание, что выход из внешней функции выполняется только тогда, когда функция, принимающая лямбда-выражение,
 * является встраиваемой.
 * Использование выражения return в лямда-выражениях, передаваемых невстариваемым функциям, недопустимо.
 */
fun nonLocalReturnFun(lock: Lock): Int {
    synchronized(lock) {
        return 2 + 3    //инструкция return производит нелокалььный возврат, потому что возвращает результат из внешнего блока
        //а не из блока, содержащего return.
    }
}

