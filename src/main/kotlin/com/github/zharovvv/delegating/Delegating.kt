package com.github.zharovvv.delegating

import java.beans.PropertyChangeEvent

fun main() {
    val personDelegating = PersonDelegating("John", 30, 10000)
    personDelegating.addPropertyChangeListener { propertyChangeEvent: PropertyChangeEvent ->
        println("Property ${propertyChangeEvent.propertyName} changed from ${propertyChangeEvent.oldValue} to ${propertyChangeEvent.newValue}")
    }
    personDelegating.emails
    personDelegating.salary = 120000

    personDelegating.setAttribute("attr1", "someData")
    println(personDelegating.attr1)
}