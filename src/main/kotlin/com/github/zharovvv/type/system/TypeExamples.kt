package com.github.zharovvv.type.system

import com.github.zharovvv.classes.User

data class Example(
    var nullableString: String?,
    val string: String = "Not Null",
    var isEnabled: Boolean = true   //будет создан getter: isEnabled() и setter: setEnabled(...)
)

fun main() {
    val example = Example(null)
    /**
     * ?. - оператор безопасного вызова.
     */
    example.nullableString?.toUpperCase()   //эквивалентно if (s != null) s.toUpperCase() else null
    /**
     * ?: - оператор "Элвис"
     * foo ?: bar - если foo != null -> foo, иначе bar.
     */
    val notNullStringByElvis: String = example.nullableString ?: "DefaultValue if it's null"
    println(notNullStringByElvis)

    try {
        val notNullOrException: String =
            example.nullableString ?: throw IllegalArgumentException("nullableString is null!!")
    } catch (e: Exception) {
        e.printStackTrace()
    }

    println(assertUser(example.nullableString))

    val notNullForCompiler: String = example.nullableString!!
}

/**
 * Оператор безопасного приведения типов as?
 * (Обычный оператор as может бросать исключение ClassCastException)
 * foo as? Type - если foo is Type -> foo as Type, иначе возвращается null
 */
fun assertUser(any: Any?): Boolean {
    val user: User = any as? User ?: return false
    println(user)
    return true
}