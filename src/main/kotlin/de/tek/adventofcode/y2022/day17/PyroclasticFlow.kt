package de.tek.adventofcode.y2022.day17

import de.tek.adventofcode.y2022.util.arrays.merge
import de.tek.adventofcode.y2022.util.arrays.splitIndices
import de.tek.adventofcode.y2022.util.math.*
import de.tek.adventofcode.y2022.util.readInputLines
import de.tek.adventofcode.y2022.util.splitByBlankLines
import java.lang.Integer.max

enum class Material(val visualization: Char) {
    AIR('.'), ROCK('#');

    companion object {
        fun parseFrom(char: Char) = values().firstOrNull { it.visualization == char }
            ?: throw NoSuchElementException("$char is not a valid Material.")
    }
}

class RockShape private constructor(private val shapeArray: Array<Array<Char>>) : Iterable<PointWithValue<Material>> {
    private val grid = GridWithPoints(Grid(shapeArray) { _, item -> Material.parseFrom(item) })

    val height = shapeArray.size

    override fun iterator() = grid.iterator()

    companion object {
        fun parseFrom(shape: String): RockShape {
            val shapeArray = shape.split("\n").map { it.toCharArray().toTypedArray() }.reversed().toTypedArray()
            return RockShape(shapeArray)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RockShape) return false

        if (!shapeArray.contentDeepEquals(other.shapeArray)) return false

        return true
    }

    override fun hashCode(): Int {
        return shapeArray.contentDeepHashCode()
    }

    override fun toString() = shapeArray.map { it.toCharArray() }.reversed().joinToString("\n") { it.joinToString("") }
}

class Rock(val shape: RockShape, position: Point) {
    var positionOfBottomLeftCorner = position
        private set

    val height = shape.height

    fun moveIn(direction: Direction) {
        positionOfBottomLeftCorner += direction.toVector()
    }

    fun positions() =
        shape.filter { it.value == Material.ROCK }.map { positionOfBottomLeftCorner + (it.point - ORIGIN) }
}

/**
 * Models the tall, narrow chamber. Only its content is modelled as data, not the floor or the walls. The content at the
 * bottom is removed, if it is no longer needed for collision detection.
 */
