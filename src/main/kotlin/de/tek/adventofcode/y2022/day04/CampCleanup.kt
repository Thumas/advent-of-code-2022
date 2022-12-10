package de.tek.adventofcode.y2022.day04

import de.tek.adventofcode.y2022.util.readInputLines

class AssignmentPair(code: String) {
    private val sections = code.split(',').map { parseLimits(it) }.map { IntRange(it[0], it[1]) }

    private fun parseLimits(it: String) = it.split('-').map { limit -> limit.toInt() }.take(2)

    fun containsFullOverlap(): Boolean {
        val biggestSection = sections.maxBy { it.length() }
        return sections.all { biggestSection.contains(it.first) && biggestSection.contains(it.last) }
    }

    fun bothSectionsOverlap() = sections[0] overlaps sections[1] || sections[1] overlaps sections[0]

    private fun IntRange.length() = last - first

    private infix fun IntRange.overlaps(other: IntRange) = this.contains(other.first) || this.contains(other.last)
}

fun main() {
    val input = readInputLines(AssignmentPair::class)

    fun part1(input: List<String>) = input.map { AssignmentPair(it) }.count { it.containsFullOverlap() }
    fun part2(input: List<String>) = input.map { AssignmentPair(it) }.count { it.bothSectionsOverlap() }

    println("In ${part1(input)} assignment pairs one range fully contains the other.")
    println("In ${part2(input)} assignment pairs the ranges overlap.")
}