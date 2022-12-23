package de.tek.adventofcode.y2022.util.math

data class Point(val x: Int, val y: Int) {

    constructor(coordinates: Pair<Int,Int>) : this(coordinates.first, coordinates.second)

    operator fun plus(vector: Vector) = Point(x + vector.x, y + vector.y)
    operator fun minus(vector: Vector) = Point (x - vector.x, y - vector.y)
    operator fun minus(other: Point) = Vector(x - other.x, y - other.y)
}

val ORIGIN = Point(0,0)

fun Iterable<Point>.lowerLeftCorner() = aggregateAlongAxes { projection -> this.minOf(projection) }
fun Iterable<Point>.upperRightCorner() = aggregateAlongAxes { projection -> this.maxOf(projection) }
private fun Iterable<Point>.aggregateAlongAxes(aggregator: Iterable<Point>.((Point) -> Int) -> Int): Point? {
    if (!this.iterator().hasNext()) return null

    val aggregateHorizontally = this.aggregator { it.x }
    val aggregateVertically = this.aggregator { it.y }

    return Point(aggregateHorizontally, aggregateVertically)
}