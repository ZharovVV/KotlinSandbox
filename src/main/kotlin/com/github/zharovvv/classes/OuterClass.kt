package com.github.zharovvv.classes

/**
 * Внешний класс
 */
class OuterClass {

    /**
     * Внутренний класс - неявно содержит ссылку на внешний класс.
     * Для его создания необходимо ключевое слово - inner.
     *
     * Java:
     * public final class InnerClass {
     *      @NotNull
     *      public final OuterClass getOuterClass() {
     *          return OuterClass.this;
     *      }
     * }
     */
    inner class InnerClass {
        fun getOuterClass(): OuterClass = this@OuterClass
    }

    /**
     * Вложеный класс - не содержит ссылки на внешний класс.
     * Создается по умолчанию.
     *
     * Java:
     * public static final class NestedClass {
     * }
     */
    class NestedClass {

    }
}