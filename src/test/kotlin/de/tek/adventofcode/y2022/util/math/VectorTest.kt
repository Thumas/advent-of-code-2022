package de.tek.adventofcode.y2022.util.math

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
class VectorTest : StringSpec({
    "Given vectors, isParallel returns true iff they are parallel." {
        forAll(
            row(Vector(1,0), Vector(1,0), true),
            row(Vector(1,0), Vector(0,1), false),
            row(Vector(1,0), Vector(2,0), true),
            row(Vector(1,0), Vector(-1,0), true),
            row(Vector(1,0), Vector(-2,0), true),
            row(Vector(1,0), Vector(0,-1), false),
            row(Vector(1,1), Vector(1,0), false),
            row(Vector(1,1), Vector(0,1), false),
            row(Vector(1,1), Vector(2,2), true),
            row(Vector(2,1), Vector(4,2), true),
            row(Vector(2,1), Vector(1,1), false),
            row(Vector(2,1), Vector(0,1), false),
            row(Vector(2,1), Vector(1,0), false),
            row(Vector(2,1), Vector(0,0), true),
        ) { first, second, expectedResult ->
            first.isParallelTo(second) shouldBe expectedResult
            second.isParallelTo(first) shouldBe expectedResult
        }
    }
})