class PyroclasticFlowChamber(
    private val width: Int, flowMovements: FlowMovements, rockShapes: List<RockShape>
) {
    private val content = mutableListOf<Array<Material>>()

    private var bottomHeight = 0L
    private var highestRockPosition = -1

    init {
        if (width <= 0) throw IllegalArgumentException("Width must be positive, was $width.")
        if (rockShapes.isEmpty()) throw IllegalArgumentException("List of rock shapes must not be empty.")
    }

    private fun currentChamberHeight() = bottomHeight + highestRockPosition + 1

    private val flowMovements = flowMovements.iterator()

    private val rockShapes = rockShapes.toInfiniteSequence().iterator()

    private data class ChamberState(val flowMovementIndex: Int, val numberOfRocks: Long, val chamberHeight: Long)

    private val rockShapeAndFlowMovementStatistics = rockShapes.associateWith { mutableSetOf<ChamberState>() }

    fun calculateChamberHeight(totalNumberOfRocks: Long): Long {
        val (oldChamberState, newChamberState) = findCycle(totalNumberOfRocks) ?: return currentChamberHeight()

        val remainingRocksAfterAllCycles = runCycles(totalNumberOfRocks, newChamberState, oldChamberState)

        // reset statistics, so no further cycle is detected
        rockShapeAndFlowMovementStatistics.values.forEach { it.clear() }
        findCycle(remainingRocksAfterAllCycles - 1)

        return currentChamberHeight()
    }

    /**
     * Finds a cycle, i.e. a repeated constellation where the top row is full and the same rock shapes is dropped
     * at the same point in the flow movement sequence. It drops the rock for which the cycle is detected and returns
     * the number of rocks and the chamber height right before and after the cycle.
     * If no cycle can be found, null is returned.
     */
    private fun findCycle(totalNumberOfRocks: Long): Pair<ChamberState, ChamberState>? {
        for (numberOfRocks in 0 until totalNumberOfRocks) {
            val rock = createNextRock()

            val topRowIsFull =
                content.isEmpty() || content.reversed().filterNot { it.all { material -> material == Material.AIR } }
                    .first()
                    .all { material -> material == Material.ROCK }

            val collectedStatisticsForRockShape = rockShapeAndFlowMovementStatistics[rock.shape]!!
            var result: Pair<ChamberState, ChamberState>? = null
            do {
                val (flowIndex, flowDirection) = flowMovements.next()

                if (topRowIsFull && result == null) {
                    val lastChamberState =
                        collectedStatisticsForRockShape.firstOrNull { it.flowMovementIndex == flowIndex }

                    val currentChamberState = ChamberState(flowIndex, numberOfRocks, currentChamberHeight())

                    if (lastChamberState != null) {
                        result = Pair(lastChamberState, currentChamberState)
                    } else {
                        collectedStatisticsForRockShape.add(currentChamberState)
                    }
                }

                rock.moveIn(flowDirection)
                if (checkCollision(rock.positions())) {
                    rock.moveIn(oppositeOf(flowDirection))
                }
                rock.moveIn(Direction.DOWN)
            } while (!checkCollision(rock.positions()))
            rock.moveIn(Direction.UP)

            addToChamber(rock)
            removeUnnecessaryContent()

            if (result != null) return result
        }

        return null
    }

    private fun createNextRock(): Rock {
        val rockShape = rockShapes.next()
        val rockPosition = Point(2, highestRockPosition + 4)
        return Rock(rockShape, rockPosition)
    }

    private fun removeUnnecessaryContent() {
        if (content.isEmpty()) return

        val lowestRowThatCouldBlockRock = determineLowestRowThatCouldBlockRock()

        if (lowestRowThatCouldBlockRock != -1) {
            val numberOfRowsToRemove = lowestRowThatCouldBlockRock + 1
            repeat(numberOfRowsToRemove) { content.removeFirst() }

            // println("Removed $numberOfRowsToRemove rows.")

            bottomHeight += numberOfRowsToRemove
            highestRockPosition -= numberOfRowsToRemove
        }
    }

    private fun determineLowestRowThatCouldBlockRock(): Int {
        // above the chamber, all cells contain air
        var reachableConnectedAirSlices = mutableListOf(0 until width)

        for (row in content.indices.reversed()) {
            val airSlicesFromLastRow = reachableConnectedAirSlices.toList()
            reachableConnectedAirSlices.clear()
            val connectedAirCellsInRow = getConnectedAirCellsIn(row)

            // only add connected components of air cells that are reachable from the last set of components
            for (component in airSlicesFromLastRow) {
                connectedAirCellsInRow
                    .filter { it.intersect(component.toSet()).isNotEmpty() }
                    .forEach(reachableConnectedAirSlices::add)
            }
            reachableConnectedAirSlices = reachableConnectedAirSlices.merge().toMutableList()

            if (reachableConnectedAirSlices.isEmpty()) return row
        }

        return -1
    }

    private fun getConnectedAirCellsIn(row: Int) =
        content[row].splitIndices { it == Material.ROCK }.filterNot { it.isEmpty() }

    private fun checkCollision(rockPositions: List<Point>) =
        rockPositions.asSequence().filter {
            !it.isBetweenChamberWalls() || !it.isAboveGround() || (it.isBelowCurrentChamberHeight() && it.overlapsRockInChamber())
        }.any()

    private fun Point.isBetweenChamberWalls() = x in 0 until width
    private fun Point.isAboveGround() = y >= 0
    private fun Point.isBelowCurrentChamberHeight() = y < content.size

    private fun Point.overlapsRockInChamber() = content[y].mapIndexed { column, material -> column to material }
        .filter { (_, material) -> material == Material.ROCK }.any { (column, _) -> x == column }

    private fun addToChamber(rock: Rock) {
        highestRockPosition = max(highestRockPosition, rock.positionOfBottomLeftCorner.y + rock.height - 1)
        while (highestRockPosition >= content.size) {
            content.add(Array(width) { Material.AIR })
        }
        for (position in rock.positions()) {
            content[position.y][position.x] = Material.ROCK
        }
    }

    private fun runCycles(
        totalNumberOfRocks: Long,
        newChamberState: ChamberState,
        oldChamberState: ChamberState
    ): Long {
        val remainingRocksBeforeSecondCycle = totalNumberOfRocks - newChamberState.numberOfRocks

        val rockDifference = newChamberState.numberOfRocks - oldChamberState.numberOfRocks
        val chamberHeightDifference = newChamberState.chamberHeight - oldChamberState.chamberHeight

        val numberOfFullCyclesStillNecessary = remainingRocksBeforeSecondCycle / rockDifference
        val remainingRocksAfterAllCycles = remainingRocksBeforeSecondCycle.mod(rockDifference)

        val chamberHeightGrowthAfterAllCycles = numberOfFullCyclesStillNecessary * chamberHeightDifference
        bottomHeight += chamberHeightGrowthAfterAllCycles

        return remainingRocksAfterAllCycles
    }

    override fun toString(): String {
        if (content.isEmpty()) return "Chamber is empty."

        val bottom = '+' + "-".repeat(width) + '+'
        var visualizedContent =
            content.map { it.joinToString("") { material -> material.visualization.toString() } }
                .mapIndexed { distanceFromTop, row -> "|$row| ${bottomHeight + distanceFromTop}" }

        if (bottomHeight == 0L) {
            visualizedContent = listOf(bottom) + visualizedContent
        }

        return visualizedContent.reversed().joinToString("\n")
    }
}

