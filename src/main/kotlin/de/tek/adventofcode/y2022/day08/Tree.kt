package de.tek.adventofcode.y2022.day08

import de.tek.adventofcode.y2022.util.math.Direction

class Tree(val size: Int) {
    private val visibleFromDirection = mutableMapOf<Direction, Boolean>()

    private val otherTreesVisibleFromDirection = Direction.values().associateWith { mutableSetOf<Tree>() }

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
        } else if (visibleFromDirection.size < 4) {
            null
        } else {
            false
        }
    }

    fun isBiggerThan(otherTree: Tree) = otherTree.size < this.size

    fun addVisibleTree(direction: Direction, otherTree: Tree): Boolean =
        otherTreesVisibleFromDirection[direction]!!.add(otherTree)

    fun getScenicScore() =
        otherTreesVisibleFromDirection.values.map { it.size }.fold(1) { product, factor -> product * factor }

    override fun toString(): String {
        return "Tree(size=$size, visibleFromDirection=$visibleFromDirection, otherTreesVisibleFromDirection=$otherTreesVisibleFromDirection)"
    }
}