package com.github.zharovvv.coroutines.lesson4

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.produce
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

/**
 * # Обработка ошибок в корутинах
 * ## try-catch
 * Для обработки ошибок используется обычный try-catch.
 * Однако [launch] и [async] нужно обрабатывать по-разному.
 * Все исключения, которые будут происходить в рутовой корутине, будут приводить к поведению,
 * аналогичному в java: будет вызываться [Thread.UncaughtExceptionHandler].
 * Это интерфейс для обработки ошибок при завершении потока из-за неперехваченного исключения.
 *
 * Все исключения, которые вы не обработаете, будут доставлены родительской корутине и скоупу,
 * свянному с ней и приводить к остановке с ошибкой. Если вы в рамках скоупа используете [Job],
 * то дочерние корутины будут также остановлены, но без ошибки.
 * Чтобы этого не происходило используйте [SupervisorJob].
 *
 * Чтобы перехватывать исключения во всем скоупе, можно обернуть весь
 * [coroutineScope] или [supervisorScope] try-catch-м:
 * ```kotlin
 *  try {
 *      coroutineScope {
 *          ...
 *      }
 *  } catch (e: Exception) {
 *      //Обрабатываем все исключения в scope
 *  }
 * ```
 * #
 * ## Что не так async-await
 * Даже при обработке исключения как положено в документации в async - оно все равно приведет к остановке
 * родительской корутины. Есть несколько способов избежать этого:
 * * обернуть вызов в [supervisorScope] ([coroutineScope] не поможет)
 * * подкинуть в [async] новый [Job]/[SupervisorJob]-контекст выполнения в корутину.
 * В этом случае разорвется связь с родительским скоупом.
 *
 * #
 * ## Уведомление об ошибке в корутине
 * [Job.invokeOnCompletion]
 * ```kotlin
 *  // Не вызывается в случае CancelException
 *  // и если ошибка произошла до задания CompletionHandler
 *  job.invokeOnCompletion { cause: Throwable? ->
 *      if (cause != null) {
 *          // Произошла ошибка
 *      } else {
 *          // Корутина была успешно выполнена
 *      }
 *  }
 * ```
 * Этот способ не позволит обработать исключение, а лишь уведомит, что корутина выполнилась успешно или с ошибкой.
 *
 * #
 * ## Отмена - это тоже ошибка
 * [CancellationException] - специальное исключение для обработки отмены выполнения корутин.
 * Вызов [CoroutineScope.cancel], [CoroutineContext.cancel], [Job.cancel]
 * приводит к созданию экземпляра [CancellationException]. Но его обработка происходит иначе и этот код зашит
 * в корутины и не может быть изменен. Отмена [Job], которым представляется корутина, не приведет к отмене её родителя,
 * и она (корутина-родитель) продолжит выполняться дальше, но дочерние корутины будут остановлены.
 *
 * Если вы хотите обрабатывать все исключения внутри корутины, написав код с try-catch на все эксепшены,
 * то ветку с [CancellationException] нужно написать отдельно, чтобы не нарушать работу того, как происходит отмена.
 * ```kotlin
 *  launch {
 *      try {
 *          ...
 *      } catch (e: CancellationException) {
 *          //обязательно пробрасываем дальше
 *          throw e
 *      } catch (e: Exception) {
 *          //обрабатываем другие исключения
 *      }
 *  }
 * ```
 * (К сожалению, пока что никакого линт-чека на это нет)
 *
 * #
 * ## [CoroutineExceptionHandler]
 * Вы можете определить поведение для всех необработанных исключений, которые происходят
 * в текущем контексте выполнения корутин. Для этого можно использовать класс [CoroutineExceptionHandler],
 * который служит перехватчиком любого исключения. Однако стоит учитывать, что повлиять на выполнение корутин
 * таким способом - нельзя. [CoroutineExceptionHandler] вызывается в последнюю очередь, после того как произошла
 * ошибка, причем может быть вызвана на любом потоке.
 *
 * ```kotlin
 *  val coroutineExceptionHandler = CoroutineExceptionHandler { context, error: Throwable ->
 *      logError(error)
 *  }
 *  CoroutineScope(coroutineExceptionHandler)
 *  scope.launch(coroutineExceptionHandler) { ... }
 * ```
 *
 * Также стоит помнить, что всегда будут выполняться [CoroutineExceptionHandler]-ы,
 * найденные через __Java ServiceLoader__, а также [Thread.UncaughtExceptionHandler]-ы.
 * Обычно его стоит использовать для логирования необработанных ошибок, например отправка их в аналитику.
 * Также CoroutineExceptionHandler не будет уведомляться об ошибках [CancellationException].
 *
 * #
 * ## Выполнение finally
 * ```kotlin
 *  val inputStream: InputStream
 *  try {
 *      doSomethingLong(inputStream)
 *  } catch (e: Exception) {
 *      //Обрабатываем исключение
 *  } finally {
 *      //корутина может быть отменена
 *      // и при вызове suspend-функции произойдет остановка выполнения корутины
 *      withContext(NonCancellable) {
 *          shutdown(inputStream)
 *      }
 *  }
 * ```
 * Порой, нам нужно гарантированно выполнить какой-то код, даже в случае отмены корутины.
 * Как раз для этого есть тип [Job] - [NonCancellable]. Вы должны запустить suspend-функцию в контексте с ней,
 * и она успешно выполнится, даже в случае отмены корутины. Обычно такое используют в блоках __finally__.
 * _Но этим лучше не увлекаться._
 * __Важно!__ Это API сделано только для использования с withContext.
 * Попытка передать его в CoroutineScope, launch и т.п. приведет к нарушению принципов _structured concurrency_.
 *
 *
 */
