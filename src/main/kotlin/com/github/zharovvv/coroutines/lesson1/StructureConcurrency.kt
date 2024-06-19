package com.github.zharovvv.coroutines.lesson1

import com.github.zharovvv.coroutines.lesson2.mainRunBlocking
import com.github.zharovvv.coroutines.lesson2.state
import kotlinx.coroutines.*

fun main() = mainRunBlocking {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    val parentJob = scope.coroutineContext[Job]!!
    val childJob1 = scope.launch {
        try {
            delay(3000)
        } catch (e: Exception) {
            println("childJob1 catch exception: $e")
            throw e
        }
    }
    val childJob2 = scope.launch {
        throw CancellationException()
//        throw IllegalStateException()
    }
    val childJob3 = scope.launch {
        try {
            withContext(Dispatchers.IO) {
                Thread.sleep(10000)
            }
        } catch (e: Exception) {
            println("childJob3 catch exception: $e")
            throw e
        }
        while (true) {
            //
        }
    }
    delay(1000)
    println("parentJob.status = ${parentJob.state}")
    println("childJob1.status = ${childJob1.state}")
    println("childJob2.status = ${childJob2.state}")
    println("childJob3.status = ${childJob3.state}")
    delay(1000)
    parentJob.cancel()
    println("Cancel parent job")
    println("parentJob.status = ${parentJob.state}")
    println("childJob1.status = ${childJob1.state}")
    println("childJob2.status = ${childJob2.state}")
    println("childJob3.status = ${childJob3.state}")
    parentJob.invokeOnCompletion {
        println("parentJob.status = ${parentJob.state}")
        println("childJob1.status = ${childJob1.state}")
        println("childJob2.status = ${childJob2.state}")
        println("childJob3.status = ${childJob3.state}")
    }
    parentJob.join()
}