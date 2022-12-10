package de.tek.adventofcode.y2022.day01

import de.tek.adventofcode.y2022.util.readInputLines

class CalorieList(input: List<String>) {
    private class Elf {
        private val food = mutableListOf<Int>()

        fun add(item: Int) {
            if (item < 0) {
                throw IllegalArgumentException("Food cannot have negative calories!")
            }
            food.add(item)
        }

        fun calculateTotal() = food.sum()
    }

    private val elves = mutableListOf<Elf>()

    init {
        var position = 0
        var elf = Elf()
        for (s in input) {
            if (s.isEmpty()) {
                elves.add(elf)

                position++
                elf = Elf()
            } else {
                try {
                    elf.add(s.toInt())
                } catch (e: NumberFormatException) {
                    throw IllegalArgumentException("Found illegal calorie in list: $s", e)
                }
            }
        }
        elves.add(elf)
    }

    fun getMaximumCalories(): Int = elves.map { it.calculateTotal() }.max()

    fun getTopThreeCaloriesSum() = elves.map { it.calculateTotal() }.sortedDescending().take(3).sum()
}

fun main() {
    val input = readInputLines(CalorieList::class)

    val maxCalories = CalorieList(input).getMaximumCalories()

    println("The maximum calories are $maxCalories.")

    val topThreeSum = CalorieList(input).getTopThreeCaloriesSum()

    println("The sum of the calories of the top three elves is $topThreeSum.")
}