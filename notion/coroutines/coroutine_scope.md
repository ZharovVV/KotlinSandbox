# CoroutineScope

Жизненный цикл для выполнения асинхронных операций.

# Жизненый цикл

Любая асинхронная операция, которая запускается в вашем приложении, должна быть остановлена, когда результат её выполнения больше вам не нужен. Это позволит не занимать устройству лишнее ресурсы и работать устройству быстрее.
В потоках мы бы использовали для этого Thread.interrupt, в RxJava - Disposable.dispose (clear). Их проблема - это неудобство API. Вам необходимо сохранять ссылки на объекты через которые происходит отмена, причем это опционально и апи вас не заставляет делать это обязательно.
В корутинах же для этого сделали `CoroutineScope`. Он является ЖЦ и ответственнен за все дочерние корутины в рамках него, а также за время их работы. Все запускающиеся корутины должны быть привязаны к какому-то корутин-скоупу. Как следствие все корутин билдеры (`launch`, `async`) являются расширениями `CoroutineScope`. Исключением является лишь `runBlocking`. Это логично, ведь это билдер предназначенный для блокировки потока, с которого запускается корутина, пока она не будет закончена. Это некий мост между блокирующим подходом и подходом основанным на приостановке (т.е. корутинах). Очень важно использовать `runBlocking` только в тестах и в функции `main`. В любых других случаях это может привести к проблемам.

# Structured concurrency

Механизм, предоставляющий иерархическую структуру для организации работы корутин. Практически все принципы structured concurrency строятся на основе `CoroutineScope` (а под капотом - через отношения родитель-ребенок у Job).
Принципы работы CoroutineScope в следующем:

- **Отмена Scope - отмена корутин**. Scope может отменить выполнение всех дочерних корутин, если возникнет ошибка или операция будет отменена.
- **Scope знает про все корутины.** Любая корутина, запускаемая в скоупе, будет храниться ссылкой в нём через отношение родитель-ребенок у Job.
- Scope автоматически ожидает выполнения всех дочерних корутин, но не обязательно завершается вместе с ними.

# CoroutineScope vs CoroutineContext

```kotlin
public interface CoroutineScope {
    public val coroutineContext: CoroutineContext
}
```

Целевое назначение является главным отличием между контекстом и скоупом. `CoroutineContext` предназначен для конфигурирования корутины. `CoroutineScope` же предназначен для объединения всех запущенных корутин в рамках него, а также под капотом передает им `Job`, который будет их объединять, и будет родительской для всех запущенных корутин в скоупе. Также скоуп является источником элементов (`CoroutineContext.Element`) контекста для построения корутин по умолчанию. Например так можно передать дефолтный диспатчер для всех корутин в скоупе.

# GlobalScope (Опасно!)

Специальный `CoroutineScope`, который не привязан к какой-либо `Job` (поэтому отменить его невозможно). Все корутины, запущенные в рамках него, будут работать до своей остановки или остановки процесса. Использование этого скоупа может легко привести к утечке памяти и нарушает принципы structured concurrency. Настоятельно рекомендуется не использовать данный скоуп, а создавать собственный, который будет привязан к жизни вашего приложения. (Например для андроид можно создать скоуп, который будет привязан к жизни application.)

# Создание `CoroutineScope`

Создавать скоупы можно через одноименную функцию `CoroutineScope(context).` Любой корутин-контекст обязательно должен иметь в себе объект типа `Job`. Если не передавать его явно, то при создании скоупа он добавится автоматически (Это будет `JobImpl`, который создается методом `Job()`). Обычно используют `SupervisorJob` вместо обычных джоб, т.к. в этом случае ошибки из любой дочерней корутины не приведут к остановке всех корутин в скоупе.
Также скоуп можно создать при помощи suspend-функции `coroutineScope`. Обычно используется когда в рамках suspend-функции нужно запустить корутину. Для создания нового скоупа функция `coroutineScope` возьмет контекст из родителя, добавит к нему Job, который, связан с внешним скоупом. Полученный новый скоуп работает по следующим правилам:

- Если произойдет креш внутри скоупа, то он пробросится родительскому скоупу.
- Остановка родительского scope приведет к остановке scope, полученного в coroutineScope.
- Функция coroutineScope приостановит выполнение корутины до тех пор пока все корутины и весь код внутри не будут выполнены.

Если хотим получить поведение, аналогичное `coroutineScope`, но при этом не получать каскадную остановку корутин - следует использовать функцию `supervisorScope`, что является полным аналогом `coroutineScope`, но вместо `Job` будет использоваться `SupervisorJob`. Важно! Такие скоупы не подойдут для выполнения операций дольше чем родительский скоуп.

# CoroutineScope внутри корутины

Каждая корутина передает внутри себя CoroutineScope, что позволяет вызывать другие корутины внутри неё. Этот скоуп формируется по следующим правилам:

- берется корутин-контекст из скоупа, в котором запускается корутина;
- к нему добавляются все элементы из контекста, переданного в корутин-билдер
- при получении Job из корутины мы будем получать текущую корутину.

```kotlin
val job = launch {
    //job == job1
    val job1 = coroutineContext[Job]
}
```

# Отмена CoroutineScope

`CoroutineScope.cancel` - отменяет scope и все запущенные в рамках него корутины. При попытке запустить любую корутину на отмененном скоупе корутина сразу будет останавливаться с ошибкой. Если же мы хотим отменить все корутины в рамках скоупа, но скоуп должен остаться живым нужно сделать так:

```kotlin
scope.coroutineContext.cancelChildren()
```

# Смена CoroutineContext

Если в рамках выполнения корутины вам надо сменить контекст для части кода внутри, то для этого необязательно запускать новую корутину, а лучше использовать suspend-функцию `withContext`. Рекомендуется использовать данную функцию при определении любой suspend-функции. Например если в suspend функции требуется загрузить данные, то явно указываем `Dispatchers.IO`.