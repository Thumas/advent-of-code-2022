package de.tek.adventofcode.y2022.day18

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BoilingBouldersTest : FunSpec({
    context("Given two adjacent cubes, the surface area is 10.") {
        val input = listOf("1,1,1","2,1,1")
        calculateSurfaceArea(input) shouldBe 10
    }

    context("Given the example input, the total surface area is 64.") {
        val input = """2,2,2
1,2,2
3,2,2
2,1,2
2,3,2
2,2,1
2,2,3
2,2,4
2,2,6
1,2,5
3,2,5
2,1,5
2,3,5""".split("\n")

        calculateSurfaceArea(input) shouldBe 64
    }
})