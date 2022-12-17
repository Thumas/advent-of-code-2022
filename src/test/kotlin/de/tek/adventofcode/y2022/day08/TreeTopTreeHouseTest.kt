package de.tek.adventofcode.y2022.day08

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class TreeGridTest : StringSpec({
    "Given non-rectangular grid, constructor throws IllegalArgumentException." {
        val sizes = arrayOf(arrayOf(1, 2, 3), arrayOf(1, 2))

        shouldThrow<IllegalArgumentException> { TreeGrid(sizes) }
    }

    "Given grid, getNumberOfVisibleTrees returns expected number." {
        forAll(
            row(arrayOf(arrayOf(1, 2, 3, 4)), 4),
            row(arrayOf(arrayOf(5, 5, 5, 5), arrayOf(5, 4, 4, 5), arrayOf(5, 5, 5, 5)), 10),
            row(
                arrayOf(
                    arrayOf(3, 0, 3, 7, 3),
                    arrayOf(2, 5, 5, 1, 2),
                    arrayOf(6, 5, 3, 3, 2),
                    arrayOf(3, 3, 5, 4, 9),
                    arrayOf(3, 5, 3, 9, 0)
                ), 21
            )
        ) { treeSizes, expectedNumberOfVisibleTrees ->
            TreeGrid(treeSizes).getNumberOfVisibleTrees() shouldBe expectedNumberOfVisibleTrees
        }
    }
})