@file:JvmName("TopLevelFunctions")

/**
 * По умолчанию создастся java-класс с именем ExampleKtFileKt со статическим методом exampleFunction.
 * Для изменения имени создаваемого класса необходимо добавить аннотацию @file:JvmName("Новое имя класса").
 * Это актуально, если метод будет вызван из Java-кода.
 */
package other.directory

/**
 * Импорт функции-расширения с изменением имени функции
 */
import com.github.zharovvv.lastChar as last

/**
 * СВОЙСТВА ВЕРХНЕГО УРОВНЯ
 */

/**
 * При компиляции поле будет:
 * public static final String CONST_VAL
 */
const val CONST_VAL = "constant value"

/**
 * При компиляции поле будет:
 * private static final String VAL
 * Также добавится static final метод чтения:
 * public static final String getVAL()
 */
val VAL = "value"

/**
 * Аналогично с VAL + метод static final метод записи.
 */
var VAR = "variable"

/**
 * ФУНКЦИЯ ВЕРХНЕГО УРОВНЯ
 *
 * При компиляции метод будет:
 * public static final String exampleFunction(@NotNull String str) {...}
 *
 */
fun exampleFunction(str: String): String {
    return str + str.last()
}