package com.github.zharovvv.delegating

import java.beans.PropertyChangeSupport
import kotlin.reflect.KProperty

/**
 * ObservableProperty как объект-делегат для свойства
 */
class ObservableProperty(var propValue: Int, private val changeSupport: PropertyChangeSupport) {

    operator fun getValue(personDelegating: PersonDelegating, prop: KProperty<*>): Int = propValue

    operator fun setValue(personDelegating: PersonDelegating, prop: KProperty<*>, newValue: Int) {
        val oldValue = propValue
        propValue = newValue
        changeSupport.firePropertyChange(prop.name, oldValue, newValue)
    }
}