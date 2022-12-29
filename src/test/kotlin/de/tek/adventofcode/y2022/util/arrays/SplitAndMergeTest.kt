package de.tek.adventofcode.y2022.util.arrays

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class SplitAndMergeTest : FunSpec({

    data class SplitIndicesTestData(val array: Array<Int>, val expectedResult: List<IntRange>)

    context("Given array and delimiter, splitIndices returns the correct IntRanges.") {
        withData(
            SplitIndicesTestData(
                arrayOf(0, 1, 2, 3, -1, -3, 51, 15, -1),
                listOf(0 to 3, 4 to 3, 6 to 7, 8 to 7).map { IntRange(it.first, it.second) }),
            SplitIndicesTestData(arrayOf(0, 1), listOf(IntRange(0, 1))),
            SplitIndicesTestData(arrayOf(-10, 1), listOf(IntRange(0, -1), IntRange(1, 1))),
            SplitIndicesTestData(arrayOf(1), listOf(IntRange(0, 0))),
            SplitIndicesTestData(arrayOf(-1), listOf(IntRange(0, -1), IntRange(1, 0))),

            ) { (array, expectedResult) ->
            array.splitIndices { it < 0 } shouldBe expectedResult
        }
    }

    data class MergeTestData(val ranges: List<IntRange>, val expectedResult: List<IntRange>)

    context("Given two overlapping IntRanges, merge returns an IntRange from the smallest element to the biggest element of both ranges.") {
        withData(
            MergeTestData(listOf(IntRange(0, 5), IntRange(4, 10)), listOf(IntRange(0, 10))),
            MergeTestData(listOf(IntRange(-1, 1), IntRange(1, 1)), listOf(IntRange(-1, 1))),
            MergeTestData(listOf(IntRange(10, 100), IntRange(5, 11)), listOf(IntRange(5, 100))),

            ) { (ranges, expectedResult) ->
            ranges.merge() shouldBe expectedResult
        }
    }

    context("Given two disjoint IntRanges, merge only sorts by the first element of the ranges.") {
        withData(
            MergeTestData(listOf(IntRange(0, 3), IntRange(4, 10)), listOf(IntRange(0, 3), IntRange(4, 10))),
            MergeTestData(listOf(IntRange(-1, 1), IntRange(2, 2)), listOf(IntRange(-1, 1), IntRange(2, 2))),
            MergeTestData(listOf(IntRange(10, 100), IntRange(5, 9)), listOf(IntRange(5, 9), IntRange(10, 100))),

            ) { (ranges, expectedResult) ->
            ranges.merge() shouldBe expectedResult
        }
    }
})