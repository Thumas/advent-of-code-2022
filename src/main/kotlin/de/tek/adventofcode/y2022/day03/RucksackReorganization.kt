package de.tek.adventofcode.y2022.day03

import de.tek.adventofcode.y2022.util.readInputLines

class ItemType(private val id: Char) {

    val priority: Int = when (id) {
        in CharRange('a', 'z') -> {
            1 + (id.code - 'a'.code)
        }

        in CharRange('A', 'Z') -> {
            27 + (id.code - 'A'.code)
        }

        else -> {
            throw IllegalArgumentException("Only letters are allowed as item type ids.")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ItemType) return false

        return (id == other.id)
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

class Rucksack(contentCode: String) {
    private val firstCompartment: List<ItemType>
    private val secondCompartment: List<ItemType>

    init {
        if (contentCode.length.mod(2) != 0) {
            throw IllegalArgumentException("Invalid content, both compartments must contain the same number of items.")
        }
        val compartmentSize = contentCode.length / 2

        firstCompartment = contentCode.take(compartmentSize).map { ItemType(it) }
        secondCompartment = contentCode.drop(compartmentSize).map { ItemType(it) }
    }

    fun determineSharedItemTypes() = firstCompartment intersect secondCompartment.toSet()

    fun getItemTypes(): Set<ItemType> = (firstCompartment + secondCompartment).toSet()
}

class Group(private val rucksacks: List<Rucksack>) {
    fun getCommonItemTypes() = rucksacks.map { it.getItemTypes() }.reduce { a, b -> a intersect b }
}

fun Sequence<Set<ItemType>>.sumPriorities() = this.flatten().sumOf { it.priority }

fun main() {
    val input = readInputLines(Rucksack::class)

    fun part1(input: List<String>) =
        input.asSequence().map { Rucksack(it).determineSharedItemTypes() }.sumPriorities()

    fun part2(input: List<String>) =
        input.asSequence().map { Rucksack(it) }.chunked(3).map { Group(it).getCommonItemTypes() }.sumPriorities()

    println("The sum of the priorities of the shared item types in each rucksack is ${part1(input)}.")
    println("The sum of the priorities of the shared item types in each group of elves is ${part2(input)}.")
}




