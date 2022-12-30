package de.tek.adventofcode.y2022.util.math

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

class ElementaryCubeTest : FunSpec({

    context("Given an empty list, ElementaryCube constructor throws IllegalArgumentException.") {
        shouldThrow<IllegalArgumentException> { ElementaryCube(emptyList()) }
    }

    context("Given intervals where one is longer than 1, ElementaryCube constructor throws IllegalArgumentException.") {
        withData(
            listOf(IntRange(0, 2)),
            listOf(IntRange(0, 1), IntRange(-1, 1)),
            listOf(IntRange(0, 2), IntRange(0, 1)),
            listOf(IntRange(0, 0), IntRange(0, 2))
        ) { intervals ->
            shouldThrow<IllegalArgumentException> { ElementaryCube(intervals) }
        }
    }

    data class DimensionTestData(val intervals: List<IntRange>, val dimension: Int)

    context("Given intervals, dimension returns the number of intervals of length 1.") {
        withData(
            nameFn = { "${it.intervals} has dimension ${it.dimension}" },
            DimensionTestData(listOf(IntRange(0, 0)), 0),
            DimensionTestData(listOf(IntRange(0, 0), IntRange(0, 0)), 0),
            DimensionTestData(listOf(IntRange(0, 0), IntRange(0, 1)), 1),
            DimensionTestData(listOf(IntRange(0, 1), IntRange(0, 1)), 2),
            DimensionTestData(listOf(IntRange(-1, 0), IntRange(0, 1)), 2),
            DimensionTestData(listOf(IntRange(-1, 0), IntRange(0, 0)), 1),
            DimensionTestData(listOf(IntRange(-1, 0), IntRange(1, 2)), 2),
            DimensionTestData(listOf(IntRange(-1, 0), IntRange(1, 2), IntRange(0, 1)), 3),
            DimensionTestData(listOf(IntRange(-1, 0), IntRange(1, 1), IntRange(0, 1)), 2),
            DimensionTestData(listOf(IntRange(-1, 0), IntRange(0, 1), IntRange(0, 0)), 2),
            DimensionTestData(listOf(IntRange(0, 0), IntRange(0, 1), IntRange(0, 0)), 1),
            DimensionTestData(listOf(IntRange(0, 0), IntRange(0, 0), IntRange(0, 0)), 0),
            DimensionTestData(listOf(IntRange(0, 0), IntRange(0, 0), IntRange(0, 1)), 1),
        ) { (intervals, expectedDimension) ->
            ElementaryCube(intervals).dimension shouldBe expectedDimension
        }
    }

    data class CompareToTestData(
        val intervals1: List<IntRange>, val intervals2: List<IntRange>, val comparisonResult: Int
    )

    context("Given two ElementaryCubes, compareTo returns the expected result.") {
        withData(
            nameFn = { "${it.intervals1} compareTo ${it.intervals2} should return ${it.comparisonResult}" },
            CompareToTestData(listOf(IntRange(0, 0)), listOf(IntRange(1, 1)), -1),
            CompareToTestData(listOf(IntRange(0, 0)), listOf(IntRange(0, 1)), -1),
            CompareToTestData(listOf(IntRange(0, 1)), listOf(IntRange(1, 1)), 1),
            CompareToTestData(listOf(IntRange(0, 1)), listOf(IntRange(0, 1)), 0),
            CompareToTestData(listOf(IntRange(0, 0)), listOf(IntRange(0, 0)), 0),
            CompareToTestData(listOf(IntRange(0, 0), IntRange(0, 1)), listOf(IntRange(0, 0)), 1),
            CompareToTestData(listOf(IntRange(0, 0), IntRange(0, 1)), listOf(IntRange(0, 0), IntRange(0, 1)), 0),
            CompareToTestData(listOf(IntRange(0, 0), IntRange(0, 0)), listOf(IntRange(0, 0), IntRange(0, 1)), -1),
            CompareToTestData(listOf(IntRange(0, 0), IntRange(0, 0)), listOf(IntRange(0, 0), IntRange(1, 1)), -1),
            CompareToTestData(listOf(IntRange(0, 1), IntRange(0, 0)), listOf(IntRange(0, 0), IntRange(1, 1)), 1),
            CompareToTestData(listOf(IntRange(0, 1)), listOf(IntRange(0, 0), IntRange(0, 1)), 1),
        ) { (firstCubeIntervals, secondCubeIntervals, expectedComparisonResult) ->
            ElementaryCube(firstCubeIntervals).compareTo(ElementaryCube(secondCubeIntervals)) shouldBe expectedComparisonResult
        }
    }

    data class AtMinimumCornerTestData(val point: Array<Int>, val expectedElementaryCube: ElementaryCube)

    context("Given a point, atMinimumCorner returns the ElementaryCube with the given point as component-wise minimum.") {
        withData(
            AtMinimumCornerTestData(
                arrayOf(0, 0, 0), ElementaryCube(listOf(IntRange(0, 1), IntRange(0, 1), IntRange(0, 1)))
            ),
            AtMinimumCornerTestData(
                arrayOf(1, 0, 0), ElementaryCube(listOf(IntRange(1, 2), IntRange(0, 1), IntRange(0, 1)))
            ),
            AtMinimumCornerTestData(
                arrayOf(1, -1, 0), ElementaryCube(listOf(IntRange(1, 2), IntRange(-1, 0), IntRange(0, 1)))
            ),
            AtMinimumCornerTestData(
                arrayOf(1, -1, 2), ElementaryCube(listOf(IntRange(1, 2), IntRange(-1, 0), IntRange(2, 3)))
            ),
            AtMinimumCornerTestData(
                arrayOf(1), ElementaryCube(listOf(IntRange(1, 2)))
            ),
            AtMinimumCornerTestData(
                arrayOf(1, 0), ElementaryCube(listOf(IntRange(1, 2), IntRange(0, 1)))
            ),

            ) { (point, expectedElementaryCube) ->
            ElementaryCube.atMinimumCorner(point) shouldBe expectedElementaryCube
        }
    }

    data class BoundaryTestData(val cube: ElementaryCube, val boundary: CubicalComplex)

    context("Given cube, the boundary is as expected.") {
        withData(
            BoundaryTestData(
                ElementaryCube.atMinimumCorner(arrayOf(0, 0, 0)), CubicalComplex.from(
                    Term(-1, ElementaryCube(IntRange(0, 0), IntRange(0, 1), IntRange(0, 1))),
                    Term(1, ElementaryCube(IntRange(1, 1), IntRange(0, 1), IntRange(0, 1))),
                    Term(1, ElementaryCube(IntRange(0, 1), IntRange(0, 0), IntRange(0, 1))),
                    Term(-1, ElementaryCube(IntRange(0, 1), IntRange(1, 1), IntRange(0, 1))),
                    Term(-1, ElementaryCube(IntRange(0, 1), IntRange(0, 1), IntRange(0, 0))),
                    Term(1, ElementaryCube(IntRange(0, 1), IntRange(0, 1), IntRange(1, 1)))
                )
            ),
            BoundaryTestData(
                ElementaryCube.atMinimumCorner(arrayOf(0, 0)), CubicalComplex.from(
                    Term(-1, ElementaryCube(IntRange(0, 0), IntRange(0, 1))),
                    Term(1, ElementaryCube(IntRange(1, 1), IntRange(0, 1))),
                    Term(1, ElementaryCube(IntRange(0, 1), IntRange(0, 0))),
                    Term(-1, ElementaryCube(IntRange(0, 1), IntRange(1, 1)))
                )
            ),
            BoundaryTestData(
                ElementaryCube.atMinimumCorner(arrayOf(0)), CubicalComplex.from(
                    Term(-1, ElementaryCube(IntRange(0, 0))),
                    Term(1, ElementaryCube(IntRange(1, 1)))
                )
            ),
        ) { (cube, expectedBoundary) ->
            cube.boundary() shouldBe expectedBoundary
        }
    }
})

