package com.github.zharovvv.type.system;

import org.jetbrains.annotations.NotNull;

/**
 * Пример 1. Очень тонкий нюанс Kotlin.
 * Вопрос:
 * Скомпилируется ли следующий код в Kotlin:
 * val exampleJava1 = ExampleJava1()
 * exampleJava1.a = null
 * Ответ: Да
 */
public class ExampleJava1 {

    /**
     * Так как в Kotlin нет полей, а есть только свойства (property) - на Java-код Kotlin смотрит относительно
     * get/set-методов. Причем, приоритет всегда у getter-а.
     * так как getter называется getA(), то свойство будет называться - "a".
     */
    @NotNull
    private String mA;

    /**
     * В данном случае getter возвращает платформенный тип String!
     */
    public String getA() {
        return mA;
    }

    /**
     * В setter-е указан тип String (NotNull). И так как String! включает в себя тип String (NotNull), то
     * Kotlin будет воспринимать метод setA как setter property а,
     * занчит будет считать свойство "a" как var-свойство, причем тип у него будет String!.
     */
    public void setA(@NotNull String a) {
        this.mA = a;
    }


    private Boolean isEnabled;  //Для обращения из Kotlin к var-свойству isEnabled
    //нужно добавить одну из следущих пар getter/setter методов:
    //1) isEnabled()/setEnabled(...)
    //2) getIsEnabled()/setIsEnabled(...)

    public Boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(Boolean enabled) {
        isEnabled = enabled;
    }
}
