package de.tek.adventofcode.y2022.util.math

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import org.assertj.core.api.Assertions

class OneNormTest : StringSpec({

    "Given a point and a radius, ballWithRadius returns the expected points." {
        forAll(
            row(Point(0, 0), 1, setOf(Point(0, 0), Point(0, 1), Point(1, 0), Point(0, -1), Point(-1, 0))),
            row(
                Point(1, 5), 6, setOf(
                    Point(1, 5),
                    Point(-5, 5), Point(-4, 5), Point(-3, 5), Point(-2, 5), Point(-1, 5), Point(0, 5),
                    Point(7, 5), Point(6, 5), Point(5, 5), Point(4, 5), Point(3, 5), Point(2, 5),
                    Point(-4, 6), Point(-3, 6), Point(-2, 6), Point(-1, 6), Point(0, 6), Point(1, 6),
                    Point(6, 6), Point(5, 6), Point(4, 6), Point(3, 6), Point(2, 6),
                    Point(-3, 7), Point(-2, 7), Point(-1, 7), Point(0, 7), Point(1, 7),
                    Point(5, 7), Point(4, 7), Point(3, 7), Point(2, 7),
                    Point(-2, 8), Point(-1, 8), Point(0, 8), Point(1, 8),
                    Point(4, 8), Point(3, 8), Point(2, 8),
                    Point(-1, 9), Point(0, 9), Point(1, 9),
                    Point(3, 9), Point(2, 9),
                    Point(0, 10), Point(1, 10),
                    Point(2, 10),
                    Point(1, 11), Point(1, -1),
                    Point(-4, 4), Point(-3, 4), Point(-2, 4), Point(-1, 4), Point(0, 4), Point(1, 4),
                    Point(6, 4), Point(5, 4), Point(4, 4), Point(3, 4), Point(2, 4),
                    Point(-3, 3), Point(-2, 3), Point(-1, 3), Point(0, 3), Point(1, 3),
                    Point(5, 3), Point(4, 3), Point(3, 3), Point(2, 3),
                    Point(-2, 2), Point(-1, 2), Point(0, 2), Point(1, 2),
                    Point(4, 2), Point(3, 2), Point(2, 2),
                    Point(-1, 1), Point(0, 1), Point(1, 1),
                    Point(3, 1), Point(2, 1),
                    Point(0, 0), Point(1, 0),
                    Point(2, 0),
                )
            ),
        ) { point, radius, expectedResult ->
            val ball = OneNorm.ballOfRadius(point, radius)
            Assertions.assertThat(ball).containsExactlyInAnyOrderElementsOf(expectedResult)
            ball.size shouldBe expectedResult.size
            ball shouldBe expectedResult
        }
    }

})
