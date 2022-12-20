package de.tek.adventofcode.y2022.day09

import de.tek.adventofcode.y2022.util.math.*
import de.tek.adventofcode.y2022.util.readInputLines

class Rope(vararg initialKnotPositions: Point) {
    private val knotPositions = arrayOf(*initialKnotPositions)
    private val tailHistory = mutableSetOf(knotPositions.last())

    constructor(initialPosition: Point, numberOfKnots: Int) : this(*Array(numberOfKnots) { initialPosition })

    companion object {
        fun atOrigin(numberOfKnots: Int) = Rope(ORIGIN, numberOfKnots)
    }

    fun move(direction: Direction) {
        moveHead(direction)
        letTailFollowHead()
    }

    private fun moveHead(direction: Direction) {
        knotPositions[0] = knotPositions[0] + direction.toVector()
    }

    private fun letTailFollowHead() {
        for (i in 1 until knotPositions.size) {
            val difference = knotPositions[i - 1] - knotPositions[i]

            if (InfinityNorm.instance.sizeOf(difference) > 1) {
                knotPositions[i] = knotPositions[i] + Vector(difference.x.signum(), difference.y.signum())
            }
        }

        updateTailHistory()
    }

    private fun Int.signum() = if (this > 0) 1 else if (this < 0) -1 else 0

    private fun updateTailHistory() {
        tailHistory.add(knotPositions.last())
    }

    fun getNumberOfVisitedTailPositions() = tailHistory.size

    override fun toString(): String {
        return "Rope(knotPositions=${knotPositions.contentToString()}, tailHistory=$tailHistory)"
    }

    fun visualize(): String {
        if (knotPositions.size > 10) return "Too many knots to visualize."

        val pointsToVisualize = tailHistory + knotPositions
        val lowerLeftCorner = pointsToVisualize.lowerLeftCorner()!!
        val upperRightCorner = pointsToVisualize.upperRightCorner()!!

        val diagonal = upperRightCorner - lowerLeftCorner

        val grid = Array(diagonal.y + 1) { Array(diagonal.x + 1) { '.' } }

        val correctToOrigin = ORIGIN - lowerLeftCorner
        tailHistory.map { it + correctToOrigin }.forEach { (x, y) -> grid[y][x] = '#' }
        knotPositions.mapIndexed { index, position -> index to position + correctToOrigin }
            .forEach { (index, point) -> grid[point.y][point.x] = index.toString()[0] }

        return grid.reversed().joinToString("\n") { it.joinToString("") }
    }

    private fun Set<Point>.lowerLeftCorner() = aggregateAlongAxes { projection -> this.minOf(projection) }
    private fun Set<Point>.upperRightCorner() = aggregateAlongAxes { projection -> this.maxOf(projection) }
    private fun Set<Point>.aggregateAlongAxes(aggregator: Iterable<Point>.((Point) -> Int) -> Int): Point? {
        if (this.isEmpty()) return null

        val aggregateHorizontally = this.aggregator { it.x }
        val aggregateVertically = this.aggregator { it.y }
        return Point(aggregateHorizontally, aggregateVertically)
    }
}

fun main() {
    val input = readInputLines(Rope::class)

    fun part1(input: List<String>): Int {
        val rope = createAndMoveRope(2, input)
        return rope.getNumberOfVisitedTailPositions()
    }

    fun part2(input: List<String>): Int {
        val rope = createAndMoveRope(10, input)
        return rope.getNumberOfVisitedTailPositions()
    }

    println("2 knots: ${part1(input)} positions were visited by the tail at least once.")
    println("10 knots: ${part2(input)} positions were visited by the tail at least once.")
}

private fun createAndMoveRope(numberOfKnots: Int, input: List<String>): Rope {
    val rope = Rope.atOrigin(numberOfKnots)
    val moves = input.flatMap(::parseMove)

    for (move in moves) {
        rope.move(move)
    }

    return rope
}

fun parseMove(s: String): List<Direction> {
    val (directionChar, numberOfMoves) = s.split(' ')
    val direction = when (directionChar) {
        "U" -> Direction.UP
        "L" -> Direction.LEFT
        "D" -> Direction.DOWN
        "R" -> Direction.RIGHT
        else -> throw IllegalArgumentException("Invalid move code, cannot parse direction: $s.")
    }

    return List(numberOfMoves.toInt()) { direction }
}
