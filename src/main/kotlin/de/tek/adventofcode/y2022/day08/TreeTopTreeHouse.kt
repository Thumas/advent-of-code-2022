package de.tek.adventofcode.y2022.day08

import de.tek.adventofcode.y2022.util.math.Direction
import de.tek.adventofcode.y2022.util.readInputLines

class TreeGrid(treeSizes: Array<Array<Int>>) {
    private val trees: Array<Array<Tree>>
    private val maxRow = treeSizes.size - 1
    private val maxColumn: Int

    init {
        maxColumn = getGridWidth(treeSizes) - 1
        trees = Array(treeSizes.size) { row -> Array(maxColumn + 1) { column -> Tree(treeSizes[row][column]) } }
    }

    private fun getGridWidth(treeSizes: Array<Array<Int>>): Int {
        val widths = treeSizes.map { it.size }.distinct()
        if (widths.size > 1) {
            throw IllegalArgumentException("The given tree sizes must form a rectangular grid.")
        }

        return widths[0]
    }

    fun getNumberOfVisibleTrees(): Int {
        setBorderVisibleFromOutside()
        gridCells()
            .filter { (_, tree) -> tree.isVisible() == null }
            .forEach { (position, tree) -> checkNeighbours(position, tree) }

        return allTrees().count { it.isVisible() == true }
    }

    private fun setBorderVisibleFromOutside() {
        for (tree in trees[0]) {
            tree.setVisible(Direction.UP)
        }
        for (tree in trees[maxRow]) {
            tree.setVisible(Direction.DOWN)
        }
        for (row in 0..maxRow) {
            trees[row][0].setVisible(Direction.LEFT)
            trees[row][maxColumn].setVisible(Direction.RIGHT)
        }
    }

    private fun gridCells(): Sequence<Pair<GridPosition, Tree>> =
        IntRange(0, maxRow).flatMap { row ->
            IntRange(0, maxColumn).map { column ->
                GridPosition(row, column)
            }.map { position ->
                position to trees.at(position)
            }
        }.asSequence()

    inner class GridPosition(val row: Int, val column: Int) {
        private val grid = this@TreeGrid
        infix fun isIn(grid: TreeGrid) = row in 0..grid.maxRow && column in 0..maxColumn

        fun distancesToBorder(grid: TreeGrid) = mapOf(
            Direction.UP to row,
            Direction.LEFT to column,
            Direction.DOWN to grid.maxRow - row,
            Direction.RIGHT to grid.maxColumn - column
        )

        operator fun plus(direction: Direction) = GridPosition(row + direction.deltaY, column + direction.deltaX)

        /**
         * Returns an iterator along the grid in the given direction. The first element returned by {@link Iterator#next()}
         * is the next position next to this position in the given direction.
         */
        fun iterator(direction: Direction) = object : Iterator<GridPosition> {
            private var currentPosition = this@GridPosition

            override fun hasNext() = (currentPosition + direction) isIn grid

            override fun next(): GridPosition {
                currentPosition += direction

                return currentPosition
            }

        }
    }

    private fun <T> Array<Array<T>>.at(position: GridPosition) = this[position.row][position.column]

    private fun checkNeighbours(position: GridPosition, tree: Tree) {
        val directionsSortedByDistanceToBorder =
            position.distancesToBorder(this).entries.sortedBy { it.value }.map { it.key }

        for (direction in directionsSortedByDistanceToBorder) {
            if (checkNeighbour(position, direction, tree)) return
        }
    }

    private fun checkNeighbour(
        position: GridPosition,
        direction: Direction,
        tree: Tree
    ): Boolean {
        val neighbouringTree = getNeighbouringTree(position, direction) ?: return true

        if (neighbouringTree.size >= tree.size) {
            tree.setInvisible(direction)
            return false
        }

        val neighbouringTreeVisible =
            neighbouringTree.isVisible(direction) ?: checkNextNeighbour(
                position,
                direction,
                neighbouringTree
            )

        return if (neighbouringTreeVisible) {
            tree.setVisible(direction)
            true
        } else {
            checkNextNeighbour(position, direction, tree)
        }
    }

    private fun getNeighbouringTree(position: GridPosition, direction: Direction): Tree? {
        val neighbourCell = position + direction
        return if (neighbourCell isIn this) {
            trees.at(neighbourCell)
        } else {
            null
        }
    }

    private fun checkNextNeighbour(
        position: GridPosition,
        direction: Direction,
        neighbouringTree: Tree
    ): Boolean {
        return checkNeighbour(position + direction, direction, neighbouringTree)
    }

    private fun allTrees(): Sequence<Tree> = trees.flatten().asSequence()

    fun getHighestScenicScore(): Int {
        for ((position, tree) in gridCells()) {
            for (direction in Direction.values()) {
                computeVisibilities(position, direction, tree)
            }
        }

        return allTrees().map { it.getScenicScore() }.max()
    }

    private fun computeVisibilities(
        position: GridPosition,
        direction: Direction,
        pointOfView: Tree
    ) {
        val treesInLine = mutableListOf<Tree>()

        position.iterator(direction).asSequence()
            .map { trees.at(it) }
            .takeWhile { neighbouringTree -> pointOfView.addVisibleTree(direction, neighbouringTree) }
            .takeWhile { neighbouringTree -> pointOfView.isBiggerThan(neighbouringTree) }
            .forEach { neighbouringTree ->
                treesInLine.removeIf { otherTree ->
                    !otherTree.addVisibleTree(
                        direction,
                        neighbouringTree
                    ) || !otherTree.isBiggerThan(neighbouringTree)
                }

                treesInLine.add(neighbouringTree)
            }
    }
}

fun main() {
    val input = readInputLines(TreeGrid::class)
    val treeSizes = input.map { it.map { char -> char.digitToInt() }.toTypedArray() }.toTypedArray()
    val treeGrid = TreeGrid(treeSizes)

    fun part1() = treeGrid.getNumberOfVisibleTrees()
    fun part2() = treeGrid.getHighestScenicScore()

    println("The number of visible trees in the grid is ${part1()}.")
    println("The highest scenic score is ${part2()}.")
}