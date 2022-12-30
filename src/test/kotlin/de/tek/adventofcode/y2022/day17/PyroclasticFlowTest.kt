package de.tek.adventofcode.y2022.day17

import de.tek.adventofcode.y2022.util.math.Direction
import de.tek.adventofcode.y2022.util.math.Point
import de.tek.adventofcode.y2022.util.splitByBlankLines
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class RockShapeTest : FunSpec({
    val horizontalLine = "####"

    val tBlock = """.#.
                   |###
                   |.#.""".trimMargin()

    val inverseLBlock = """..#
                          |..#
                          |###""".trimMargin()

    context("Given a rock shape, the string representation of the return value matches the input.") {
        withData(horizontalLine, tBlock, inverseLBlock) { string ->
            val shape = RockShape.parseFrom(string)

            shape.toString() shouldBe string
        }
    }

    data class IteratorTestData(val input: String, val expectedPositions: Set<Point>)

    context("Given a rock shape, the iterator returns the expected rock positions.") {
        withData(
            IteratorTestData(horizontalLine, listOf(0 to 0, 1 to 0, 2 to 0, 3 to 0).map(::Point).toSet()),
            IteratorTestData(tBlock, listOf(1 to 0, 0 to 1, 1 to 1, 2 to 1, 1 to 2).map(::Point).toSet()),
            IteratorTestData(inverseLBlock, listOf(0 to 0, 1 to 0, 2 to 0, 2 to 1, 2 to 2).map(::Point).toSet()),
        ) { (string, expectedPositions) ->
            val shape = RockShape.parseFrom(string)

            val positions = shape.filter { it.value == Material.ROCK }.map { it.point }.toList()
            positions shouldHaveSize expectedPositions.size
            positions shouldContainExactlyInAnyOrder expectedPositions
        }
    }
})

class PyroclasticFlowChamberTest : FunSpec({
    val iBlock = """#
                   |#
                   |#
                   |#""".trimMargin()

    val squareBlock = """##
                        |##""".trimMargin()

    /*
|#......|
|#......|
|#......|
|#......|
|#......|
|#......|
|###....|
|###....|
+-------+
     */
    context("Given two stacked i-blocks and a square block right of them, then the highest rock position is 7.") {
        val flowMovements = FlowMovements(
            listOf(
                Direction.LEFT,
                Direction.LEFT,
                Direction.LEFT,
                Direction.LEFT,
                Direction.LEFT,
                Direction.LEFT,
                Direction.LEFT,
                Direction.LEFT,
                Direction.RIGHT,
                Direction.RIGHT,
                Direction.RIGHT,
                Direction.RIGHT
            )
        )
        val rockShapes = listOf(iBlock, iBlock, squareBlock).map(RockShape::parseFrom)
        val pyroclasticFlowChamber = PyroclasticFlowChamber(7, flowMovements, rockShapes)

        pyroclasticFlowChamber.calculateChamberHeight(3) shouldBe 8
    }
})

class PyroclasticFlowTest : FunSpec({
    val shapeInput = """####
                   |
                   |.#.
                   |###
                   |.#.
                   |
                   |..#
                   |..#
                   |###
                   |
                   |#
                   |#
                   |#
                   |#
                   |
                   |##
                   |##""".trimMargin().split("\n")

    val shapes = splitByBlankLines(shapeInput).map { it.joinToString("\n") }
    val rockShapes = shapes.map(RockShape::parseFrom)

    val exampleInput = ">>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>"

    context("Given the example input, the rock tower has height 3068.") {
        part1(exampleInput, rockShapes) shouldBe 3068L
    }

    context("Given the example input, after 3 rocks, the chamber has the expected string visualization.") {
        val expectedVisualization = """|..#....| 5
|..#....| 4
|####...| 3
|..###..| 2
|...#...| 1
|..####.| 0
+-------+"""

        val flowMovements = FlowMovements.parseFlowDirections(exampleInput)
        val pyroclasticFlowChamber = PyroclasticFlowChamber(7, flowMovements, rockShapes)

        pyroclasticFlowChamber.calculateChamberHeight(3)

        pyroclasticFlowChamber.toString() shouldBe expectedVisualization
    }
})