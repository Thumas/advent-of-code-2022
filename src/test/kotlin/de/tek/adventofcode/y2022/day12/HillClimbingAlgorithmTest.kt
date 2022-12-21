package de.tek.adventofcode.y2022.day12

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class HeightMapTest : StringSpec({
    "Given example, the length of the shortest path is as expected." {
        val input = """Sabqponm
abcryxxl
accszExk
acctuvwj
abdefghi""".split("\n")

        val array = input.map { it.toCharArray().toTypedArray() }.toTypedArray()
        HeightMap(array).getLengthOfShortestPath() shouldBe 31
    }
})