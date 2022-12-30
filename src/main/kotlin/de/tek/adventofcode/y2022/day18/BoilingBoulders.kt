package de.tek.adventofcode.y2022.day18

import de.tek.adventofcode.y2022.util.math.ElementaryCube
import de.tek.adventofcode.y2022.util.readInputLines

class BoilingBoulders

fun main() {
    val input = readInputLines(BoilingBoulders::class)

    println("The surface area of your scanned lava droplet is ${calculateSurfaceArea(input)}.")
}

fun calculateSurfaceArea(input: List<String>) =
    input.map(::parseCube).map(ElementaryCube::boundary).reduce { first, second -> first + second }.size()

private fun parseCube(string: String): ElementaryCube {
    val ints = string.split(",").map { it.toInt() }.toTypedArray()
    return ElementaryCube.atMinimumCorner(ints)
}