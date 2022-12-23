package de.tek.adventofcode.y2022.day14

import de.tek.adventofcode.y2022.util.math.Point
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class CaveTest : StringSpec({
    val rockPositionsFromExample = listOf(
        498 to 4,
        498 to 5,
        498 to 6,
        497 to 6,
        496 to 6,
        503 to 4,
        502 to 4,
        502 to 5,
        502 to 6,
        502 to 7,
        502 to 8,
        502 to 9,
        501 to 9,
        500 to 9,
        499 to 9,
        498 to 9,
        497 to 9,
        496 to 9,
        495 to 9,
        494 to 9
    ).map(::Point).toSet()

    "Given the example, toString returns the expected visualization." {
        val cave = Cave(Point(500, 0), rockPositionsFromExample)

        val expectedVisualization =
            """......+...
              |..........
              |..........
              |..........
              |....#...##
              |....#...#.
              |..###...#.
              |........#.
              |........#.
              |#########.
            """.trimMargin()

        cave.toString() shouldBe expectedVisualization
    }

    "Given rock below the sand source, the simulation counts 0 sand units." {
        val cave = Cave(Point(500, 0), setOf(Point(500, 1)))

        cave.runSimulation() shouldBe 0
    }

    "Given rock 2 cells below the sand source, the simulation counts 1 sand units." {
        val cave = Cave(Point(500, 0), setOf(Point(500, 2)))

        cave.runSimulation() shouldBe 0
    }

    /*
         +

       #####
       -->
         +
        ooo
       #####
     */
    "Given a line of rock with width 5 and 2 cells below the sand source, the simulation throws a SandOverflowException." {
        val rockPositions = listOf(498 to 2, 499 to 2, 500 to 2, 501 to 2, 502 to 2).map(::Point).toSet()
        val cave = Cave(Point(500, 0), rockPositions)

        shouldThrow<SandOverflowException> { cave.runSimulation() }
    }

    /*
         +


       #####
       -->
         +
         o
        ooo
       #####
     */
    "Given a line of rock with width 5 and 3 cells below the sand source, the simulation counts 4 sand units." {
        val rockPositions = listOf(498 to 3, 499 to 3, 500 to 3, 501 to 3, 502 to 3).map(::Point).toSet()
        val cave = Cave(Point(500, 0), rockPositions)

        cave.runSimulation() shouldBe 4
    }

    /*
         +



       #####
       -->
         +

         o
        ooo
       #####
     */
    "Given a line of rock with width 5 and 4 cells below the sand source, the simulation counts 4 sand units." {
        val rockPositions = listOf(498 to 4, 499 to 4, 500 to 4, 501 to 4, 502 to 4).map(::Point).toSet()
        val cave = Cave(Point(500, 0), rockPositions)

        cave.runSimulation() shouldBe 4
    }

    "Given the example, the simulation counts 24 sand units." {
        val cave = Cave(Point(500,0), rockPositionsFromExample)

        cave.runSimulation() shouldBe 24
    }
})

class RegolithReservoirTest : StringSpec({

})