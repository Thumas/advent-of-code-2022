package de.tek.adventofcode.y2022.util.algorithms

import de.tek.adventofcode.y2022.util.math.until
import java.math.BigInteger

fun <T> Set<T>.subsets(pruneBy: (Set<T>) -> Boolean = { _ -> false }): List<Set<T>> {
    val elements = this.toList()
    val numberOfElements = elements.size

    val result = mutableListOf<Set<T>>()

    val numberOfElementsInPowerSet = BigInteger.valueOf(2).pow(numberOfElements)
    for (bitSet in BigInteger.ZERO until numberOfElementsInPowerSet) {
        val combination = mutableSetOf<T>()

        ElementsInSubset@
        for (bit in 0 until numberOfElements) {
            if (pruneBy(combination)) break@ElementsInSubset

            if (bitSet.testBit(bit)) {
                combination.add(elements[bit])
            }
        }
        result.add(combination)
    }

    return result
}

fun <T> Set<T>.permutations(pruneBy: (List<T>) -> Boolean = { _ -> false }): Sequence<List<T>> = sequence {
    val set = this@permutations
    val remainingElements = set.toMutableSet()
    val stack = mutableListOf(remainingElements.toMutableList())
    val permutation = mutableListOf<T>()
    val alreadyYielded = Array(set.size) { false }

    while (stack.isNotEmpty()) {
        val elementsForCurrentPosition = stack.last()

        if (elementsForCurrentPosition.isNotEmpty()) {
            val element = elementsForCurrentPosition.removeLast()

            permutation.add(element)

            if (pruneBy(permutation)) {
                permutation.removeLast()

                if (!alreadyYielded[permutation.size - 1]) {
                    yield(permutation)
                    alreadyYielded[permutation.size - 1] = true
                }

                continue
            }
            alreadyYielded[permutation.size - 1] = false

            remainingElements.remove(element)
            stack.add(remainingElements.toMutableList())
        } else {
            stack.removeLast()

            if (permutation.size > 0) {
                if (!alreadyYielded[permutation.size - 1]) {
                    yield(permutation)
                }
                alreadyYielded[permutation.size - 1] = false

                val freeElement = permutation.removeLast()
                remainingElements.add(freeElement)
            }
        }
    }

    yield(permutation)
}