package com.github.zharovvv.coroutines

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.Continuation

/**
 * # Continuation
 * * это абстрактное представление состояния программы в определенный момент,
 * которое может быть сохранено и использовано для перехода в это состояние.
 * __Continuation__ содержит всю информацию, чтобы продолжить выполнение программы с определенной точки.
 *
 * * __continuation__ является функцией обратного вызова, которая представляет текущее состояние выполнения
 * программы.
 *
 * ## Continuation Passing Style (CPS, Стиль передачи продолжения)
 * - это стиль написания программ, в котором события в будущем (то есть остальная часть вычислений) передается
 * как явный параметр. Значение, переданное в качестве этого параметра, называется __continuation-ом__.
 *
 * ##[kotlin.coroutines.Continuation]
 * - интерфейс, который является основным супер-типом всех корутин и представляет собой основу, на которой
 * реализован механизм корутин в kotlin.
 */
class ContinuationExample {

    /**
     * Пример применения _стиля передачи продолжения_
     */
    fun cpsExample() {
        fun processDate(data: String, callback: (String) -> Unit) {
            callback.invoke(data)
        }
        processDate(data = "CPS Example!") { println(it) }
    }

    /**
     * Данная функция скомпилируется в следующее (декомпилированный в java байткод):
     * ```java
     * @Nullable
     * public final Object suspendFunExample(
     *  @NotNull String data,
     *  @NotNull Continuation $completion
     * ) {
     *      System.out.println(data);
     *      return Unit.INSTANCE;
     * }
     * ```
     */
    suspend fun suspendFunExample(data: String) {
        println(data)
    }

    /**
     * [kotlin.coroutines.Continuation] представляет собой callback и имеет следующие методы:
     * ```kotlin
     * public interface Continuation<in T> {
     *  public val context: CoroutineContext
     *  public fun resumeWith(result: Result<T>)
     * }
     * ```
     */
    val continuation: Continuation<Any?>? = null

    /**
     * [Result] - обычный value-класс, хранящий результат или ошибку.
     */
    val result: Result<Any?> = Result.failure(IllegalStateException())
}

suspend fun main() {
    val job = GlobalScope.launch {
        delay(1000)
        print("World!")
    }
    print("Hello ")
    job.join()
}