package de.tek.adventofcode.y2022.day17

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

class Rock(private val shape: RockShape, position: Point) {
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
 * Models the tall, narrow chamber. Only its content is modelled as data, not the floor or the walls.
 */
class PyroclasticFlowChamber(
    private val width: Int, flowMovements: List<Direction>, rockShapes: List<RockShape>
) {
    private val content = mutableListOf<Array<Material>>()
    var highestRockPosition = -1
        private set

    private fun currentChamberHeight() = content.size

    private val flowMovements = flowMovements.toInfiniteSequence().iterator()

    private val rockShapes = rockShapes.toInfiniteSequence().iterator()

    fun dropNextRock() {
        val rockPosition = Point(2, highestRockPosition + 4)
        val rockShape = rockShapes.next()
        val rock = Rock(rockShape, rockPosition)

        do {
            val flowDirection = flowMovements.next()
            rock.moveIn(flowDirection)
            if (checkCollision(rock.positions())) {
                rock.moveIn(oppositeOf(flowDirection))
            }
            rock.moveIn(Direction.DOWN)
        } while (!checkCollision(rock.positions()))

        rock.moveIn(Direction.UP)
        addToChamber(rock)
    }

    private fun checkCollision(rockPositions: List<Point>) =
        rockPositions.asSequence().filter {
            !it.isBetweenChamberWalls() || !it.isAboveGround() || (it.isBelowCurrentChamberHeight() && it.overlapsRockInChamber())
        }.any()

    private fun Point.isBetweenChamberWalls() = x in 0 until width
    private fun Point.isAboveGround() = y >= 0
    private fun Point.isBelowCurrentChamberHeight() = y < currentChamberHeight()

    private fun Point.overlapsRockInChamber() = content[y].mapIndexed { column, material -> column to material }
        .filter { (_, material) -> material == Material.ROCK }.any { (column, _) -> x == column }

    private fun addToChamber(rock: Rock) {
        highestRockPosition = max(highestRockPosition, rock.positionOfBottomLeftCorner.y + rock.height - 1)
        while (highestRockPosition >= currentChamberHeight()) {
            content.add(Array(width) { Material.AIR })
        }
        for (position in rock.positions()) {
            content[position.y][position.x] = Material.ROCK
        }
    }

    override fun toString(): String {
        val bottom = '+' + "-".repeat(width) + '+'
        val visualizedContent =
            content.map { it.joinToString("") { material -> material.visualization.toString() } }.map { "|$it|" }

        return (listOf(bottom) + visualizedContent).reversed().joinToString("\n")
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
}

fun part1(input: String, rockShapes: List<RockShape>): Int {
    val flowMovements = input.toList().map(::parseFlowDirection)
    val pyroclasticFlowChamber = PyroclasticFlowChamber(7, flowMovements, rockShapes)

    repeat(2022) { pyroclasticFlowChamber.dropNextRock() }

    println(pyroclasticFlowChamber)

    return pyroclasticFlowChamber.highestRockPosition + 1
}

fun parseFlowDirection(it: Char) = if (it == '<') Direction.LEFT else Direction.RIGHT
