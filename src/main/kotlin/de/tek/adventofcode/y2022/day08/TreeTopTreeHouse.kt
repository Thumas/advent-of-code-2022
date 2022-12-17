package de.tek.adventofcode.y2022.day08

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


    enum class Direction(val deltaX: Int, val deltaY: Int) {
        UP(0, -1), LEFT(-1, 0), DOWN(0, 1), RIGHT(1, 0)
    }

    class Tree(val size: Int) {
        private val visibleFromDirection = mutableMapOf<Direction, Boolean>()

        fun setVisible(direction: Direction) {
            visibleFromDirection[direction] = true
        }

        fun setInvisible(direction: Direction) {
            visibleFromDirection[direction] = false
        }

        /**
         * Returns true, if the tree is visible from the given direction, false if it is not visible and null, if no
         * visibility information is available.
         */
        fun isVisible(direction: Direction): Boolean? =
            if (visibleFromDirection.containsKey(direction)) visibleFromDirection[direction] else null

        /**
         * Returns true, if the tree is visible from some direction, false if it is definitely not visible from any
         * direction, and null otherwise (i.e. if it is invisible from some directions and the rest has not been
         * determined yet).
         */
        fun isVisible(): Boolean? {
            val isVisibleFromSomeDirection = visibleFromDirection.values.any { it }

            return if (isVisibleFromSomeDirection) {
                true
            } else if (visibleFromDirection.size < 4){
                null
            } else {
                false
            }
        }

        override fun toString(): String {
            return "Tree(size=$size, visibleFromDirection=$visibleFromDirection)"
        }
    }

    fun getNumberOfVisibleTrees(): Int {
        setBorderVisible()
        for (row in 1 until maxRow) {
            for (column in 1 until maxColumn) {
                val tree = trees[row][column]
                if (tree.isVisible() == null) {
                    checkNeighbours(row, column, tree)
                }
            }
        }

        return allTrees().count { it.isVisible() == true }
    }

    private fun setBorderVisible() {
        trees[0].forEach { it.setVisible(Direction.UP) }
        trees[maxRow].forEach { it.setVisible(Direction.DOWN) }
        IntRange(0, maxRow).forEach { row ->
            trees[row][0].setVisible(Direction.LEFT)
            trees[row][maxColumn].setVisible(Direction.RIGHT)
        }
    }

    private fun checkNeighbours(row: Int, column: Int, tree: Tree) {
        val distancesToBorder = mapOf(
            Direction.UP to row,
            Direction.LEFT to column,
            Direction.DOWN to maxRow - row,
            Direction.RIGHT to maxColumn - column
        )

        for ((direction, _) in distancesToBorder.entries.sortedBy { it.value }) {
            if (checkNeighbour(row, column, direction, tree)) return
        }
    }

    private fun checkNeighbour(
        row: Int,
        column: Int,
        direction: Direction,
        tree: Tree
    ): Boolean {
        val neighbouringTree = getNeighbouringTree(row, column, direction)

        if (neighbouringTree.size >= tree.size) {
            tree.setInvisible(direction)
            return false
        }

        val neighbouringTreeVisible =
            neighbouringTree.isVisible(direction) ?: checkNextNeighbour(row, column, direction, neighbouringTree)

        return if (neighbouringTreeVisible) {
            tree.setVisible(direction)
            true
        } else {
            checkNextNeighbour(row, column, direction, tree)
        }
    }

    private fun getNeighbouringTree(row: Int, column: Int, direction: Direction) =
        trees[moveRow(row, direction)][moveColumn(column, direction)]

    private fun checkNextNeighbour(
        row: Int,
        column: Int,
        direction: Direction,
        neighbouringTree: Tree
    ) = checkNeighbour(moveRow(row, direction), moveColumn(column, direction), direction, neighbouringTree)

    private fun moveRow(row: Int, direction: Direction) = row + direction.deltaY

    private fun moveColumn(column: Int, direction: Direction) = column + direction.deltaX

    private fun allTrees(): Sequence<Tree> = trees.flatten().asSequence()
}

fun main() {
    val input = readInputLines(TreeGrid::class)

    fun part1(input: List<String>): Int {
        val treeSizes = input.map { it.map { char -> char.digitToInt() }.toTypedArray() }.toTypedArray()
        return TreeGrid(treeSizes).getNumberOfVisibleTrees()
    }

    println("The number of visible trees in the grid is ${part1(input)}.")
}