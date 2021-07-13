package com.github.zharovvv.delegating

import kotlin.properties.Delegates
import kotlin.reflect.KProperty

/**
 * # Делегирование свойств
 * Делегирование свойств в Kotlin также опирается на соглашения.
 *
 * **Делегирование** - шаблон проектирования, согласно которому объект не сам выполняет требуемое задание, а делегирует
 * его другому вспомогательному объекту. Такой вспомогательный объект называется _делегатом_.
 *
 * Делегирование свойств дает возможность повторно использовать логику хранения значений свойств, инициализвции,
 * чтения и изменения. Это очень можный механизм для разработки фреймворков.
 */
class PersonDelegating(val name: String, age: Int, salary: Int) : PropertyChangeAware() {

    /**
     * Реализация отложенной инициализации на основе приема _теневого свойства_ (backing property).
     * NB: данная реализация не будет безопасной в контексте многопоточного выполнения.
     */
//    private var _emails: List<Email>? = null
//    val emails: List<Email>
//        get() {
//            if (_emails == null) {
//                _emails = loadEmail(this)
//            }
//            return _emails!!
//        }
    /**
     * Использование делегата, возвращаемого функцией lazy из стандартной библиотеки.
     * Функция lazy возвращает объект, имеющий метод getValue(...,...) с соответствующей сигнатурой, - то есть её
     * можно использовать с ключевым словом by для создания делигированного свойства.
     * В аргументе функции lazy передается лямда-выражение, которое она вызывает для инициализации значения.
     * Функция lazy по умолчанию пригодна для использования в многопоточном окружении, и если потребуется, ей можно
     * передать дополнительные параметры, чтобы сообщить, какую блокировку использовать или вообще игнорировать средства
     * синхронизации, если класс никогда не будет использоваться в многопоточной среде.
     */
    val emails: List<Email> by lazy { loadEmail(this) }

    /**
     * Правила делегирования свойств. Допустим имеется класс, делегирующий свойство:
     * ```
     *  class C {
     *      var prop: Type by MyDelegate()
     *  }
     * ```
     * Экземпляр C будет хранить скрытое свойство (назовем его <delegate>).
     * Кроме того, для представления свойства компилятор будет использовать объект типа KProperty (назовем его <property>)
     * В результате компилятор сгенерирует следующий код:
     * ```
     *  class C {
     *      private val <delegate> = MyDelegate()
     *      var prop: Type
     *          get() = <delegate>.getValue(this, <property>)
     *          set(value: Type) = <delegate>.setValue(this, <property>, value)
     *  }
     * ```
     * Kotlin автоматически сохраняет делегата в скрытом свойстве и вызывает методы getValue и setValue делегата при
     * попытке прочитать или изменить основное свойство.
     */
    var age: Int by ObservableProperty(age, changeSupport)// объект справа от by - делегат

    /**
     * Стандартная библиотека Kotlin уже содержит класс, похожий на ObservableProperty.
     * Но этот класс никак не связан с PropertyChangeSupport, использовавшемся выше, поэтому потребуется передать
     * лямбда-выражение, определяющее, как должны передаваться уведомления об изменении значения свойсва.
     */
    var salary: Int by Delegates.observable(salary, onChange = { property: KProperty<*>, oldValue: Int, newValue: Int ->
        changeSupport.firePropertyChange(property.name, oldValue, newValue)
    })


    private val _attributes: MutableMap<String, String> = hashMapOf()

    fun setAttribute(attrName: String, value: String) {
        _attributes[attrName] = value
    }

    /**
     * Возможность использовать словарь в роли объекта-делегата возможно благодаря тому, что в стандартной библиотеке
     * опеределены функции-расширения getValue (для Map<in String, V> и MutableMap<in String, out V>) и
     * setValue (для MutableMap<in String, in V>).
     * Имя свойства автоматически используется как ключ для доступа к значению в словаре.
     * Ссылка на person.attr1 фактически транслируется в вызов _attributes.getValue(person, prop), который, в свою
     * очередь, возвращает результат выражения `_attributes[prop.name]`
     */
    val attr1: String by _attributes    //Использование словаря в роли объекта-делегата

}
