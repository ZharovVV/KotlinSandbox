package com.github.zharovvv.interfaces

open class RichButton : Clickable { //открытый класс: другие могут наследовать его.

    override fun click() {}     //Переопределение открытой функции также является открытым

    final override fun showOff() {  //Ключевое слово final делает переопределяемую функцию закрытой
        super.showOff()
    }

    fun disable() {}    //закрытая функция - невозможно переопределить в подклассе

    open fun animate() {}   //открытая функция

    //Абстрактные методы всегда открыты. Поэтому не требуется явно использовать модификатор open.
    //Абстракные методы не могут иметь реализацию.
}