fun <T> Iterable<T>.toInfiniteSequence() = sequence {
    while (true) {
        for (item in this@toInfiniteSequence) {
            yield(item)
        }
    }
}

fun main() {
    val shapeInput = """####
                   |
                   |.#.
                   |###
                   |.#.
                   |
                   |..#
                   |..#
                   |###
                   |
                   |#
                   |#
                   |#
                   |#
                   |
                   |##
                   |##""".trimMargin().split("\n")

    val shapes = splitByBlankLines(shapeInput).map { it.joinToString("\n") }
    val rockShapes = shapes.map(RockShape::parseFrom)

    val input = readInputLines(PyroclasticFlowChamber::class)[0]

    println("The tower of rocks will be ${part1(input, rockShapes)} units high after 2022 rocks have stopped falling.")
    println(
        "The tower of rocks will be ${part2(input, rockShapes)} units high " +
                "after 1000000000000 rocks have stopped falling."
    )
}

fun part1(input: String, rockShapes: List<RockShape>): Long {
    return getChamberHeightAfterSimulation(input, rockShapes, 2022)
}

fun part2(input: String, rockShapes: List<RockShape>): Long {
    return getChamberHeightAfterSimulation(input, rockShapes, 1000000000000)
}

private fun getChamberHeightAfterSimulation(
    input: String,
    rockShapes: List<RockShape>,
    numberOfRocks: Long
): Long {
    val flowMovements = FlowMovements.parseFlowDirections(input)
    val pyroclasticFlowChamber = PyroclasticFlowChamber(7, flowMovements, rockShapes)

    return pyroclasticFlowChamber.calculateChamberHeight(numberOfRocks)
}

class FlowMovements(private val directions: List<Direction>) : Iterable<Pair<Int, Direction>> {
    init {
        if (directions.isEmpty()) throw IllegalArgumentException("List of flow movements must not be empty.")
    }

    override fun iterator() = directions.toInfiniteIndexedSequence().iterator()

    companion object {
        fun parseFlowDirections(string: String) = string.toList().map(::parseFlowDirection).let(::FlowMovements)
        private fun parseFlowDirection(char: Char) = when (char) {
            '<' -> Direction.LEFT
            '>' -> Direction.RIGHT
            else -> throw IllegalArgumentException("Cannot parse flow direction from $char.")
        }
    }
}


fun <T> Iterable<T>.toInfiniteIndexedSequence() = sequence {
    while (true) {
        for ((index, item) in this@toInfiniteIndexedSequence.withIndex()) {
            yield(index to item)
        }
    }
}
