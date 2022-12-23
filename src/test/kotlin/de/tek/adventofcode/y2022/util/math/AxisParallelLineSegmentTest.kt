package de.tek.adventofcode.y2022.util.math

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class AxisParallelLineSegmentTest : StringSpec({
    "Given non-axis parallel points, the constructor throws an IllegalArgumentException." {
        forAll(
            row(Point(0, 0), Point(1, 1)),
            row(Point(0, 0), Point(-1, 1)),
            row(Point(151, 313), Point(-1, 51))
        ) { first, second ->
            shouldThrow<IllegalArgumentException> {
                AxisParallelLineSegment(first, second)
            }
        }
    }

    "Given an AxisParallelLineSegment, toListOfPoints returns the expected list of Points." {
        forAll(
            row(Point(0, 0), Point(0, 0), listOf(Point(0, 0))),
            row(Point(0, 0), Point(0, 1), listOf(Point(0, 0), Point(0, 1))),
            row(Point(0, 0), Point(0, 2), listOf(Point(0, 0), Point(0, 1), Point(0, 2))),
            row(
                Point(0, 0),
                Point(0, 5),
                listOf(Point(0, 0), Point(0, 1), Point(0, 2), Point(0, 3), Point(0, 4), Point(0, 5))
            ),
            row(Point(8, 10), Point(5, 10), listOf(Point(8, 10), Point(7, 10), Point(6, 10), Point(5, 10))),
            row(
                Point(8, 10),
                Point(8, 5),
                listOf(Point(8, 10), Point(8, 9), Point(8, 8), Point(8, 7), Point(8, 6), Point(8, 5))
            )
        ) { first, second, expectedPoints ->
            AxisParallelLineSegment(first, second).toListOfPoints() shouldBe expectedPoints
        }
    }
})
