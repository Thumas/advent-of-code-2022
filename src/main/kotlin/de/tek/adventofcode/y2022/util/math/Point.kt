package de.tek.adventofcode.y2022.util.math

data class Point(val x: Int, val y: Int) {
    operator fun plus(vector: Vector) = Point(x + vector.x, y + vector.y)

    operator fun minus(other: Point) = Vector(x - other.x, y - other.y)
}

val ORIGIN = Point(0,0)