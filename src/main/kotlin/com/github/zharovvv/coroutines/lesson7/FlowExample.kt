@file:Suppress("KDocUnresolvedReference")

package com.github.zharovvv.coroutines.lesson7

import com.github.zharovvv.coroutines.lesson2.mainRunBlocking
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*

/**
 * # [Flow]
 * Suspend-функции асинхронно возвращают только одно значение, но в реальных задачах мы сталкиваемся с тем, что нам
 * нужно обрабатывать несколько значений из одного источника. Для этого в корутинах сделали [Flow] -
 * собственную реализацию реактивных потоков на основе корутин.
 *
 * #
 * ## Холодные стримы, но не всегда
 * [Flow] - это холодный асинхронный стрим данных, который последовательно выдает значения и завершается успешно или
 * завершается ошибкой. Flow очень схож с Observable из RxJava: также имеет множество стандартных операторов,
 * промежуточных для модификации потока данных, и терминальных, которые запускают сбор данных из флоу.
 * Код внутри флоу не запускается сразу, а будет выполнен только тогда, когда вызовется один из терминальных операторов.
 * Это и является одной из основных причин, почему промежуточные операторы не являются suspend-функциями.
 *
 * Сам по себе флоу - это примитивнейший интерфейс, который содержит единственный метод [Flow.collect] с параметром
 * [FlowCollector]. Этот метод запускает процесс получения данных из флоу и это его единственная задача.
 * Сначала [Flow] был только холодным, однако в последствии появились его "горячие" реализации [StateFlow]
 * и [SharedFlow].
 *
 * ```kotlin
 *  public interface Flow<out T> {
 *
 *      public suspend fun collect(collector: FlowCollector<T>)
 *  }
 *
 *  public fun interface FlowCollector<in T> {
 *
 *      public suspend fun emit(value: T)
 *  }
 *
 * ```
 *
 * #
 * ## Операторы через расширения
 * Все операторы [Flow] реализованы через extension-функции, это позволяет легко добавить новые операторы, не усложняя
 * интерфейс флоу, а также это стирает грань между встроенными операторами и своими собственными.
 *
 * #
 * ## Flow Builders
 * Самые простые:
 * * [flowOf]
 * * [Iterable.asFlow]
 *
 * Если хотим самостоятельно эмитить значения:
 * * [flow]
 * ```kotlin
 * fun fibonacci(): Flow<BigInteger> = flow {
 *     var x = BigInteger.ZERO
 *     var y = BigInteger.ONE
 *     while (true) {
 *         emit(x)
 *         x = y.also {
 *             y += x
 *         }
 *     }
 * }
 *
 * fibonacci().take(100).collect { println(it) }
 * ```
 *
 * `emit` should happen strictly in the dispatchers of the [block] in order to preserve the flow context.
 * For example, the following code will result in an [IllegalStateException]:
 *
 * ```
 * flow {
 *     emit(1) // Ok
 *     withContext(Dispatcher.IO) {
 *         emit(2) // Will fail with ISE
 *     }
 * }
 * ```
 *
 * If you want to switch the context of execution of a flow, use the [flowOn] operator.
 *
 * #
 * ## Преобразование потока
 * Промежуточные операторы применяются к входящему стриму (_upstream_) и возвращают выходной стрим (_downstream_).
 * Промежуточные операторы не выполняют каких либо операций они только настраивают цепочку для будущего выполнения.
 *
 * _Короче, все как в RxJava._
 *
 * Пример кастомного оператора [Flow.unique].
 *
 * #
 * ## Терминальные операторы
 * Все терминальные операторы это suspend-функции, которые должны быть запущены в корутине в рамках какого-то скоупа.
 * _(Некоторые терминальные операторы не являются suspend-функциями, т.к. являются удобными обертками
 * над suspend-функциями.)_ Такие операторы применяются к входному потоку и запускают выполнение всех операций в рамках
 * него. Это все происходит асинхронно и не будет блокировать поток.
 *
 * Самый простой оператор для запуска сбора данных это вызвать оператор [Flow.collect],
 * который является suspend-функцией.
 *
 * Чтобы каждый раз не создавать корутину для сбора данных из флоу, можно использовать стандартную функцию
 * [Flow.launchIn], в которую нужно передать [CoroutineScope]. Внутри на переданном скоупе будет создана корутина,
 * в которой вызовется [Flow.collect].
 *
 * __Способы получения данных в корутине:__
 * * Передать лямбду в [Flow.collect]
 * ```kotlin
 *  scope.launch {
 *      myFlow.collect { value ->
 *          println("Collected $value")
 *      }
 *  }
 * ```
 *
 * * [Flow.onEach] + [Flow.collect] без лямбды:
 * _в примере collect вызывается внутри [launchIn]_
 * ```kotlin
 * flow
 *     .onEach { value -> updateUi(value) }
 *     .onCompletion { cause -> updateUi(if (cause == null) "Done" else "Failed") }
 *     .catch { cause -> LOG.error("Exception: $cause") }
 *     .launchIn(uiScope)
 * ```
 *
 * __Важно помнить__, что некоторые терминальные операторы могут привести к __бесконечному ожиданию__,
 * а значит к приостановке (прерыванию) корутины. Например, __флоу может быть бесконечным__ и эмитить значения пока его
 * не остановят. Важно это учитывать при использовании некоторых терминальных операторов, например [Flow.toList], для
 * которых важно, чтобы флоу завершился. В таких случаях используются дополнительные операторы,
 * которые будут ограничивать период ожидания.
 *
 * #
 * ## Последовательное выполнение и буферизация
 * Flow работает на основе корутин - это значит, что все операторы выполняют suspend-лямбды и вызовы collect в какой-то
 * корутине, а значит соответствуют всем её правилам работы.
 * Все suspend-функции выполняются последовательно, то есть после вызова каждого [FlowCollector.emit] нужно дождаться
 * окончания выполнения collect и всех операторов. И только после этого можно будет выполнить новый эмит.
 *
 * Другими словами: Flow эмитит следующее значение только после того как выполнятся все операторы для него и collector
 * обработает его.
 *
 * В такой ситуации flow может выполняться очень долго, если collect не будет выполняться быстро.
 * Пример такой ситуации - получение данных с гироскопа, которые сразу должны сохраняться в файл.
 * Поток данных сенсора может быть очень интенсивный, что создаст очередь на их запись, а запись может не успевать
 * из-за не быстрой файловой системы. В этом случае можно ускорить работу флоу, кешируя все значения и не дожидаться,
 * когда будет выполнен collect. Специально для этого можно использовать оператор [Flow.buffer], который собирает
 * все полученные значения из флоу, а затем передает их коллектору, когда тот будет готов их обработать.
 * Под капотом эмитится данные будут в одной корутине, а собираться в другой. Для этого в игру вступают [Channel].
 *
 * #
 * ## Адаптер для асинхронных подходов
 * Если у вас уже есть какой-то уже существующий реактивный подход или callback-и, которые вам надо превратить во flow,
 * то специально для этого есть flow-builder [callbackFlow], который с помощью каналов осуществляет коммуникацию между
 * новым холодным флоу и callback-style api.
 *
 * См. пример функции для конвертации callback-api во Flow - [Api.asFlow].
 *
 * Для single-shot callback API можно использовать также [suspendCancellableCoroutine]
 * (Не подойдет если хотим использовать Flow).
 *
 * #
 * ## Смена контекста выполнения
 * Flow имеет собственный контекст выполнения, который нельзя менять внутри операторов с помощью вызова [withContext].
 * При попытке эмита из другого контекста у вас произойдет ошибка. По умолчанию flow будет работать в том же контексте,
 * что и вызов [Flow.collect], т.е в контексте корутины.
 *
 * Для смены контекста выполнения используется оператор [Flow.flowOn]. Этот оператор меняет контекст выполнения только
 * для upstream-а, т.е. для всех операторов в цепочке, до предыдущего вызова flowOn. Важно, что flowOn не влияет
 * на контекст в котором будет работать collector. Для него используется контекст в месте вызова.
 *
 * Такие правила смены контекста обуславливаются тем, что у flow есть правило
 * ___Context Preservation___ _(сохранение контекста)_ - особенность флоу, которая требует сохранение
 * контекста коллектора.
 *
 * #
 * ## Обработка ошибок
 * * Стандартный способ обработки ошибок это обернуть suspend-функцию collect в try-catch.
 *
 *
 * Следует учитывать важный принцип ___Exception Transparency___ _(прозрачность исключений)_ - ошибки в выходном потоке
 * всегда должны доходить до коллектора. Подробнее можно почитать
 * [тут](https://elizarov.medium.com/exceptions-in-kotlin-flows-b59643c940fb).
 * Каждая реализация Flow должна следовать этому принципу.
 * Другое определение Exception Transparency: нижестоящее исключение всегда должно передаваться коллектору.
 * (То есть, если возникнет ошибка в коллекторе, она должна отлавливаться в коллекторе,
 * а не перехватываться вышестоящим оператором).
 *
 * * Другой способ обработки ошибок - оператор [Flow.catch].
 * Перехватывает исключения при завершении потока и вызывает указанный action с перехваченным исключением.
 * Этот оператор *прозрачен* для исключений, возникающих в нисходящем потоке
 * (то есть игнорирует исключения downstream-а), и не перехватывает исключения, выбрасываемые для отмены потока.
 *
 * The [action] code has [FlowCollector] as a receiver and can [emit][FlowCollector.emit] values downstream.
 * For example, caught exception can be replaced with some wrapper value for errors:
 *
 * ```kotlin
 * flow.catch { e -> emit(ErrorWrapperValue(e)) }
 * ```
 *
 * Пример идеальной обработки ошибок от Романа Елизарова (в случае когда нужно отлавливать все исключения, в том числе
 * и в downstream-е):
 * ```kotlin
 *  fun <T> Flow<T>.handleErrors(): Flow<T> = catch { e -> showErrorMessage(e) }
 *
 *  ...
 *  uiScope.launch {
 *      dataFlow()
 *          .onEach { value -> updateUI(value) }
 *          .handleErrors()
 *          .collect()
 *  }
 *  ...
 *  // или ещё лучше
 *  ...
 *  dataFlow()
 *      .onEach { value -> updateUI(value) }
 *      .handleErrors()
 *      .launchIn(uiScope)
 *  ...
 * ```
 *
 * #
 * ## [SharedFlow] и [StateFlow] - горячие flow
 * [SharedFlow] - это горячий флоу, который эмитит значения всем его подписчикам, как это делает [BroadcastChannel]
 * (_устаревшее апи, которое и призван заменить SharedFlow_).
 *
 * У SharedFlow есть несколько особенностей:
 * * SharedFlow никогда не останавливается, это бесконечный поток данных.
 * То есть при попытке вызвать [SharedFlow.collect] ваша корутина будет остановлена навсегда.
 * * SharedFlow - горячий поток данных, так как эмитит значения сразу после создания.
 * * SharedFlow может иметь множество подписчиков
 * * SharedFlow не имеет контекста выполнения, как следствие вызов [SharedFlow.flowOn] не имеет никакого эффекта на него.
 * * Реализации [SharedFlow] поделены на мутабильные [MutableSharedFlow] и иммутабильные [SharedFlow] реализации.
 *
 * По умолчанию SharedFlow не сохраняет каких-либо значений, а просто служит передатчиком данных.
 * В такой конфигурации любой вызов [MutableSharedFlow.emit] будет приводить к прерыванию корутины до тех пор, пока
 * подписчики не смогут принять значение. Такое поведение не всегда ас устраивает, поэтому в SharedFlow есть опции,
 * которые позволяют гибко настраивать буферизацию.
 *
 * Весь буфер SharedFlow состоит из двух частей:
 * * Replay Cache - элементы, которые будут доставляться всем новым подписчикам
 * * Extra Buffer - элементы сохраняются при наличии подписчиков, когда они не могут быть доставлены сразу же.
 * Очищается при отсутствии подписчиков.
 *
 * Помимо этого можно также управлять поведением буфера при его переполнении
 * с помощью параметра onBufferOverflow [BufferOverflow].
 * По умолчанию используется [BufferOverflow.SUSPEND], которая будет прерывать корутину, из которой происходит вызов
 * [MutableSharedFlow.emit], до тех пор пока не появится подписчик, способный забрать значение.
 * [BufferOverflow.DROP_OLDEST] и [BufferOverflow.DROP_LATEST] при попытке emit-а в случае переполнения буфера будут
 * просто сбрасывать последнее/первое значение в нем, чтобы освободить место для новых данных. При таком поведении
 * операция emit всегда будет выполняться без прерывания, если конечно задан ненулевой размер буфера.
 *
 *
 * [StateFlow] это частный случай [SharedFlow]. Специальный горячий флоу, который хранит одно значение и доставляет его
 * всем подписчикам. Новое значение будет доставляться только, если оно изменилось (то есть oldValue != newValue, иначе
 * emit будет игнорироваться).
 *
 * StateFlow эквивалентен SharedFlow со следующим поведением:
 * ```kotlin
 * // MutableStateFlow(initialValue) is a shared flow with the following parameters:
 * val shared = MutableSharedFlow(
 *     replay = 1,
 *     extraBufferCapacity = 0,
 *     onBufferOverflow = BufferOverflow.DROP_OLDEST
 * )
 * shared.tryEmit(initialValue) // emit the initial value
 * val state = shared.distinctUntilChanged() // get StateFlow-like behavior
 * ```
 *
 * Чаще всего [StateFlow] используют для сохранения состояния элементов UI,
 * например в Android им можно заменить LiveData. Нормальную замену SingleLiveEvent сделать не получится, однако есть
 * статья, описывающая почему
 * [SingleLiveEvent это плохо](https://medium.com/androiddevelopers/viewmodel-one-off-event-antipatterns-16a1da869b95)
 *
 * #
 * ## Backpressure
 * В корутинах такой проблемы нет :)
 * Так как все выполняется по умолчанию последовательно, а если мы решаем использовать buffer, тогда мы явно указываем
 * параметры, что будет происходить в случае его переполнения.
 */
