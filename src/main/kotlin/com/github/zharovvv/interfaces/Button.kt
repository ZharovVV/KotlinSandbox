package com.github.zharovvv.interfaces

class Button : Clickable, Focusable {
    override fun click() {

    }

    override fun showOff() {
        //Компилятор Kotlin вынуждает предоставить собственную реализацию, т.к. метод содержится в обоих интерфейсах
        super<Clickable>.showOff()  //В Java это бы выглядело так: Clickable.super.showOff()
        super<Focusable>.showOff()
    }

    /**
     * При компиляции также будет добавлен метод с реализацией по умолчанию, объявленный в [Focusable]:
     * public void setFocus(boolean b) {
     * com.github.zharovvv.interfaces.Focusable.DefaultImpls.setFocus(this, b);
     * }
     */
}