class CubicalComplexTest : FunSpec({

    context("Given two cubes with inverse coefficients, the CubicalComplex is Zero.") {
        withData(
            listOf(Term(-1, ElementaryCube(IntRange(0, 0))), Term(1, ElementaryCube(IntRange(0, 0)))),
            listOf(Term(-2, ElementaryCube(IntRange(0, 0))), Term(2, ElementaryCube(IntRange(0, 0)))),
            listOf(Term(1, ElementaryCube(IntRange(0, 0))), Term(-1, ElementaryCube(IntRange(0, 0)))),
            listOf(Term(-1, ElementaryCube(IntRange(0, 1))), Term(1, ElementaryCube(IntRange(0, 1)))),
            listOf(Term(-2, ElementaryCube(IntRange(0, 1))), Term(2, ElementaryCube(IntRange(0, 1)))),
            listOf(Term(1, ElementaryCube(IntRange(0, 1))), Term(-1, ElementaryCube(IntRange(0, 1)))),
            listOf(
                Term(-1, ElementaryCube(IntRange(0, 1), IntRange(0, 1))),
                Term(1, ElementaryCube(IntRange(0, 1), IntRange(0, 1)))
            ),
            listOf(
                Term(-2, ElementaryCube(IntRange(0, 1), IntRange(0, 1))),
                Term(2, ElementaryCube(IntRange(0, 1), IntRange(0, 1)))
            ),
            listOf(
                Term(1, ElementaryCube(IntRange(0, 1), IntRange(0, 1))),
                Term(-1, ElementaryCube(IntRange(0, 1), IntRange(0, 1)))
            ),
        ) { terms ->
            CubicalComplex.from(terms) shouldBeSameInstanceAs CubicalComplex.Zero
        }
    }

    context("Given cubes with zero coefficients, the CubicalComplex is Zero.") {
        withData(
            listOf(Term(0, ElementaryCube(IntRange(0, 0))), Term(0, ElementaryCube(IntRange(0, 0)))), listOf(
                Term(0, ElementaryCube(IntRange(0, 1), IntRange(0, 1))),
                Term(0, ElementaryCube(IntRange(0, 1), IntRange(0, 1)))
            )
        ) { terms ->
            CubicalComplex.from(terms) shouldBeSameInstanceAs CubicalComplex.Zero
        }
    }

    data class SumTestData(val terms: List<Term<ElementaryCube>>, val size: Int)

    context("Given a complex, size returns the sum of the absolute coefficients.") {
        withData(
            SumTestData(listOf(Term(1, ElementaryCube(IntRange(0, 1)))), 1),
            SumTestData(listOf(Term(2, ElementaryCube(IntRange(0, 1)))), 2),
            SumTestData(listOf(Term(2, ElementaryCube(IntRange(0, 1))), Term(1, ElementaryCube(IntRange(1, 2)))), 3),
            SumTestData(
                listOf(
                    Term(2, ElementaryCube(IntRange(0, 1), IntRange(0, 1))),
                    Term(1, ElementaryCube(IntRange(1, 2), IntRange(1, 2)))
                ), 3
            ),

            ) { (terms, expectedSize) ->
            CubicalComplex.from(terms).size() shouldBe expectedSize
        }
    }
})