fun main() = mainRunBlocking {
    flowOf(1)
    flowOf(1, 2, 3)
    listOf(1, 2, 3).asFlow()
    flow {
        emit(1) // Ok
        withContext(Dispatchers.IO) {
            emit(2) // Will fail with ISE
        }
    }

    val mutableSharedFlow = MutableSharedFlow<Int>(replay = 1)


    val collectorJob = launch(CoroutineName("collector")) {
        mutableSharedFlow
            .map {
                println("map $it on thread = ${Thread.currentThread()}")
                //map 1 on thread = Thread[DefaultDispatcher-worker-1,5,main]
                it
            }
            .flowOn(Dispatchers.IO)
            .onEach {
                println("onEach: ${it}; currentThread = ${Thread.currentThread()}")
                //onEach: 1; currentThread = Thread[MainDispatcher,5,main]
            }
            .flowOn(Dispatchers.Main)
            .collect()

        //корутина никогда сама не завершится
        println("Никогда не будет напечатано")
    }

    launch(CoroutineName("emitter")) {
        println("emit 1 on Thread ${Thread.currentThread()}")   //emit 1 on Thread Thread[MainDispatcher,5,main]
        mutableSharedFlow.emit(1)
        delay(1000)
        println("emit 2 on Thread ${Thread.currentThread()}")   //emit 2 on Thread Thread[MainDispatcher,5,main]
        mutableSharedFlow.emit(2)
        delay(1000)
        println("emit 3 on Thread ${Thread.currentThread()}")
        mutableSharedFlow.emit(3)
        delay(1000)
        println("emit 4 on Thread ${Thread.currentThread()}")
        mutableSharedFlow.emit(4)

        delay(1000)
        collectorJob.cancel()
    }
}

