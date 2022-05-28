package com.github.zharovvv.classes.`object`

object SingletonObject

interface Marker {
    val value: String
}

class SomeClass {
    //decompiled
    //public static final Key Key = new Key((DefaultConstructorMarker)null);
    companion object Key : Marker {
        override val value: String get() = "key"
    }
}

fun someFun(marker: Marker) {
    println(marker.hashCode())
}

fun main() {
    //someFun((Marker)SomeClass.Key);
    someFun(SomeClass)
}