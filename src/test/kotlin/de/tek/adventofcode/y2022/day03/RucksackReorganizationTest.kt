package de.tek.adventofcode.y2022.day03

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe

class ItemTypeTest : StringSpec({
    "priority" {
        forAll(
            row('a', 1), // code == 97
            row('b', 2),
            row('y', 25),
            row('z', 26), // code ==122
            row('A', 27), // code = 65
            row('B', 28),
            row('F', 32),
            row('Z', 52)
        ) { char, expectedPriority ->
            ItemType(char).priority shouldBe expectedPriority
        }
    }
})

class RucksackTest : StringSpec({
    "determineSharedItemTypes" {
        listOf(
            Pair("vJrwpWtwJgWrhcsFMMfFFhFp", 'p'),
            Pair("jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL", 'L'),
            Pair("PmmdzqPrVvPwwTWBwg", 'P'),
            Pair("wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn", 'v'),
            Pair("ttgJtRGJQctTZtZT", 't'),
            Pair("CrZsJsPPZsGzwwsLwLmpwMDw", 's')
        ).map { (input, expectedChar) -> Pair(input, setOf(ItemType(expectedChar))) }
            .forAll { (input, expectedResult) ->
                Rucksack(input).determineSharedItemTypes() shouldBe expectedResult
            }
    }
})

fun main() {

}