fun main(): Unit = runBlocking {
    CoroutineScope(SupervisorJob()).launch(CoroutineName("example1")) {
        try {
            doSomethingLong()
        } catch (e: Exception) {
            //Обрабатываем исключение
            //Но не все так просто
        } finally {

        }
        handleLaunchException() //выбросит ошибку, остановит родительскую корутину,
        // а также дочернюю корутину handleAsyncException
        handleAsyncException()
    }
        .join() //просто для разнообразия сделал так.
    // Вызываем join, так как скоуп не свзязан с родительским (который в runBlocking).
    // Без join программа завершится после выполнения блока runBlocking,
    // не дожидаясь завершения всех корутин.
}

@Suppress("UNREACHABLE_CODE")
suspend fun doSomethingLong(): Int {
    println("coroutineName = ${coroutineContext[CoroutineName]?.name}")
    println("currentThread = ${Thread.currentThread()}")
    println("doSomethingLong")
//    throw IllegalArgumentException()
    return 100500
}

/**
 * [launch] и [actor] - самостоятельная обработка исключений (т.е. внутри)
 */
suspend fun handleLaunchException(): Unit = coroutineScope { //из-за coroutineScope корутина
    // handleAsyncException - не будет отменена (так как используется Job, а не SupervisorJob.
    launch(CoroutineName("handleLaunchException")) {
        try {
            doSomethingLong()
        } catch (e: Exception) {
            //Обрабатываем исключение
            throw e
        }
    }
}

/**
 * [async] и [produce] требуют от того, кто вызовет код обработать исключение.
 */
suspend fun handleAsyncException(): Unit =
    supervisorScope {// один из правильных способов обработки ошибок для async-await
        val deferred: Deferred<Int> = async(CoroutineName("handleAsyncException")) {
            doSomethingLong()
        }
        try {
            deferred.await()
        } catch (e: Exception) {
            //Обрабатываем исключение
            throw e
        }
    }

/**
 * Пример обработки всех исключений в скоупе.
 */
suspend fun safetyCallInScope(): Unit =
    try {
        coroutineScope {

        }
    } catch (e: Exception) {
        //Обрабатываем все исключения в scope
    }