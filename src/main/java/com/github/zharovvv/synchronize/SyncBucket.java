package com.github.zharovvv.synchronize;

import java.util.ArrayList;
import java.util.List;

public class SyncBucket {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    /**
     * Переменная, помеченная ключевым словом volatile:
     * 1) всегда будет атомарно читаться и записываться. Даже если это 64-битные double или long.
     * (long и double — самые «тяжеловесные» примитивы в Java: они весят по 64 бита.
     * И в некоторых 32-битных платформах просто не реализована атомарность чтения и записи 64-битных переменных.
     * Такие переменные читаются и записываются в две операции.
     * Сначала в переменную записываются первые 32 бита, потом еще 32.)
     * <p>
     * 2) Java-машина не будет помещать ее в кэш.
     * Так что ситуация, когда 10 потоков работают со своими локальными копиями исключена.
     */
    private volatile long volatileX = 2222222222222222222L;

    private Object val;

    public Object getVal() {
        return val;
    }

    public void setVal(Object val) {
        this.val = val;
    }

    public static void main(String[] args) {
        SyncBucket syncBucket = new SyncBucket();
        List<Thread> threads = new ArrayList<>();
        for (int i = 1; i <= 100; ++i) {
            final Integer val = i;
            threads.add(
                    new Thread(() -> {
                        Thread currentThread = Thread.currentThread();
                        /*
                         * Перед входом потока в блок синхронизации выполняется проверка, что монитор синхронизируемого объекта
                         * (syncBucket в данном случае) не заблокирован.
                         *
                         * Если монитор объекта не заблокирован - то поток выполняет код в блоке
                         * синхронизации, при этом завладевая монитором синхронизируемого объекта. После выполнения кода
                         * в блоке поток выходит из монитора синхронизируемого объекта.
                         *
                         * Если монитор объекта заблокирован - то поток будет приостановлен до тех пор, пока поток,
                         * владеющий монитором синхронизируемого объекта, не выйдет из монитора.
                         */
                        synchronized (syncBucket) {
                            System.out.println(currentThread.getName() + ": в syncBucket добавлен объект: " + val.toString());
                            syncBucket.setVal(val);
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Object receivedValue = syncBucket.getVal();
                            System.out.println(currentThread.getName() + ": из syncBucket получен объект: " + receivedValue.toString());
                            if (val != receivedValue) {
                                System.out.println(ANSI_RED + "ОБНАРУЖНО НЕСОВПАДЕНИЕ!!! в потоке: " + currentThread.getName() + ANSI_RESET);
                            }
                        }
                    }, "Thread-" + i)
            );
        }
        for (Thread thread : threads) {
            thread.start();
        }

        synchronized (syncBucket) {
            //Если убрать блок synchronized, то проверка того, что монитор объекта syncBucket заблокирован, не будет выполнена.
            //Как следствие Поток main не будет ожидать монитор объекта syncBucket и все его методы будут вызваны из этого потока.
            Integer val = 100500;
            Thread currentThread = Thread.currentThread();
            System.out.println(currentThread.getName() + ": в syncBucket добавлен объект: " + val);
            syncBucket.setVal(val);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Object receivedValue = syncBucket.getVal();
            System.out.println(currentThread.getName() + ": из syncBucket получен объект: " + receivedValue.toString());
            if (val != receivedValue) {
                System.out.println(ANSI_RED + "ОБНАРУЖНО НЕСОВПАДЕНИЕ!!! в потоке: " + currentThread.getName() + ANSI_RESET);
            }
        }


        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Завершение основного потока");
    }
}
