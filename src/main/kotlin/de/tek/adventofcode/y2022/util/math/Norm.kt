package de.tek.adventofcode.y2022.util.math

interface Norm {
    fun sizeOf(vector: Vector): Int

    fun ballOfRadius(point: Point, radius: Int): Set<Point>

    infix fun Point.distanceTo(other: Point) = sizeOf(other - this)

    fun ballOfRadius(radius: Int) = ballOfRadius(ORIGIN, radius)
}