package de.tek.adventofcode.y2022.util.arrays


fun <T> Array<T>.splitIndices(predicate: (T) -> Boolean): List<IntRange> {
    val result = mutableListOf<IntRange>()

    var currentSlice =  IntRange(0,-1)
    this.forEachIndexed { index, element ->
        currentSlice = if (predicate(element)) {
            result.add(currentSlice)
            IntRange(index+1,index)
        } else {
            IntRange(currentSlice.first, index)
        }
    }
    if (currentSlice.isEmpty() || currentSlice.first in indices) result.add(currentSlice)

    return result
}

fun List<IntRange>.merge(): List<IntRange> {
    if (isEmpty()) return this
    val result = mutableListOf<IntRange>()

    val sortedList = this.sortedBy { it.first }

    var i = 0
    while (i < size) {
        var currentRange = sortedList[i]
        while (i+1 < size && sortedList[i+1].first in currentRange) {
            val last = Integer.max(currentRange.last, sortedList[i + 1].last)
            currentRange = IntRange(currentRange.first, last)
            i++
        }
        result.add(currentRange)
        i++
    }

    return result
}
