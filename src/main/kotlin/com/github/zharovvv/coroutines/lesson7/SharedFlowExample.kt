package com.github.zharovvv.coroutines.lesson7

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.zip

fun main() {
    runBlocking {
        val sharedFlow1 = MutableSharedFlow<Int>(
//            extraBufferCapacity = 1,
//            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
        val job1 = launch(Dispatchers.IO) {
            var counter = 0
            while (true) {
                sharedFlow1.emit(counter++)
                delay(100)
            }
        }

        val job2 = launch(Dispatchers.IO) {
            val flow = ('a'..'z').asFlow()
            flow.zip(sharedFlow1) { char, int -> "$char $int" }
                .collect {
                    delay(1000)
                    println("currTime = ${System.currentTimeMillis()}: " + it)  //a 1 -> b 2 -> c 10 -> d 20
                }
        }
        joinAll(job1, job2)
    }
}