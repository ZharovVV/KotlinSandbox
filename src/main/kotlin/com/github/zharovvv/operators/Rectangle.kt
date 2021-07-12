package com.github.zharovvv.operators

data class Rectangle(val upperLeft: Point, val lowerRight: Point) {

    operator fun contains(point: Point): Boolean {
        return point.x in upperLeft.x until lowerRight.x && //Создает полузакрытый диапазон и проверяет принадлежность еу координты x
                point.y in upperLeft.y until lowerRight.y
    }
}
