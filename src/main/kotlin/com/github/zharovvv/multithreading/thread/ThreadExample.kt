package com.github.zharovvv.multithreading.thread

@Volatile
var volatileVar: String = ""


fun main() {
    val mainThread = Thread.currentThread()
    println(mainThread.stackTrace.forEach(::println))
    val thread1 = Thread {
        try {
            Thread.sleep(3000L)
        } catch (e: InterruptedException) {
            //Корректная обработка InterruptedException
            //При перехвате исключения InterruptedException в catch статус потока автоматически сбрасывается
            // и Thread.currentThread().isInterrupted() вернет false.
            //Поэтому мы повторно устанавливаем флаг потока, сигнализирующий о прерывании.
            Thread.currentThread().interrupt()
            /* Прерывание потока осуществляется при помощи метода Thread.interrupt().
             Существует два способа которыми JVM уведомляет поток о том, что его прерывают.
             Первый — это собственно InterruptedException.
             Второй — это флаг потока INTERRUPT, который может быть получен при помощи метода Thread.isInterrupted().
             Игнорирование второго метода сигнализирования о прерывании и является типичной ошибкой.*/
        }
    }
    val thread2 = Thread {
        while (true) {

        }
    }
    thread1.start()
    thread2.start()
    thread1.join()
    thread2.join()
}