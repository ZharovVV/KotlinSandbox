package com.github.zharovvv.high.order.functions;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;

public class UseKotlinFunctionInJavaExample {

    public static void main(String[] args) {
        HighOrderFunctionDeclarationExamplesKt.threeAndFour(() -> {
            System.out.println("Call HighOrderFunctionDeclarationExamples#threeAndFour from Java");
            return Unit.INSTANCE;   //Необходимо явно вернуть значение типа Unit.
        });
        HighOrderFunctionDeclarationExamplesKt.twoAndThree((Integer x, Integer y) -> x * y + 2 * y);
        Function2<Integer, Integer, Integer> sum = (x, y) -> x * y;
        HighOrderFunctionDeclarationExamplesKt.twoAndThree(sum);
        //В более старых версиях Java (где отсутствуют лямбда-выражения) можно передать экземляр
        //анонимного класса, реализующего метод invoke из соответствующего интерфейса:
        HighOrderFunctionDeclarationExamplesKt.threeAndFour(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                //do something
                return Unit.INSTANCE;
            }
        });
    }
}
