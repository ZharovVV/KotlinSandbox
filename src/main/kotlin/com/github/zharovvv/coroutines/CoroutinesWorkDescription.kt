@file:OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)

package com.github.zharovvv.coroutines

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Ссылки на основные классы:
 * [kotlin.coroutines.jvm.internal.BaseContinuationImpl]
 * [kotlin.coroutines.jvm.internal.ContinuationImpl]
 * [kotlin.coroutines.jvm.internal.CompletedContinuation]
 * [kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED]
 * [kotlin.coroutines.intrinsics.createCoroutineFromSuspendFunction]
 *
 * (when coroutine is RUNNING)
 * * runBlocking$default
 * * -> [BlockingCoroutine.joinBlocking]
 * * -> [EventLoopImplBase.processNextEvent]
 * * -> [DispatchedTask.run]
 * * -> [kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith]
 * * -> invokeSuspend (вызывается у анонимного класса,
 * в которую компилируется suspend-лямбда в блоке кода runBlocking)
 */
fun main() {
    //по умолчанию в качестве параметра метода используется EmptyCoroutineContext
    //создается объект EventLoop (BlockingEventLoop) и добавляется в ThreadLocalEventLoop
    //создается новый контекст, в который кладется eventLoop
    //создается BlockingCoroutine, в конструктор которого передается новый контекст, eventLoop и текущий thread
    //вызывается coroutine.start(...)
    //под капотом вызывается IntrinsicsJvmKt#createCoroutineUnintercepted
    //с этого момента coroutine считается CREATED
    //(далее не совсем понятно, что происходит, но в итоге создается и возвращается объект (какой-то) Continuation)
    //IntrinsicsJvmKt#intercepted -> CoroutineDispatcher#interceptContinuation -> создается DispatchedContinuation
    //DispatchedContinuation#resumeCancellableWith -> CoroutineDispatcher(BlockingEventLoop)#dispatch
    //-> BlockingEventLoop#enqueue (помещаем DispatchedContinuation (который является Runnable) в очередь)
    //
    //вызывается coroutine.joinBlocking() (из этого метода мы выйдем только после завершения
    // всех корутин в блоке runBlocking (там внутри while(true) который достает из очереди runnable))
    //-> eventLoop#processNextEvent (достается runnable из очереди)
    //-> DispatchedTask(DispatchedContinuation).run()
    //-> DispatchedContinuation.continuation(BaseContinuationImpl)#resumeWith
    //с этого момента coroutine считается RUNNING
    //-> invokeSuspend (вызывается suspend-лябда в блоке runBlocking)
    runBlocking {
        //создание StandaloneCoroutine
        //вызывается coroutine.start(...)
        //DispatchedContinuation помещается в очередь
        launch(CoroutineName("1st Coroutine")) {
            println("thread for ${coroutineContext[CoroutineName]?.name}: ${Thread.currentThread()}")
            //thread for 1st Coroutine: Thread[main,5,main]
            //как видно используется не Dispatchers.Default - из-за того, что в скоупе runBlocking задан другой диспатчер

            //блок кода начнет выполняться когда он будет извлечен из очереди
            //(извлечение из очереди происходит в вечном цикле в методе BlockingCoroutine#joinBlocking)
            println(this)

            //Если говорить коротко, то здесь:
            //в очередь _delayed кладется DelayedTask
            //(в цикле в методе BlockingCoroutine#joinBlocking в первую очередь проверяется _delayed)
            //Корутина приостанавливается (COROUTINE_SUSPENDED)
            //....
            //Далее когда в ходе очередной проверки очереди _delayed
            //выясняется, что DelayedTask должен быть выполнен (проходит 1000 мс)
            //DelayedResumeTask достается из очереди и вызывается:
            //DelayedResumeTask.run -> ... -> BaseContinuationImpl.resumeWith
            //-> invokeSuspend (снова вызывается блок кода launch
            // (причем вызывается в месте, идущем сразу после delay,
            // из-за особенности компиляции suspend-лямбды))
            //...
            delay(1000)
            println("first coroutine after delay")
            delay(1000)
            for (i in 0..2) {
                println("first coroutine: $i")
            }
        }
        launch(CoroutineName("2nd Coroutine") + Dispatchers.Default) {
            println("thread for ${coroutineContext[CoroutineName]?.name}: ${Thread.currentThread()}")
            //thread for 2nd Coroutine: Thread[DefaultDispatcher-worker-1,5,main]
        }
        launch {
            println(this)
            for (i in 0..2) {
                println("second coroutine: $i")
            }
        }
        customLaunch {
            println(this)
            delay(100)
            println("custom")
        }
    }
    println("END")
}

/**
 * В месте вызова [customLaunch] при компиляции suspend-лямбда [block] преобразуется к виду:
 * создается анонимный внутренний класс Function2<Object, Object, Object>
 * TODO предположительно Function2<Object(???), Continuation, Object (Вместо Unit по аналогии с suspend-функцией)>
 * + создаются методы, которые используются в функции invoke.
 * ```java
 * new Function2() {
 *  int label;
 *
 *  @Nullable
 *  public final Object invokeSuspend(@NotNull Object $result) {
 *      ...
 *      switch(this.label) {
 *      case 0:
 *          this.label = 1;
 *          <вызов suspend-функции>
 *      case <количество suspend-функций в блоке кода>:
 *          ...
 *      }
 *      ...
 *  }
 *
 *  @NotNull
 *  public final Continuation create(@Nullable Object value, @NotNull Continuation completion) {...}
 *
 *  public final Object invoke(Object var1, Object var2) {
 *      return ((<undefinedtype>)this.create(var1, (Continuation)var2)).invokeSuspend(Unit.INSTANCE);
 *  }
 * }
 * ```
 * TODO очень похоже что на самом деле suspend-лямбды представлены классом
 * [kotlin.coroutines.jvm.internal.SuspendLambda], а также реализует интерфейс
 * Function2<Object, Object, Object>
 */
fun CoroutineScope.customLaunch(block: suspend CoroutineScope.() -> Unit): Job {
    val defaultCoroutineContext: CoroutineContext = EmptyCoroutineContext
    val coroutineStart: CoroutineStart = CoroutineStart.DEFAULT
    val context = newCoroutineContext(defaultCoroutineContext)
    val coroutine: AbstractCoroutine<Unit> = CustomCoroutine(context, active = true)
    coroutine.start(
        start = coroutineStart,
        receiver = coroutine as CoroutineScope,
        block = block
    )
    return coroutine
}

//Copy of StandaloneCoroutine
class CustomCoroutine(
    parentContext: CoroutineContext,
    active: Boolean
) : AbstractCoroutine<Unit>(parentContext, initParentJob = true, active = active) {
    override fun handleJobException(exception: Throwable): Boolean {
        handleCoroutineException(context, exception)
        return true
    }
}