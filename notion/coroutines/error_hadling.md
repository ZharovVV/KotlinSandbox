# Обработка ошибок в корутинах

# try-catch

Для обработки ошибок используется обычный **try-catch**. Однако `launch` и `async` нужно обрабатывать по-разному. Все исключения, которые будут происходить в рутовой корутине, будут приводить к поведению, аналогичному в java: будет вызываться `Thread.UncaughtExceptionHandler`. Это интерфейс для обработки ошибок при завершении потока из-за неперехваченного исключения.
Все исключения, которые вы не обработаете, будут доставлены родительской корутине и скоупу, свянному с ней и приводить к остановке с ошибкой. Если вы в рамках скоупа используете `Job`, то дочерние корутины будут также остановлены, но без ошибки. Чтобы этого не происходило используйте `SupervisorJob`. В таком случае ошибка в дочерней джобе не приведет к отмене других дочерних джоб, а также самого родителя.
Чтобы перехватывать исключения во всем скоупе, можно обернуть весь `coroutineScope` или `supervisorScope` try-catch-м:

```kotlin
try {
    coroutineScope {
        ...
    }
} catch (e: Exception) {
    //Обрабатываем все исключения в scope
}
```

# Что не так async-await

Даже при обработке исключения как положено в документации в `async` - оно все равно приведет к остановке родительской корутины. Есть несколько способов избежать этого:

- обернуть вызов в `supervisorScope` (`coroutineScope` не поможет)
- подкинуть в async новый `Job`/`SupervisorJob`-контекст выполнения в корутину. В этом случае разорвется связь с родительским скоупом. (Не стоит так делать - это нарушает принципы structured concurrency.)

# **Распространение исключений**

