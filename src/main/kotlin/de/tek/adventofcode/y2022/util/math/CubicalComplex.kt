package de.tek.adventofcode.y2022.util.math

import kotlin.math.abs

class ElementaryCube(val intervals: List<IntRange>) : Comparable<ElementaryCube> {
    init {
        if (intervals.isEmpty()) throw IllegalArgumentException("Interval list must not be empty.")
        if (intervals.any { it.last - it.first > 1 }) {
            throw IllegalArgumentException("All sides of a cube must have length 1. Given was $intervals.")
        }
    }

    constructor(vararg intervals: IntRange) : this(intervals.toList())

    val dimension = intervals.count { it.last - it.first > 0 }

    fun boundary(): CubicalComplex {
        var sign = -1
        val terms = mutableListOf<Term<ElementaryCube>>()
        for ((index, interval) in intervals.withIndex()) {
            val beforeIndex = intervals.subList(0, index)
            val afterIndex = if (index + 1 < intervals.size) {
                intervals.subList(index + 1, intervals.size)
            } else {
                emptyList()
            }

            terms += Term(
                sign,
                ElementaryCube(beforeIndex + listOf(IntRange(interval.first, interval.first)) + afterIndex)
            )
            terms += Term(
                -sign,
                ElementaryCube(beforeIndex + listOf(IntRange(interval.last, interval.last)) + afterIndex)
            )

            sign *= -1
        }

        return CubicalComplex.from(terms)
    }

    override fun compareTo(other: ElementaryCube): Int {
        val dimensionDifference = this.dimension - other.dimension

        if (dimensionDifference != 0) return dimensionDifference

        val intervalDifferences = intervals.zip(other.intervals).map { (thisInterval, otherInterval) ->
            Pair(thisInterval.first - otherInterval.first, thisInterval.last - otherInterval.last)
        }

        return intervalDifferences.map { it.first }.firstOrNull { it != 0 }
            ?: intervalDifferences.map { it.second }.firstOrNull { it != 0 }
            ?: (this.intervals.size - other.intervals.size)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ElementaryCube) return false

        if (intervals != other.intervals) return false

        return true
    }

    override fun hashCode(): Int {
        return intervals.hashCode()
    }

    override fun toString(): String {
        val stringRepresentation = intervals.joinToString("x") { "[${it.first},${it.last}]" }
        return "ElementaryCube(intervals=$stringRepresentation, dimension=$dimension)"
    }

    companion object {
        fun atMinimumCorner(minCorner: Array<Int>) = ElementaryCube(minCorner.map { IntRange(it, it + 1) })
    }
}

data class Term<T>(val coefficient: Int, val element: T)

operator fun <T> Int.times(term: Term<T>) = term.copy(coefficient = this * term.coefficient)

class CubicalComplex private constructor(val terms: List<Term<ElementaryCube>>) {

    operator fun plus(other: CubicalComplex): CubicalComplex {
        if (this == Zero) return other
        if (other == Zero) return this
        return from(terms + other.terms)
    }

    fun size(): Int = terms.sumOf { abs(it.coefficient) }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CubicalComplex) return false

        if (simplify(terms) != simplify(other.terms)) return false

        return true
    }

    override fun hashCode(): Int {
        return terms.hashCode()
    }

    companion object {
        val Zero = CubicalComplex(emptyList())

        fun from(vararg terms: Term<ElementaryCube>) = from(terms.asList())
        fun from(terms: List<Term<ElementaryCube>>): CubicalComplex {
            if (terms.map { it.element.dimension }.distinct().count() > 1) {
                throw IllegalArgumentException("All elementary cubes must have the same dimension")
            }

            val simplified = simplify(terms)
            return if (simplified.isEmpty()) {
                Zero
            } else {
                CubicalComplex(simplified)
            }
        }

        private fun simplify(terms: List<Term<ElementaryCube>>): List<Term<ElementaryCube>> =
            terms.groupBy({ it.element }) { it.coefficient }
                .mapValues { (_, coefficients) -> coefficients.sum() }
                .map { (element, coefficientSum) -> Term(coefficientSum, element) }
                .filter { it.coefficient != 0 }
                .sortedBy { it.element }
    }
}

operator fun Int.times(complex: CubicalComplex) = CubicalComplex.from(complex.terms.map { this * it })