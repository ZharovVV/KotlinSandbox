package com.github.zharovvv.multithreading.atomic

import java.util.concurrent.atomic.AtomicInteger

fun main() {
    val atomicInteger = AtomicInteger(0)
    atomicInteger.compareAndSet(0, 1)
    atomicInteger.getAndIncrement()
}

