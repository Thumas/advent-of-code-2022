package de.tek.adventofcode.y2022.day09

import de.tek.adventofcode.y2022.util.math.Direction.*
import de.tek.adventofcode.y2022.util.math.ORIGIN
import de.tek.adventofcode.y2022.util.math.Point
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import org.assertj.core.api.Assertions

class RopeTest : StringSpec({
    "Given initial positions and a direction, moveHead results in correct positions." {
        forAll(
            row(ORIGIN, ORIGIN, UP, Point(0, 1), Point(0, 0)),
            row(ORIGIN, ORIGIN, LEFT, Point(-1, 0), Point(0, 0)),
            row(ORIGIN, ORIGIN, DOWN, Point(0, -1), Point(0, 0)),
            row(ORIGIN, ORIGIN, RIGHT, Point(1, 0), Point(0, 0)),
            row(Point(0, 1), Point(0, 0), RIGHT, Point(1, 1), Point(0, 0)),
            row(Point(0, 1), Point(0, 0), UP, Point(0, 2), Point(0, 1)),
            row(Point(0, 1), Point(0, 0), LEFT, Point(-1, 1), Point(0, 0)),
            row(Point(0, 1), Point(0, 0), DOWN, Point(0, 0), Point(0, 0)),
            row(Point(1, 1), Point(0, 0), RIGHT, Point(2, 1), Point(1, 1)),
            row(Point(1, 1), Point(0, 0), UP, Point(1, 2), Point(1, 1)),
            row(Point(1, 1), Point(0, 0), LEFT, Point(0, 1), Point(0, 0)),
            row(Point(1, 1), Point(0, 0), DOWN, Point(1, 0), Point(0, 0)),
        ) { headStart, tailStart, direction, expectedPositionHead, expectedPositionTail ->
            val rope = Rope(headStart, tailStart)
            rope.move(direction)

            val expectedRope = Rope(expectedPositionHead, expectedPositionTail)
            Assertions.assertThat(rope).usingRecursiveComparison().ignoringFieldsMatchingRegexes("tailHistory")
                .isEqualTo(expectedRope)
        }
    }

    "Given movedHead called for a list of directions, " +
            "getNumberOfVisitedTailPositions returns the expected number of positions visited by the tail." {
                forAll(
                    row(
                        2,
                        listOf(
                            List(4) { RIGHT },
                            List(4) { UP },
                            List(3) { LEFT },
                            listOf(DOWN),
                            List(4) { RIGHT },
                            listOf(DOWN),
                            List(5) { LEFT },
                            listOf(RIGHT, RIGHT)
                        ).flatten(),
                        13
                    ),
                    row(
                        10,
                        listOf(List(5) { RIGHT },
                            List(8) { UP },
                            List(8) { LEFT },
                            List(3) { DOWN },
                            List(17) { RIGHT },
                            List(10) { DOWN },
                            List(25) { LEFT },
                            List(20) { UP }).flatten(),
                        36
                    )
                ) { numberOfKnots, moves, expectedResult ->
                    val rope = Rope.atOrigin(numberOfKnots)
                    for (move in moves) {
                        rope.move(move)
                        println(rope.visualize() + '\n')
                    }

                    rope.getNumberOfVisitedTailPositions() shouldBe expectedResult
                }
            }
})