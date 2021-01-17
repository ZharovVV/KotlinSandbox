package com.github.zharovvv

import java.lang.IllegalArgumentException

fun main(args: Array<String>) {
    saveUser(User(id = 1, address = "", name = ""))
}

class User(val id: Int, val name: String, val address: String)

fun saveUser(user: User) {
    user.validateBeforeSave()
    //Сохранение информации о пользователе
}

private fun User.validateBeforeSave() {
    fun validate(value: String, fieldName: String) {    //Объявление локальной функции
        if (value.isEmpty()) {
            throw IllegalArgumentException("Can`t save user ${id}: empty $fieldName")
        }
    }
    validate(name, "Name")
    validate(this.address, "Address")
}