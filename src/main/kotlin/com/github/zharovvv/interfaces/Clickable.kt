package com.github.zharovvv.interfaces

interface Clickable {
    fun click()
    fun showOff() = println("i`m clickable!")   //метод с реализацией по умолчанию
}