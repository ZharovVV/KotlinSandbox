package com.github.zharovvv.coroutines.lesson5

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.channels.Channel.Factory.RENDEZVOUS
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.receiveAsFlow
import java.util.concurrent.BlockingQueue

/**
 * # [Channel]
 * Для передачи значений между корутинами мы можем использовать async-await, но они позволяют работать лишь с одним
 * значением. Когда нужно работать с несколькими, нужно использовать [Channel] и [Flow].
 * #
 * ## Что такое Channel?
 * Каналы это концепт, схожий с [BlockingQueue] из Java.
 *
 * _Немного про BlockingQueue:_
 *
 * BlockingQueue - это очередь Queue со следующими дополнениями:
 * * Блокирующая операция take() - берет следующий элемент,
 * если же очередь пустая - блокирует выполнение до появления элемента (до момента, когда перестанет быть пустой)
 * * Блокирующая операция put(E e) - помещает элемент в очередь,
 * если очередь заполнена - блокирует выполнение до освобождения места в очереди
 * и успешного помещения в нее нового элемента.
 *
 * Ключевая разница - то что вместо блокирующих потоков операций, [Channel] построены на основе корутин и поэтому
 * используют прерывания. В основном используются два основных метода:
 * * [Channel.send] - для отправки значений в канал
 * * [Channel.receive] - для получения значений из канала
 * Получить значения из канала можно не только с помощью [Channel.receive], но и просто с помощью цикла итерировать
 * по нему (по каналу).
 *
 * send и receive - это suspend-функции, но с каналами можно также работать и обычными способами, используя функции:
 * * [Channel.trySend]
 * * [Channel.tryReceive]
 *
 * Эти методы возвращают value class [ChannelResult], у которого есть методы для проверки статуса выполнения операции:
 * * [ChannelResult.isSuccess] - получилось отправить/получить данные
 * * [ChannelResult.isFailure] - произошла неуспешная операция, но это не означает, что канал закрыт или неисправен.
 * (Например если была попытка отправить значение в заполненный канал.)
 * * [ChannelResult.isClosed] - канал был закрыт
 *
 * #
 * ## Закрытие Channel
 * По умолчанию канал будет работать до вызова функции [Channel.close].
 * Под капотом происходит отправка специального значения для индикации того, что в канале больше ничего не будет.
 * При попытке считать значение из закрытого канала будет выброшено исключение. Вы можете проверить доступен ли канал
 * на получение/отправку данных с помощью функций [Channel.isClosedForReceive] и [Channel.isClosedForSend].
 *
 * Одновременно у канала может быть больше одного получателя, так и больше одного отправщика.
 * Каналы рассчитаны на работу с несколькими корутинами.
 *
 * __Важно помнить__, что channel может приводить к приостановке корутины, пока он не сможет обработать значение на
 * отправку или получение. Соответственно если есть такое прерывание - оно будет останавливать корутину.
 * Чтобы этого не происходило нужно помнить, что channel,
 * когда он больше не нужен - __обязательно нужно останавливать__ и обрабатывать потенциальную ошибку, которая может
 * произойти в [Channel.send] или [Channel.receive]. _(см. пример [badExample])_
 *
 * #
 * ## Операторы Channel в ранних версиях kotlinx.coroutine
 * В ранних версиях было множество операторов, для модификации потока данных. Однако с появлением [Flow] эта возможность
 * была убрана, и каналы остались только для своей непосредственной цели:
 * __коммуникация между несколькими корутинами__. Хотите работать с реактивными потоками данных - используйте [Flow].
 *
 * #
 * ## Channel без буфера
 * По умолчанию у каналов нет буфера (используется capacity = [RENDEZVOUS] (т.е. 0)).
 * Получается, что при вызове [SendChannel.send] он (вызов) каждый раз будет приостановлен и не сможет отправить
 * значение получателю. Должно состояться некое "свидание" отправителя с получателем.
 *
 * Стандартные размеры буфера:
 * * [Channel.RENDEZVOUS] - без буфера. Размер для любого Channel по умолчанию.
 * * [Channel.CONFLATED] - Размер буфера - 1. Хранит только последнее полученное значение, а предыдущее удаляется.
 * Выбирать политику переполнения буфера при таком типе capacity мы не можем.
 * * [Channel.BUFFERED] - Задает стандартный размер буфера, который определен в свойствах окружения (по умолчанию это 64)
 * * [Channel.UNLIMITED] - Максимально возможный размер буфера - [Int.MAX_VALUE].
 *
 * Политики поведения при переполнении буфера:
 * * [BufferOverflow.SUSPEND] - корутина будет приостановлена, если принять значение некому, а буфер переполнен или
 * отсутствует. Политика по умолчанию.
 * * [BufferOverflow.DROP_OLDEST] - Удаляет самые старые значения в буфере при его переполнении.
 * Вызов send никогда не приостановит корутину, а trySend всегда успешно выполнится, при условии наличия буфера в Channel.
 * * [BufferOverflow.DROP_LATEST] - Удаляет самые новые значения в буфере при его переполнении.
 *
 * Логично, что __не все политики поведения при переполнении буфера могут быть использованы с любым размером буфера.__
 *
 * #
 * ## [actor] & [produce]
 * Зачастую, вам не придется в коде использовать Channel(...) (создавать его явно).
 * Вы встретитесь с билдерами [produce] и [actor], которые возвращают специальные каналы:
 * [ReceiveChannel] и [SendChannel] соответственно.
 *
 * [produce] нужен для генерации конечных или бесконечных потоков значений.
 * [actor] используется для получения значений извне и для их упорядоченной обработки.
 * (Они очень пригодятся для синхронизации работы нескольких корутин.)
 *
 * #
 * ## [Channel] vs [Flow]
 * Каналы позволяют получать и передавать значения извне, а также обеспечивают работу с несколькими корутинами
 * одновременно и делают это безопасно. Flow же служат простым горячим потоком данных (речь о замене некоторых Channel
 * на специальные Flow), которые за счет других требований имеют производительность выше и более простое API.
 * Также каналы используются для создания некоторых операторов Flow и обмена данными между несколькими корутинами.
 *
 */
