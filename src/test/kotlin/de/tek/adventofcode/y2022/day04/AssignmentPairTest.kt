package de.tek.adventofcode.y2022.day04

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class AssignmentPairTest : StringSpec({
    "containsFullOverlap" {
        forAll(
            row("2-4,6-8", false),
            row("2-3,4-5", false),
            row("5-7,7-9", false),
            row("2-8,3-7", true),
            row("6-6,4-6", true),
            row("2-6,4-8", false),

            ) { input, expectedResult ->
            AssignmentPair(input).containsFullOverlap() shouldBe expectedResult
        }
    }
    "bothSectionsOverlap" {
        forAll(
            row("2-4,6-8", false),
            row("2-3,4-5", false),
            row("5-7,7-9", true),
            row("2-8,3-7", true),
            row("6-6,4-6", true),
            row("2-6,4-8", true),
            row("23-54,22-55", true)

            ) { input, expectedResult ->
            AssignmentPair(input).bothSectionsOverlap() shouldBe expectedResult
        }
    }
})