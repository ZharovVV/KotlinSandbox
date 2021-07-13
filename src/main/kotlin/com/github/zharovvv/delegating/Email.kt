package com.github.zharovvv.delegating

data class Email(val emailAddress: String)

//Имитация долгой операции полулчения списка емейлов из БД.ж
fun loadEmail(person: PersonDelegating): List<Email> {
    println("Load emails for ${person.name}")
    return listOf(Email("mail@mail.ru"), Email("mail@gmail.com"))
}