/**
 * Оператор, который эмитит только изменившиеся значения
 * _Специально так подробно_
 */
fun <T> Flow<T>.unique(): Flow<T> {
    val upstream: Flow<T> = this
    return flow {
        val downstreamFlowCollector: FlowCollector<T> = this
        var previous: Any? = NoValue
        upstream.collect { value: T ->
            if (value != previous) {
                previous = value
                downstreamFlowCollector.emit(value)
            }
        }
    }
}

private object NoValue

//--------------- callbackFlow Example ----------------------------

interface Api<T> {

    fun register(callback: Callback<T>)

    fun unregister(callback: Callback<T>)

    interface Callback<T> {

        fun onNextValue(value: T)

        fun onComplete()

        fun onError(e: Exception)
    }
}

fun <T> Api<T>.asFlow(): Flow<T> {
    val api: Api<T> = this
    return callbackFlow {
        val producerScope: ProducerScope<T> = this
        val callback = object : Api.Callback<T> {
            override fun onNextValue(value: T) {
                producerScope.trySendBlocking(value)    //для одного значения можно вызвать trySend
                    //Тут используем именно trySendBlocking, чтобы заблокировать вызов в том случае, если канал занят
                    //trySend соответственно не блокирует вызов даже если заблокирован канал
                    .onFailure {
                        //log here
                    }
            }

            override fun onComplete() {
                //важно вызвать иначе producerScope.awaitClose будет вечно ждать ещё отправку элементов
                producerScope.channel.close() //или просто producerScope.close()
            }

            override fun onError(e: Exception) {
                producerScope.cancel(CancellationException("API Error", e))
            }

        }
        api.register(callback)
        producerScope.awaitClose { api.unregister(callback) }
    }
}
//--------------------- endregion ---------------------------------