package de.tek.adventofcode.y2022.util.algorithms

import de.tek.adventofcode.y2022.util.math.until
import java.math.BigInteger

/**
 * Returns a list of all subsets of this set.
 */
fun <T> Set<T>.subsets(): List<Set<T>> {
    val elements = this.toList()
    val numberOfElements = elements.size

    val result = mutableListOf<Set<T>>()

    val numberOfElementsInPowerSet = BigInteger.valueOf(2).pow(numberOfElements)
    for (bitSet in BigInteger.ZERO until numberOfElementsInPowerSet) {
        val combination = mutableSetOf<T>()

        for (bit in 0 until numberOfElements) {
            if (bitSet.testBit(bit)) {
                combination.add(elements[bit])
            }
        }
        result.add(combination)
    }

    return result
}

/**
 * Returns a Sequence of pairs:
 * - the first component is the beginning of a permutation of this set for which the given pruneBy function
 *   returns true; if it returns false for some combination, no combination starting with that combination is included
 *   in the result; if no pruneBy parameter is given, all permutations are returned
 * - the second component is as set of remaining elements of this set that are not part of the permutation.
 */
fun <T> Set<T>.permutations(pruneBy: (List<T>) -> Boolean = { _ -> false }): Sequence<Pair<List<T>, Set<T>>> =
    sequence {
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

                    if (permutation.size > 0 && !alreadyYielded[permutation.size - 1]) {
                        yield(Pair(permutation.toList(), remainingElements.toSet()))
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
                        yield(Pair(permutation.toList(), remainingElements.toSet()))
                    }
                    alreadyYielded[permutation.size - 1] = false

                    val freeElement = permutation.removeLast()
                    remainingElements.add(freeElement)
                }
            }
        }

        if (permutation.size > 0) {
            yield(Pair(permutation.toList(), remainingElements.toSet()))
        }
    }