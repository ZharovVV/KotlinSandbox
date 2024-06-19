package com.github.zharovvv.sealed

sealed interface A {
    val c: String
}

data class B(
    val b: String,
    override val c: String
) : A

data class C(
    override val c: String
) : A