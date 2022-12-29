package de.tek.adventofcode.y2022.util.math


private fun <T> getGridWidth(data: Iterable<Collection<T>>): Int {
    val widths = data.map { it.size }.distinct()
    if (widths.size > 1) {
        throw IllegalArgumentException("The given rows must form a rectangular grid.")
    }

    return widths[0]
}

private fun <T> getGridWidth(data: Array<Array<T>>): Int {
    val widths = data.map { it.size }.distinct()
    if (widths.size > 1) {
        throw IllegalArgumentException("The given rows must form a rectangular grid.")
    }

    return widths[0]
}

open class Grid<S, T>(underlyingGrid: List<MutableList<S>>, transform: (Point, S) -> T) : Iterable<T> {
    protected val grid = underlyingGrid.mapIndexed { rowNumber, row ->
        row.mapIndexed { columnNumber, item ->
            transform(Point(columnNumber, rowNumber), item)
        }.toMutableList()
    }
    protected val maxRow = underlyingGrid.size - 1
    protected val maxColumn = getGridWidth(grid)
    private val rectangle = Rectangle(Point(0, 0), Point(maxColumn, maxRow))

    constructor(
        array: Array<Array<S>>,
        transform: (Point, S) -> T
    ) : this((array.indices).map { row ->
        (0 until getGridWidth(array)).map { column ->
            array[row][column]
        }.toMutableList()
    }, transform)

    fun at(position: Point) = if (contains(position)) grid[position.y][position.x] else null

    override fun iterator(): Iterator<T> = grid.flatten().iterator()

    infix fun contains(position: Point) = position in rectangle

    fun getListOfRows() = grid.map { it.toMutableList() }

    companion object {
        fun <T> withPoints(array: Array<Array<T>>): GridWithPoints<T, T> {
            return GridWithPoints(Grid(array) {_: Point, item: T -> item})
        }
    }
}

class GridWithPoints<S, T>(underlyingGrid: Grid<S, T>) :
    Grid<T, PointWithValue<T>>(underlyingGrid.getListOfRows(), ::PointWithValue) {
    operator fun get(position: Point) = at(position)?.value
    operator fun set(position: Point, newValue: T) {
        grid[position.y][position.x] = PointWithValue(position, newValue)
    }
}

infix fun <S, T> Point.isIn(grid: Grid<S, T>) = grid contains this

data class PointWithValue<T>(val point: Point, val value: T)