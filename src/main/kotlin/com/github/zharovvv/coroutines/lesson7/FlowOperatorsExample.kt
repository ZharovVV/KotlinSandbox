package com.github.zharovvv.coroutines.lesson7

import com.github.zharovvv.coroutines.lesson2.mainRunBlocking
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@OptIn(FlowPreview::class, DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
fun main() = mainRunBlocking {
    val singleThreadDispatcher = newSingleThreadContext("")
    val limitedDispatcher = Dispatchers.Default.limitedParallelism(parallelism = 1)
    val startTime = System.currentTimeMillis()
    listOf("AAAA", "BBB", "CC", "D")
        .asFlow()
        .flatMapConcat { value: String ->
//            println("flatMapMergeBlock; coroutineContext = ${currentCoroutineContext()}")
            createDelayedCharFlow(source = value)
        }
        .flowOn(
            singleThreadDispatcher
        )
        .onEach { char -> print(char) } // DACB - BCA - BA - A
        .onCompletion {
            val duration = System.currentTimeMillis() - startTime
            println("\nduration: $duration ms") //duration: 4061 ms
        }
        .launchIn(this)
    listOf("AAAA", "BBB", "CC", "D")
        .asFlow()
        .flatMapConcat { value: String ->
//            println("flatMapMergeBlock; coroutineContext = ${currentCoroutineContext()}")
            createDelayedCharFlow(source = value)
        }
        .flowOn(
            singleThreadDispatcher
        )
        .onEach { char -> print(char) } // DACB - BCA - BA - A
        .onCompletion {
            val duration = System.currentTimeMillis() - startTime
            println("\nduration: $duration ms") //duration: 4061 ms
        }
        .launchIn(this)
}

private fun createDelayedCharFlow(source: String): Flow<Char> {
    return flow {
//        println("charFlow; coroutineContext = ${currentCoroutineContext()}")
        source.asIterable().forEachIndexed { index: Int, char: Char ->
//            delay(index * 1000.toLong())
            emulateLongBlockingOperation()
            emit(char)
        }
    }
        .flowOn(Dispatchers.IO)
}

private suspend fun emulateLongBlockingOperation() {
//    println("emulateLongBlockingOperation; coroutineContext = ${currentCoroutineContext()}")
    withContext(Dispatchers.IO) {
//        println("emulateLongBlockingOperation(withContext); coroutineContext = ${currentCoroutineContext()}")
        Thread.sleep(1000)
    }
}