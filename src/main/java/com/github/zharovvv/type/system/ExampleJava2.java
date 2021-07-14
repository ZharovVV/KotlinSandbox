package com.github.zharovvv.type.system;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Пример 1. Очень тонкий нюанс Kotlin.
 * Вопрос:
 * Скомпилируется ли следующий код в Kotlin:
 * val exampleJava2 = ExampleJava2()
 * exampleJava2.a = "bla-bla"
 * Ответ: Нет
 */
public class ExampleJava2 {

    /**
     * Так как в Kotlin нет полей, а есть только свойства (property) - на Java-код Kotlin смотрит относительно
     * get/set-методов. Причем, приоритет всегда у getter-а.
     * так как getter называется getA(), то свойство будет называться - "a".
     */
    private String mA;

    /**
     * Getter возвращает тип String (NotNull).
     */
    @NotNull
    public String getA() {
        return mA;
    }

    /**
     * В setter-е указан тип String?. Так как тип String (NotNull, указанный в getter-е) не включает в себя тип String?, то
     * Kotlin будет воспринимать метод setA, как обычный метод, но не как setter property.
     * Таким образом, Kotlin будет считать свойство "a" как val-свойство (типа String).
     */
    public void setA(@Nullable String a) {
        this.mA = a;
    }
}
