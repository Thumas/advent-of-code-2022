package de.tek.adventofcode.y2022.util.math

data class AxisParallelLineSegment(val start: Point, val end: Point) {

    constructor(startEnd: Pair<Point,Point>) : this(startEnd.first, startEnd.second)

    init {
        if (start.x != end.x && start.y != end.y)
            throw IllegalArgumentException("The given points do not lie on a line parallel to one of the axes.")
    }

    fun toListOfPoints(): List<Point> =
        if (start.x == end.x) {
            if (start.y <= end.y) {
                start.y..end.y
            } else {
                start.y downTo end.y
            }.map { y -> Point(start.x, y) }
        } else {
            if (start.x <= end.x) {
                start.x..end.x
            } else {
                start.x downTo end.x
            }.map { x -> Point(x, start.y) }
        }
}