package com.github.zharovvv.classes

class User(val name: String) {

    /**
     * [JvmField] - Указывает компилятору Kotlin не создавать геттеры / сеттеры для этого свойства и раскрывать его как поле.
     */
    @JvmField
    var javaField: String = ""

    var address: String = "unknown"
        set(value) {
            println("$field -> $value")
            field = value   //Обращение к полю из методов доступа
        }
}