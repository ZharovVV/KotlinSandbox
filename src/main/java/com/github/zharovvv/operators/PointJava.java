package com.github.zharovvv.operators;

import java.util.Objects;

public class PointJava {

    private final int x;
    private final int y;

    public PointJava(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointJava pointJava = (PointJava) o;
        return x == pointJava.x && y == pointJava.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "PointJava{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    /**
     * Вызывая java-код из Kotlin, можно использовать синтаксис операторов для любых методов с именами,
     * совпадающими с соглашениями в Kotlin. Так как в Java отсутствует синтаксис, позволяющий отметить
     * функции-операторы, требование использовать модификатор _operator_ к Java-коду не применяется, и в учет
     * принимаются только имена и количество параметров.
     */
    public PointJava plus(PointJava other) {
        return new PointJava(this.x + other.getX(), this.y + other.getY());
    }

    public PointJava minus(PointJava other) {
        return new PointJava(this.x - other.getX(), this.y - other.getY());
    }

    public PointJava times(double scale) {
        return new PointJava((int) (scale * x), (int) (scale * y));
    }
}
