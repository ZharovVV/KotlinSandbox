# Синхронизация в корутинах

Корутины могут выполняться параллельно на нескольких потоках, когда они используют `CoroutineDispatcher`, поддерживающий многопоточность, например `Dispatchers.Default`. В такой ситуации конечно может возникать доступ к одному и тому же ресурсу из нескольких корутин. Чтобы такой код работал корректно вам нужно позаботиться о синхронизации доступа к таким объектам.

# Принципы синхронизации

Все подходы по обеспечению целостности общих ресурсов между несколькими потоками всегда сводятся к тому, чтобы гарантировать, что только один поток может иметь доступ в критическую секцию в момент времени. Это происходит за счет **ограничения доступа**, либо **обеспечения последовательности доступа**, которая может достигаться посредством работы с данными только в одном потоке.
Все потокобезопасные коллекции, atomic-и, синхронизированные коллекции из java могут использоваться, но примитивы а-ля synchronized, lock и т.п уже не будут работать в любом случае.

### Захват блокировки

Синхронизация имеет одно важное свойство: снятие блокировки должно происходить на том же потоке, на котором она и была захвачена. Особенность работы корутин заключается в том, что после вызова suspend-функции она (корутина) может быть продолжена в любом потоке в рамках диспатчера, связанного с ней.
*Критическая секция* - участок исполняемого кода программы, в котором производится доступ к общему ресурсу, который не должен быть одновременно использован более чем одним потоком выполнения.
В критической секции (то есть между захватом и снятием блокировки) не должно быть вызовов suspend-функций! IDEA будет подсвечивать такую ошибку.
Другой способ - это использовать `Mutex` из библиотеки корутин. Они имеют аналогичное поведение с `synchronized`, но в критической секции уже может происходить вызов suspend-функции. Важная особенность: одна и та же корутина не сможет попасть в критическую секцию, в которой она же и захватила блокировку, в отличие от synchronized и lock в java.

```kotlin
suspend fun main() {
		var counter = 0
    val mutex = Mutex()
    supervisorScope {
        val jobs = List(100) {
            launch(start = CoroutineStart.LAZY) {
                repeat(1_000) {
                    mutex.withLock {
                        counter += generateInt()
                    }
                }
            }
        }
        jobs.joinAll()
	      println("goodExample: $counter")//print: goodExample: 100000
    }
}
```

### Последовательный доступ

Способы обеспечения последовательного доступа при помощи корутин:

- **Делать все на одном потоке**. Для этого можно выделить `CoroutineDispatcher`, который будет работать на отдельном выделенном потоке при помощи метода `newSingleThreadContext`. Данная функция создаст специальный `CoroutineContext`, который обязательно должен быть закрыт с помощью вызова `ExecutorCoroutineDispatcher.close` когда он больше вам не нужен. Главное - минимизируете выполнение кода в этом контексте, иначе вы получите последовательные операции и потеряете все преимущества параллельного выполнения.

```kotlin
suspend fun main() {
		var counter = 0
    val counterContext = newSingleThreadContext("Counter")
    supervisorScope {
        val jobs = List(100) {
            launch(start = CoroutineStart.LAZY) {
                repeat(1_000) {
                    withContext(counterContext) {
//                        println(Thread.currentThread()) //Thread[Counter,5,main]
                        counter += generateInt()
                    }
                }
            }
        }
        jobs.joinAll()
        counterContext.close()
        println("singleThreadExample: $counter")//print: singleThreadExample: 100000
    }
}
```

- **Делать все в Dispatchers.Main.** (продолжение пункта выше). Но лучше использовать главный поток только в случае необходимости, когда операция требует выполнения именно на главном потоке, в противном случае лучше создать отдельный поток, чтобы не блокировать UI, а затем закрыть его, когда он будет не нужен.
- **Использовать Channel для синхронизации.** Суть подхода - использование `actor`, который будет служить очередью для выполнения операций. Корутины посылают в него данные и только `actor` может модифицировать и считывать общие данные между корутинами. В итоге благодаря свойствам каналов, обеспечивается последовательное выполнение запросов из нескольких корутин и не важно в каких потоках они работают. **Этот подход и рекомендуется для синхронизации между корутинами.** Обычно его называют ***Синхронизацией через коммуникацию***. Если вам нужно выполнять несколько типов операций над данными, то тут уже применяется *паттерн команда*. Вы можете создать специальный sealed-класс с набором команд. `actor` будет их обрабатывать, а приходить они будут снаружи. Рекомендуется прятать работу с `actor` в виде приятного api.

```kotlin
suspend fun main() {
		val counter = Counter(coroutineContext)
    supervisorScope {
        val jobs = List(100) {
            launch(start = CoroutineStart.LAZY) {
                repeat(1_000) {
                    try {
                        counter += generateInt()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        throw e
                    }
                }
            }
        }
        jobs.joinAll()
    }
    println("channelExample: ${counter.getCount()}")//print: channelExample: 100000
}

class Counter(coroutineContext: CoroutineContext = EmptyCoroutineContext) {

    private val scope = CoroutineScope(coroutineContext)
    private var counter: Int = 0

    private val counterCommands = scope.actor<CounterCommand> {
        //После выполнения block actor-а ActorCoroutine отменяется,
        // а также под капотом отменяется и Channel, которому делегировалась вся работа
        consumeEach { command -> // либо for (command in this) {
            //consumeEach под капотом также вызывает итератор в цикле while(iterator.hasNext()) {...}
            //ChannelIterator.hasNext - это suspend-функция
            //Возвращает true, если в канале есть элементы,
            //приостанавливает вызывающую сторону, пока этот канал пуст,
            // или возвращает false, если канал закрыт для приема без причины.
            when (command) {
                is CounterCommand.Add -> counter += command.count
                is CounterCommand.Remove -> counter -= command.count
                is CounterCommand.Get -> (command.response as CompletableDeferred<Int>).complete(counter)
            }
        }
    }

    suspend fun add(count: Int) {
        counterCommands.send(CounterCommand.Add(count))
    }

    suspend operator fun plusAssign(count: Int) = add(count)

    suspend fun remove(count: Int) {
        counterCommands.send(CounterCommand.Remove(count))
    }

    suspend operator fun minusAssign(count: Int) = remove(count)

    suspend fun getCount(): Int {
        val getCommand = CounterCommand.Get()
        counterCommands.send(getCommand)
        return getCommand.response.await()
    }

    private sealed class CounterCommand {
        class Add(val count: Int) : CounterCommand()
        class Remove(val count: Int) : CounterCommand()
        class Get(val response: Deferred<Int> = CompletableDeferred()) : CounterCommand()
    }
}
```

- **Использовать java.util.concurrent**. Например java.util.concurrent.atomic.AtomicInteger для примера с Counter-ом. Однако стоит помнить, что это часть апи java. Если вы хотите мульти-платформенное решение на корутинах - надо использовать подходы, рекомендуемые авторами библиотеки и являющимися нативными именно для котлина и корутин. Это позволит вам переносить код между различными платформами.
Также стоит отметить, что появилась котлиновская библиотека **atomicfu** (подойдет для KMP)