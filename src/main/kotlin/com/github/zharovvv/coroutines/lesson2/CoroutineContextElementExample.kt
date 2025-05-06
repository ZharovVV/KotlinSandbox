package com.github.zharovvv.coroutines.lesson2

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ThreadContextElement
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

fun main() {
    runBlocking {
        launch(MyCustomContextElement()) {
            println("before delay")
            withContext(Dispatchers.Default) {
                delay(3000L)
            }
            println("after delay")
        }
    }
}

class MyCustomContextElement : ThreadContextElement<Unit> {

    companion object Key : CoroutineContext.Key<MyCustomContextElement>

    override val key: CoroutineContext.Key<*> get() = Key
    override fun updateThreadContext(context: CoroutineContext) {
        println("[${Thread.currentThread().name}] updateThreadContext")
    }

    override fun restoreThreadContext(context: CoroutineContext, oldState: Unit) {
        println("[${Thread.currentThread().name}] restoreThreadContext")
    }


}