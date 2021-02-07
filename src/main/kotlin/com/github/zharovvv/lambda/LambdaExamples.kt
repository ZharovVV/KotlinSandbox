package com.github.zharovvv.lambda

val SIMPLE_SUM: (Int, Int) -> Int = { x, y -> x + y }

val SIMPLE_MINUS = { x: Int, y: Int -> x - y }

private fun execute(a: Int, b: Int, executor: (Int, Int) -> Int): Int = executor.invoke(a, b)

fun main1() {
    println(execute(a = 1, b = 2, SIMPLE_SUM))
    println(SIMPLE_SUM.invoke(1, 2))
    println(SIMPLE_SUM(1, 2))

    println(execute(6, 12, executor = SIMPLE_MINUS))
    println(SIMPLE_MINUS.invoke(6, 12))
    println(SIMPLE_MINUS(6, 12))

    /**
     * Синтаксис Kotlin позволяет вынести лямбда-выражение за круглые скобки,
     * если оно является последним аргументом вызываемой функции.
     */
    println(execute(2, 3) { a, b -> a * b })    //same of println(execute>>>(2, 3, { a, b -> a * b })<<<)
}


class Button(var onClickListener: () -> Unit = {}) {
    fun onClick() = onClickListener()   //same of onClickListener.invoke()
}

fun main() {

    /**
     * При захвате лямбда-выражением финальной переменной её значение копируется, точно как в Java.
     */
    val buttonName = "Кнопочка"

    /**
     * При захвате изменяемой переменной её значение сохраняется как экземпляр класса Ref.
     * Переменная Ref - финальная и может быть захвачена, в то время как фактческое значение хранится в поле
     * и может быть изменено внутри лямбда-выражения.
     *
     * В данном случае создается экземпляр класса IntRef:
     * public static final class IntRef implements Serializable {
     *      public int element;
     *
     *      @Override
     *      public String toString() {
     *          return String.valueOf(element);
     *      }
     * }
     *
     * Java:
     * final IntRef clicks = new IntRef();
     * clicks.element = 0;
     */
    var clicks = 0

    val button = Button()
    button.onClickListener = {
        println("Нажата кнопка \"$buttonName\"")
        clicks++    //Java: clicks.element++
    }

    button.onClick()
    button.onClick()
    button.onClick()
    println(clicks)
}

fun topLevelFunc(var1: String, var2: String): String = var1 + var2

val linkToTopLevelFunc: (String, String) -> String = ::topLevelFunc
