package de.tek.adventofcode.y2022.util

fun splitByBlankLines(input: List<String>) : List<List<String>> {
    val result = mutableListOf(mutableListOf<String>())
    for (line in input) {
        if (line.isBlank()) {
            result.add(mutableListOf())
        } else {
            result.last().add(line)
        }
    }
    return result
}