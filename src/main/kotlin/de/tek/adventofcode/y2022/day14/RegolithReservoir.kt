package de.tek.adventofcode.y2022.day14

import de.tek.adventofcode.y2022.util.math.*
import de.tek.adventofcode.y2022.util.readInputLines

enum class Material(private val visualization: Char) {
    AIR('.'), SAND('o'), ROCK('#');

    override fun toString() = visualization.toString()
}

class Cave(private val sandSource: Point, rockPositions: Set<Point>) {
    private val grid: GridWithPoints<Material>

    init {
        val array = buildArrayFrom(rockPositions)

        grid = Grid.withPoints(array)
    }

    private fun buildArrayFrom(rockPositions: Set<Point>): Array<Array<Material>> {
        val givenPositions = rockPositions + sandSource
        val lowestPosition = givenPositions.maxOfOrNull { it.y }
            ?: throw IllegalArgumentException("No rock positions given, so the sand flows unobstructed.")
        val rightmostPosition = givenPositions.maxOfOrNull { it.x }!!

        val array = Array(lowestPosition + 1) { y ->
            Array(rightmostPosition + 1) { x ->
                if (Point(x, y) in rockPositions) Material.ROCK else Material.AIR
            }
        }
        return array
    }

    fun runSimulation(): Int {
        var sandCounter = -1
        do {
            if (!sandSource.isFree()) {
                return sandCounter + 1
            }
            produceSand()
            sandCounter++

            val sandPath = generateSequence(sandSource) { oldPosition -> moveSand(oldPosition) }
            val firstPositionOutsideTheGrid = sandPath.filter { !(it isIn grid) }.firstOrNull()
        } while (firstPositionOutsideTheGrid == null)

        return sandCounter
    }

    private fun produceSand() {
        if (sandSource.isFree()) {
            grid[sandSource] = Material.SAND
        } else {
            throw SandOverflowException("Sand source at $sandSource is blocked by ${grid[sandSource]}, cannot produce sand.")
        }
    }

    private fun Point.isFree(): Boolean = grid[this].let { it == null || it == Material.AIR }

    private fun moveSand(position: Point): Point? {
        val newPosition = determineNewPosition(position) ?: return null

        updateMaterial(position, newPosition)

        return newPosition
    }

    private fun determineNewPosition(position: Point) =
        // gravity is pointing in the opposite direction because of array index order
        sequenceOf(Vector(0, 1), Vector(-1, 1), Vector(1, 1))
            .map { position + it }
            .filter { it.isFree() }
            .firstOrNull()

    private fun updateMaterial(
        position: Point,
        newPosition: Point
    ) {
        grid[position] = Material.AIR
        if (newPosition isIn grid) {
            grid[newPosition] = Material.SAND
        }
    }

    override fun toString(): String {
        val pointsToVisualize =
            grid.iterator().asSequence().filter { it.value != Material.AIR }.map { it.point }.toList() + sandSource
        val lowerLeftCorner = pointsToVisualize.lowerLeftCorner()!!
        val upperRightCorner = pointsToVisualize.upperRightCorner()!!

        val diagonal = upperRightCorner - lowerLeftCorner

        val correctToOrigin = ORIGIN - lowerLeftCorner
        val array = Array(diagonal.y + 1) { y ->
            Array(diagonal.x + 1) { x ->
                val positionInGrid = Point(x, y) - correctToOrigin
                if (positionInGrid == sandSource) {
                    '+'
                } else {
                    val value = grid.at(positionInGrid)?.value
                        ?: throw IllegalStateException("Position ($x,$y) is outside of the grid.")
                    value.toString()[0]
                }
            }
        }

        return array.joinToString("\n") { it.joinToString("") }
    }
}

class SandOverflowException(s: String) : Exception(s)

fun main() {
    val input = readInputLines(Cave::class)

    println("${part1(input)} units of sand come to rest before sand starts flowing into the abyss below.")
    println("${part2(input)} units of sand come to rest before the sand clogs its source.")
}

fun part1(input: List<String>): Int {
    val rockPositions = parseRockPositions(input)
    val cave = Cave(Point(500, 0), rockPositions)

    return cave.runSimulation().also { println(cave) }
}

fun part2(input: List<String>): Int {
    val sandSource = Point(500, 0)
    val rockPositions = parseRockPositions(input)
    val ground = determineGroundPositions(sandSource, rockPositions)

    val cave = Cave(sandSource, rockPositions + ground)

    return cave.runSimulation().also { println(cave) }
}

private fun parseRockPositions(input: List<String>) =
    input.flatMap(::parseLineSegments).flatMap(AxisParallelLineSegment::toListOfPoints).toSet()

private fun determineGroundPositions(
    sandSource: Point,
    rockPositions: Set<Point>
): List<Point> {
    val heightOfSandSource = heightOfSandSource(rockPositions)

    // sand can move at most its fall height to the left and to the right
    val ground = AxisParallelLineSegment(
        sandSource + Vector(heightOfSandSource, heightOfSandSource),
        sandSource + Vector(-heightOfSandSource, heightOfSandSource)
    )
    return ground.toListOfPoints()
}

private fun parseLineSegments(string: String): List<AxisParallelLineSegment> {
    val nodes = string.split(" -> ").map(::parseCommaSeparatedInts).map { Point(it[0], it[1]) }

    val nodePairs = nodes.zip(nodes.drop(1))
    return nodePairs.map(::AxisParallelLineSegment)
}

private fun parseCommaSeparatedInts(it: String) = it.split(",").take(2).map(String::toInt)

private fun heightOfSandSource(rockPositions: Set<Point>): Int {
    val lowestScanPosition = (rockPositions + Point(500, 0)).maxOf { it.y }
    return lowestScanPosition + 2
}