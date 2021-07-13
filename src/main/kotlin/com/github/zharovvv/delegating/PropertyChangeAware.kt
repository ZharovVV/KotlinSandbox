package com.github.zharovvv.delegating

import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport

/**
 * Вспомогательный класс, для использования PropertyChangeSupport.
 */
open class PropertyChangeAware {
    @Suppress("LeakingThis")
    protected val changeSupport = PropertyChangeSupport(this)

    fun addPropertyChangeListener(listener: PropertyChangeListener) {
        changeSupport.addPropertyChangeListener(listener)
    }

    fun removePropertyChangeListener(listener: PropertyChangeListener) {
        changeSupport.removePropertyChangeListener(listener)
    }
}