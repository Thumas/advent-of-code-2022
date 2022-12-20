package de.tek.adventofcode.y2022.util.math

data class Vector(val x: Int, val y: Int) {

    infix fun isParallelTo(other: Vector): Boolean = (x * other.y - y * other.x == 0)

    operator fun minus(other: Vector) = Vector(x - other.x, y- other.y)
}

operator fun Int.times(vector: Vector) = Vector(this@times * vector.x, this@times * vector.y)
