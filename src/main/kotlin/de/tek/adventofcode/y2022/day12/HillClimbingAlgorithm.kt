package de.tek.adventofcode.y2022.day12

import de.tek.adventofcode.y2022.util.math.Direction
import de.tek.adventofcode.y2022.util.math.Graph
import de.tek.adventofcode.y2022.util.math.Grid
import de.tek.adventofcode.y2022.util.math.Point
import de.tek.adventofcode.y2022.util.readInputLines

class HeightMap(map: Array<Array<Char>>) : Grid<Char, HeightMap.Cell>(map, { position, char -> Cell(position, char) }) {
    class Cell(val position: Point, private val mark: Char) {

        fun isStartingPosition() = mark == 'S'

        fun isGoal() = mark == 'E'

        fun getElevation() =
            if (isStartingPosition()) {
                0
            } else if (isGoal()) {
                26
            } else {
                mark - 'a'
            }

        infix fun isReachableFrom(other: Cell) = this.getElevation() <= other.getElevation() + 1

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Cell) return false

            if (position != other.position) return false

            return true
        }

        override fun hashCode(): Int {
            return position.hashCode()
        }

        override fun toString(): String {
            return "Cell(position=$position, mark=$mark)"
        }
    }

    fun getLengthOfShortestPath(): Int {
        val edges = computeEdges()
        val (start, end) = findStartEnd()

        val shortestPath = Graph(edges).findShortestPath(start, end)

        return shortestPath.size - 1
    }

    private fun computeEdges(): Set<Pair<Cell, Cell>> {
        val edges = mutableSetOf<Pair<Cell, Cell>>()

        for (cell in this) {
            for (direction in Direction.values()) {
                val neighbourPosition = cell.position + direction.toVector()
                val neighbour = at(neighbourPosition)

                if (neighbour?.isReachableFrom(cell) == true) {
                    edges.add(Pair(cell, neighbour))
                }
            }
        }
        return edges
    }

    private fun findStartEnd(): Pair<Cell, Cell> {
        var start: Cell? = null
        var end: Cell? = null
        for (cell in this) {
            if (cell.isStartingPosition()) start = cell
            if (cell.isGoal()) end = cell

            if (start != null && end != null) break
        }

        if (start == null) {
            throw IllegalArgumentException("The height map did not contain a start cell.")
        } else if (end == null) {
            throw IllegalArgumentException("The height map did not contain a goal.")
        }

        return Pair(start, end)
    }

    fun getLengthOfShortestPathFromLowestElevation(): Int {
        val startEndCombinations = findPairsOfLowestElevationAndGoalCells()

        val edges = computeEdges()
        val graph = Graph(edges)

        return startEndCombinations.map { graph.findShortestPath(it.first, it.second).size - 1 }.filter { it > 0 }.min()
    }

    private fun findPairsOfLowestElevationAndGoalCells(): List<Pair<Cell, Cell>> {
        val lowestElevationCells = mutableListOf<Cell>()
        var end: Cell? = null
        for (cell in this) {
            if (cell.getElevation() == 0) {
                lowestElevationCells.add(cell)
            } else if (cell.isGoal()) {
                end = cell
            }
        }

        if (end == null) {
            throw IllegalArgumentException("The height map did not contain a goal.")
        }

        return lowestElevationCells.map { Pair(it, end) }
    }
}

fun main() {
    val input = readInputLines(HeightMap::class)

    fun part1(input: List<String>): Int {
        val array = input.map { it.toCharArray().toTypedArray() }.toTypedArray()
        return HeightMap(array).getLengthOfShortestPath()
    }


    fun part2(input: List<String>): Int {
        val array = input.map { it.toCharArray().toTypedArray() }.toTypedArray()
        return HeightMap(array).getLengthOfShortestPathFromLowestElevation()
    }

    println("The shortest path has length ${part1(input)}.")
    println(
        "The fewest steps required to move starting from any square with elevation a to the location that should " +
                "get the best signal is ${part2(input)}."
    )
}