@OptIn(ExperimentalCoroutinesApi::class, ObsoleteCoroutinesApi::class)
fun main() {
    val scope = CoroutineScope(SupervisorJob())
    scope.launch {
        `example RENDEZVOUS`()
        `example CONFLATED`()
        val sendChannel: SendChannel<Int> = actor {
            val data = receiveAndLog()  //receive = 2002
        }
        sendChannel.send(2002)
        println(sendChannel.isClosedForSend) //false
        try {
            sendChannel.send(100500)
        } catch (e: Exception) {
            e.printStackTrace() //java.util.concurrent.CancellationException: RendezvousChannel was cancelled
            throw e
        }
        val receiveChannel: ReceiveChannel<Int> = produce {
            send(2003)
        }
        receiveChannel.receiveAndLog() //receive = 2003
    }.invokeOnCompletion {
        scope.cancel()
    }
    while (scope.isActive) {
    }
}

suspend fun channelIterator(channel: Channel<Int>) = coroutineScope {
    for (value: Int in channel) {
        println(value)
    }
    //channel.forEach {} не определен для Channel.
}

//вечно будет выполняться
suspend fun badExample() = coroutineScope {
    //Имитируем переполнение буфера
    val channelWithoutBuffer: Channel<Int> = Channel(capacity = 0)  //same Channel(capacity = RENDEZVOUS)
    //отправляем значение в Channel
    launch {
        try {
            channelWithoutBuffer.send(1)
        } catch (e: ClosedSendChannelException) {
            //Обрабатываем ошибку в случае закрытия канала
            e.printStackTrace()
        }
    }
    // Забываем вызвать channelWithoutBuffer.close()

    // launch все также работает и приостановлен
    // потому что channel не может обработать
    // и не был остановлен
}

fun channelToFlow() {
    val receiveChannel: ReceiveChannel<Any?> = Channel<Any?>().apply { trySend(100500) }

    // Горячий Flow
    // Значения могут быть получены только один раз
    // (при повторном вызове collect у Flow будет креш)
    val flowCollectOnce: Flow<Any?> = receiveChannel.consumeAsFlow()

    // Горячий Flow.
    // Может использоваться для нескольких получателей значений.
    // (множественный вызов collect)
    val flowCollectManyTimes = receiveChannel.receiveAsFlow()
}

@Suppress("FunctionName")
suspend fun `example RENDEZVOUS`(): Unit = coroutineScope {
    val channel = Channel<Any?>(
        // Размер буфера
        capacity = RENDEZVOUS, //рандеву = свидание :)
        // Что делать при переполнении буфера
        onBufferOverflow = BufferOverflow.SUSPEND,
        // Сюда попадут все значения, которые не доставили
        onUndeliveredElement = null
    )
    launch {
        println("try receive...")
        println("receive = ${channel.receive()}; receiver1")    //receive = 123; receiver1
    }
    launch {
        delay(3000)
        val value = 100500
        println("send = $value")
        channel.send(value)
    }
    launch {
        delay(1500)
        val value = 123
        println("send = $value")
        channel.send(value)
    }
    launch {
        println("try receive...")
        println("receive = ${channel.receive()}; receiver2") //receive = 100500; receiver2
    }
    //Порядок получателей может быть другим, так как запускаться корутины будут в разных потоках.
    //Но точно будет получено сначала 123, затем 100500.
}

@Suppress("FunctionName")
suspend fun `example CONFLATED`(): Unit = coroutineScope {
    val chanel = Channel<Any?>(capacity = CONFLATED) // capacity = 1
    launch { chanel.sendAndLog(1) }
    launch { chanel.sendAndLog(2) }
    launch { chanel.sendAndLog(3) }
    launch { chanel.sendAndLog(4) }
    launch { chanel.sendAndLog(5) }
    launch { chanel.sendAndLog(6) }
    launch { chanel.sendAndLog(7) }
    launch { chanel.sendAndLog(8) }
    launch { chanel.sendAndLog(9) }
    launch {
        chanel.receiveAndLog()
    }
}

suspend fun <T> ReceiveChannel<T>.receiveAndLog(): T = receive().also { println("receive = $it") }

suspend fun <T> SendChannel<T>.sendAndLog(value: T) {
    send(value)
    println("send = $value")
}
