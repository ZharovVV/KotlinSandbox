package com.github.zharovvv.annotation

import com.github.zharovvv.reflection.findAnnotation
import kotlin.reflect.full.memberProperties

@Target(    //Определяет типы элементов, к которым может применяться объявляемая следом аннотация.
    AnnotationTarget.PROPERTY,   //Цель PROPERTY. Обращаем внимание, что такую аннотацию нельзя будет использовать в Java.
    AnnotationTarget.FIELD  //В этом случае аннотация будет применяться к свойствам в Kotlin и к полям в Java.
)
annotation class KotlinAnnotation(val name: String)

/**
 * ## Метааннотации - аннотации, применяемые к другим аннотациям.
 * Пример объвления метааннотации.
 */
@Target(AnnotationTarget.ANNOTATION_CLASS)
annotation class MetaAnnotation

data class SomeDataClass(@KotlinAnnotation("marked") val str: String)
data class SomeDataClass2(val str: String)

fun Any.hasKAnnotationProperty(): Boolean {
    return javaClass.kotlin.memberProperties
        .any { kProperty1 -> kProperty1.findAnnotation<KotlinAnnotation>() != null }
}

fun main() {
    val someDataClass = SomeDataClass("123")
    val someDataClass2 = SomeDataClass2("345")
    println(someDataClass.hasKAnnotationProperty())
    println(someDataClass2.hasKAnnotationProperty())
}