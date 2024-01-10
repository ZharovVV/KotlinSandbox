package com.github.zharovvv.multithreading.synchronizedd

import java.util.*

fun main() {
    val pc = ProducerConsumer<String>()
    val t1 = Thread {
        (0..43)
            .map(Int::toString)
            .forEach {
                pc.produce(it)
                println("produce $it")
            }
    }
    val t2 = Thread {
        repeat(44) {
            val result = pc.consume()
            println("consume $result")
        }
    }
    t1.start()
    t2.start()
    t1.join()
    t2.join()
}

internal class ProducerConsumer<T> : Object() {

    private val queue: Deque<T> = ArrayDeque()

    @Synchronized
    fun produce(value: T) {
        while (queue.size == BUFFER_MAX_SIZE) {
            wait()
        }
        queue.add(value)
        notify()
    }

    @Synchronized
    fun consume(): T {
        while (queue.size == 0) {
            wait()
        }
        val result = queue.remove()
        notify()
        return result
    }

    private companion object {
        const val BUFFER_MAX_SIZE = 42
    }
}