Сборщики сопрограмм бывают двух видов: автоматическое распространение исключений ( [launch](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/launch.html) ) или предоставление их пользователям ( [async](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/async.html) и [produce](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.channels/produce.html) ). Когда эти построители используются для создания ***корневой*** сопрограммы, которая не является ***дочерней*** по отношению к другой сопрограмме, первые построители рассматривают исключения как **неперехваченные** исключения, аналогично Java `Thread.uncaughtExceptionHandler`, в то время как вторые полагаются на то, что пользователь воспользуется окончательным исключением, например, через [await](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-deferred/await.html) или [receive](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.channels/-receive-channel/receive.html) ( [produce](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.channels/produce.html) и [receive](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.channels/-receive-channel/receive.html) описаны в разделе [«Channels»](https://github.com/Kotlin/kotlinx.coroutines/blob/master/docs/channels.md))

# Уведомление об ошибке в корутине

```kotlin
job.invokeOnCompletion { cause: Throwable? ->
    if (cause != null) {
        // Произошла ошибка (или отмена, если cause это CancellationException) 
    } else {
        // Корутина была успешно выполнена
    }
}
```

Этот способ не позволит обработать исключение, а лишь уведомит, что корутина выполнилась успешно или с ошибкой.

# Отмена - это тоже ошибка

`CancellationException` - специальное исключение для обработки отмены выполнения корутин. Вызов `CoroutineScope.cancel`, `CoroutineContext.cancel`, `Job.cancel` приводит к созданию экземпляра `CancellationException`.

> `CancellationException` выбросится не сразу после вызова cancel, а через какое-то время. Обычно до первого вызова `Job.ensureActive` .
Например в `withContext` эта проверка выполняется перед вызовом переданного блока кода и в конце. Функция `delay` же в случае отмены, сгенерирует `CancellationException` практически сразу после вызова cancel, не дожидаясь завершения выполнения.
> 

Но его обработка происходит иначе и этот код зашит в корутины и не может быть изменен. Отмена `Job`, которым представляется корутина, не приведет к отмене её родителя, и она (корутина-родитель) и другие дочерние джобы продолжат выполняться дальше.
Если вы хотите обрабатывать все исключения внутри корутины, написав код с try-catch на все эксепшены, то ветку с CancellationException нужно написать отдельно, чтобы не нарушать работу того, как происходит отмена.

```kotlin
launch {
    try {
        ...
    } catch (e: CancellationException) {
        //обязательно пробрасываем дальше
        throw e
    } catch (e: Exception) {
        //обрабатываем другие исключения
    }
}
```

Исходя из вышеописанного можно сделать вывод, что `runCatching` из kotlin для обработки ошибок в корутинах - не подойдет. Вместо этого можно использовать кастомную функцию-расширение:

```kotlin
inline fun <T, R> cancellableRunCatching(block: T.() -> R): Result<R> = try {
		Result.success(block())
} catch (cancellationException: CancellationException) {
		throw cancellationException
} catch (exception: Exception) {
		Result.failure(exception)
}
```

# CoroutineExceptionHandler

Вы можете определить поведение для всех необработанных исключений, которые происходят в текущем контексте выполнения корутин. Для этого можно использовать класс `CoroutineExceptionHandler`, который служит перехватчиком любого исключения. Однако стоит учитывать, что повлиять на выполнение корутин таким способом - нельзя. `CoroutineExceptionHandler` вызывается в последнюю очередь, после того как произошла ошибка, причем может быть вызвана на любом потоке.

```kotlin
val coroutineExceptionHandler = CoroutineExceptionHandler { context, error: Throwable ->
    logError(error)
}
CoroutineScope(coroutineExceptionHandler)
scope.launch(coroutineExceptionHandler) { ... }
```

Также стоит помнить, что всегда будут выполняться CoroutineExceptionHandler-ы, найденные через **Java ServiceLoader**, а также `Thread.UncaughtExceptionHandler`-ы. Обычно его стоит использовать для логирования необработанных ошибок, например отправка их в аналитику. Также `CoroutineExceptionHandler` не будет уведомляться об ошибках `CancellationException`.

# Выполнение finally

```kotlin
val inputStream: InputStream
try {
    doSomethingLong(inputStream)
} catch (e: Exception) {
    //Обрабатываем исключение
} finally {
    //корутина может быть отменена
    // и при вызове suspend-функции произойдет остановка выполнения корутины
    withContext(NonCancellable) {
        shutdown(inputStream)
    }
}
```

Порой, нам нужно гарантированно выполнить какой-то код, даже в случае отмены корутины. Как раз для этого есть тип `Job` - `NonCancellable`. Вы должны запустить suspend-функцию в контексте с ней, и она успешно выполнится, даже в случае отмены корутины. Обычно такое используют в блоках **finally**. Но этим лучше не увлекаться.

**Важно!** Это API сделано только для использования с `withContext`. Попытка передать его в CoroutineScope, launch и т.п. приведет к нарушению принципов *structured concurrency*.

# Дополнительные материалы

[Exceptions in coroutines](https://medium.com/androiddevelopers/exceptions-in-coroutines-ce8da1ec060c)

Так например, там описано, что передача `SupervisorJob` в качестве аргумента в coroutine builder не будет иметь желаемого эффекта. `SupervisorJob` работает так как описано только если он является частью `CoroutineScope`, т.е когда скоуп создан через `supervisorScope`, либо через `CoroutineScope(SupervisorJob)`. Ещё важный тезис из статьи: необработанные исключения всегда доставляются родительской джобе, поэтому `CoroutineExceptionHandler` (который должен отлавливать ошибки в дочерних корутинах) нужно устанавливать в контексте родительской корутины.

```kotlin
val scope = CoroutineScope(Job())
scope.launch(handler) {
    launch {
        throw Exception("Failed coroutine") //будет обработано handler-ом
    }
}
...
val scope = CoroutineScope(Job())
scope.launch {
    launch(handler) {
        throw Exception("Failed coroutine") //не будет обработано handler-ом
        //Так как handler установлен не в том контексте.
        // Исключение распространится до родительской корутины,
        // а поскольку в контексте родителя хендлер не задан,
        // исключение не будет обработано.
    }
}
```