package de.tek.adventofcode.y2022.util.math

enum class Direction(val deltaX: Int, val deltaY: Int) {
    UP(0, 1), LEFT(-1, 0), DOWN(0, -1), RIGHT(1, 0);

    fun toVector() = Vector(deltaX, deltaY)
}