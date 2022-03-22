package com.github.zharovvv.coroutines

import kotlinx.coroutines.*

fun main() {
    val startTime = System.currentTimeMillis()
    runBlocking {
        launch {
            val startTime1 = System.currentTimeMillis()
            val value1 = simpleSuspendFun1()
            val value2 = simpleSuspendFun2()
            val endTime = System.currentTimeMillis()
            println("coroutine 1: $value1; $value2; ${endTime - startTime1} ms")
            //coroutine 1: value1; value2; 6014 ms
        }
        launch {
            val startTime1 = System.currentTimeMillis()
            val deferred1: Deferred<String> = async {
                simpleSuspendFun1()
            }
            val deferred2: Deferred<String> = async {
                simpleSuspendFun2()
            }
            delay(2000)
            val combinedResult = "" +
                    "${deferred1.await()}; " +
                    deferred2.await() +
                    ""
            val endTime = System.currentTimeMillis()
            println("coroutine 2: $combinedResult; ${endTime - startTime1} ms")
            //coroutine 2: value1; value2; 3007 ms
        }
        println("Start")
    }
    val endTime = System.currentTimeMillis()
    println("End; ${endTime - startTime} ms")   //End; 6048 ms
}

suspend fun simpleSuspendFun1(): String {
    delay(3000)
    return "value1"
}

suspend fun simpleSuspendFun2(): String {
    delay(3000)
    return "value2"
}

