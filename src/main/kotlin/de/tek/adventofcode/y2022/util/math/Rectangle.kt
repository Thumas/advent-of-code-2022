package de.tek.adventofcode.y2022.util.math

data class Rectangle(private val lowerLeftCorner: Point, private val upperRightCorner: Point) : Iterable<Point> {

    init {
        if (lowerLeftCorner.x > upperRightCorner.x || lowerLeftCorner.y > upperRightCorner.y) {
            throw IllegalArgumentException(
                "First argument must be the lower left corner, " +
                        "second argument must be the upper right corner of the rectangle."
            )
        }
    }

    infix fun contains(point: Point) =
        point.x in lowerLeftCorner.x..upperRightCorner.x && point.y in lowerLeftCorner.y..upperRightCorner.y

    override fun iterator() =
        (lowerLeftCorner.x..upperRightCorner.x).asSequence().flatMap { x ->
            (lowerLeftCorner.y..upperRightCorner.y).asSequence().map { y -> Point(x, y) }
        }.iterator()
}