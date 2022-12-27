package de.tek.adventofcode.y2022.util.algorithms

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder

class SubsetsTest : StringSpec({

    "Given set of integers, combinations returns all subsets." {
        val set = setOf(1, 2, 3)

        set.subsets() shouldContainExactlyInAnyOrder listOf(
            emptySet(),
            setOf(1),
            setOf(2),
            setOf(3),
            setOf(1, 2),
            setOf(1, 3),
            setOf(2, 3),
            setOf(1, 2, 3)
        )
    }
})

class PermutationsTest : StringSpec({
    "Given set of integers, permutations returns all permutations of that set." {
        val set = setOf(3, 2, 1)

        set.permutations().map { it.first }.toList() shouldContainExactlyInAnyOrder listOf(
            listOf(1, 2, 3),
            listOf(1, 3, 2),
            listOf(2, 1, 3),
            listOf(2, 3, 1),
            listOf(3, 1, 2),
            listOf(3, 2, 1)
        )
    }

    "Given set of integers, permutations returns an empty set as second components." {
        val set = setOf(3, 2, 1)

        set.permutations().map { it.second }.toList() shouldContainExactlyInAnyOrder List(6) { emptySet() }
    }

    "Given set of integers and pruneBy as sum is bigger than x, permutations returns only expected permutations of the set." {
        forAll(
            row(
                setOf(3, 2, 1), 3, listOf(
                    listOf(1, 2),
                    listOf(1),
                    listOf(2, 1),
                    listOf(2),
                    listOf(3)
                )
            ),
            row(
                setOf(4, 3, 2, 1), 6, listOf(
                    listOf(1, 2, 3),
                    listOf(1, 3, 2),
                    listOf(2, 1, 3),
                    listOf(2, 3, 1),
                    listOf(3, 1, 2),
                    listOf(3, 2, 1),
                    listOf(1, 4),
                    listOf(4, 1),
                    listOf(2, 4),
                    listOf(4, 2),
                    listOf(4)
                )
            )
        ) { set, limit, expectedResult ->

            set.permutations { it.sum() > limit }.map { it.first }
                .toList() shouldContainExactlyInAnyOrder expectedResult
        }
    }

    "Given set of integers and pruneBy as sum is bigger than x, permutations returns as second components the elements" +
            "in the set that are not part of the permutation." {
                forAll(
                    row(
                        setOf(3, 2, 1), 3, listOf(
                            setOf(3),
                            setOf(2, 3),
                            setOf(3),
                            setOf(1, 3),
                            setOf(1, 2),
                        )
                    ),
                    row(
                        setOf(4, 3, 2, 1), 6, listOf(
                            emptySet(),
                            emptySet(),
                            emptySet(),
                            emptySet(),
                            emptySet(),
                            emptySet(),
                            setOf(2, 3),
                            setOf(2, 3),
                            setOf(1, 3),
                            setOf(1, 3),
                            setOf(1, 2, 3)
                        )
                    )
                ) { set, limit, expectedResult ->

                    set.permutations { it.sum() > limit }.map { it.second }
                        .toList() shouldContainExactlyInAnyOrder expectedResult
                }
            }

})