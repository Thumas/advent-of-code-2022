package de.tek.adventofcode.y2022.util.math

open class Grid<S, T>(private val array: Array<Array<S>>, private val transform: (Point, S) -> T) : Iterable<T> {
    protected val grid: List<MutableList<T>>
    protected val maxRow = array.size - 1
    protected val maxColumn: Int

    init {
        maxColumn = getGridWidth() - 1
        grid = (0..maxRow).map { row ->
            (0..maxColumn).map { column -> transform(Point(column, row), array[row][column]) }.toMutableList()
        }
    }

    private fun getGridWidth(): Int {
        val widths = array.map { it.size }.distinct()
        if (widths.size > 1) {
            throw IllegalArgumentException("The given rows must form a rectangular grid.")
        }

        return widths[0]
    }

    fun at(position: Point) = if (contains(position)) grid[position.y][position.x] else null

    override fun iterator(): Iterator<T> = grid.flatten().iterator()

    infix fun contains(position: Point) = position.y in 0..maxRow && position.x in 0..maxColumn
}

infix fun <S,T> Point.isIn(grid: Grid<S,T>) = grid contains this
