package com.github.zharovvv.classes

class DelegatingCollection<T>(
    private val innerList: Collection<T> = ArrayList()
) : Collection<T> by innerList {

    override val size: Int
        get() {
            //Additional logic
            return innerList.size
        }

    //Остальные методы Collection делегируются объекту innerList (по умолчанию ArrayList)
}