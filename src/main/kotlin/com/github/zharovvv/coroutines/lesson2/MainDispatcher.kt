package com.github.zharovvv.coroutines.lesson2

import kotlinx.coroutines.*
import kotlinx.coroutines.internal.MainDispatcherFactory
import kotlin.coroutines.CoroutineContext

//@OptIn(InternalCoroutinesApi::class)
class MainDispatcherFactoryImpl : MainDispatcherFactory {

    override val loadPriority: Int = 1

    override fun createDispatcher(allFactories: List<MainDispatcherFactory>): MainCoroutineDispatcher {
        return MainCoroutineDispatcherImpl()
    }
}


@OptIn(DelicateCoroutinesApi::class)
//Не требуется, так как согласие на использование апи дано на уровне всего модуля
//См. build.gradle.kts - файл
//upd: снова требуется, т.к. удалил согласие на уровне модуля
class MainCoroutineDispatcherImpl : MainCoroutineDispatcher() {

    private val dispatcher = newSingleThreadContext("MainDispatcher")

    override val immediate: MainCoroutineDispatcher get() = this

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        dispatcher.dispatch(context, block)
    }
}

@DelicateCoroutinesApi
fun mainRunBlocking(block: suspend CoroutineScope.() -> Unit) = runBlocking(
    context = SupervisorJob() + Dispatchers.Main,
    block = block
)