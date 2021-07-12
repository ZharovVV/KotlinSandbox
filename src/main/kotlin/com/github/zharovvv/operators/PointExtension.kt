@file:JvmName("PointExtension")

package com.github.zharovvv.operators

operator fun Double.times(point: Point): Point {
    return point * this
}

operator fun Point.get(index: Int): Int {
    return when (index) {
        0 -> x
        1 -> y
        else -> throw IndexOutOfBoundsException("Invalid coordinate $index")
    }
}
