package de.tek.adventofcode.y2022.day08

import de.tek.adventofcode.y2022.util.math.Direction
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class TreeTest : StringSpec({
    "Given tree, then its initial scenic score is zero." {
        val tree = Tree(1)
        tree.getScenicScore() shouldBe 0
    }

    "Given two trees, then compareSizeAndSetVisibility returns true, iff the the first tree is bigger than the second tree." {
        forAll(
            row(1, true), row(2, false), row(3, false)
        ) { otherTreeSize, expectedResult ->
            val tree = Tree(2)
            val otherTree = Tree(otherTreeSize)

            tree.isBiggerThan(otherTree) shouldBe expectedResult
        }
    }

    "Given tree and call compareSizeAndSetVisibility for another tree, then the first trees initial scenic score is zero." {
        forAll(
            row(1), row(2), row(3),
        ) { otherTreeSize ->
            val tree = Tree(2)
            val otherTree = Tree(otherTreeSize)

            tree.isBiggerThan(otherTree)

            tree.getScenicScore() shouldBe 0
        }
    }

    "Given tree and call compareSizeAndSetVisibility for surrounding trees, then the first trees initial scenic score is 1." {
        forAll(
            row(1), row(2), row(3),
        ) { otherTreeSize ->
            val tree = Tree(2)
            val otherTree = Tree(otherTreeSize)

            Direction.values().forEach { tree.isBiggerThan(otherTree) }
            tree.getScenicScore() shouldBe 1
        }
    }

})

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

    "Given grid, getHighestScenicScore returns expected number." {
        forAll(
            row(arrayOf(arrayOf(1, 2, 3, 4)), 0),
            row(
                arrayOf(
                    arrayOf(5, 5, 5, 5),
                    arrayOf(5, 4, 4, 5),
                    arrayOf(5, 5, 5, 5)
                ),
                1
            ),
            row(
                arrayOf(
                    arrayOf(5, 5, 5, 5),
                    arrayOf(2, 3, 4, 5),
                    arrayOf(5, 5, 5, 5)
                ),
                2
            ),
            row(
                arrayOf(
                    arrayOf(3, 0, 3, 7, 3),
                    arrayOf(2, 5, 5, 1, 2),
                    arrayOf(6, 5, 3, 3, 2),
                    arrayOf(3, 3, 5, 4, 9),
                    arrayOf(3, 5, 3, 9, 0)
                ), 8
            )
        ) { treeSizes, expectedHighestScenicScore ->
            TreeGrid(treeSizes).getHighestScenicScore() shouldBe expectedHighestScenicScore
        }
    }
})